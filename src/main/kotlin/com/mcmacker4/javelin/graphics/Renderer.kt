package com.mcmacker4.javelin.graphics

import com.google.common.collect.MultimapBuilder
import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.*
import com.mcmacker4.javelin.gl.frame.GLFrameBuffer
import com.mcmacker4.javelin.gl.frame.RenderBuffer
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.util.Resources
import org.joml.Matrix3f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil


class Renderer {

    //Shaders
    private val shaderProgram = Resources.loadShader("shader")
    private val gbuffersShader = Resources.loadShader("gbuffers")
    private val shadowShader = Resources.loadShader("shadowmap")
    private val resultShader = Resources.loadShader("result")

    //RenderTextures
    private var albedoSpecularTexture = GLTexture2D(Display.MAIN.width, Display.MAIN.height, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)
    private var positionTexture = GLTexture2D(Display.MAIN.width, Display.MAIN.height, GL_RGB, GL_RGB16F, GL_FLOAT)
    private var normalTexture = GLTexture2D(Display.MAIN.width, Display.MAIN.height, GL_RGB, GL_RGB16F, GL_FLOAT)
    
    //RenderBuffers
    private val depthRB = RenderBuffer(Display.MAIN.width, Display.MAIN.height, GL_DEPTH24_STENCIL8)

    //FrameBuffers
    private val gbufferFB = GLFrameBuffer()
    private val drawBuffers = MemoryUtil.memAllocInt(4)

    //Game Objects
    private val renderableObjects = MultimapBuilder.hashKeys().hashSetValues().build<VertexArrayObject, GameObject>()
    private val lights = arrayListOf<GameObject>()
    private val MAX_LIGHTS = 10

    //Memory Buffers
    private val tempMatrix4Buffer = MemoryUtil.memAllocFloat(16)
    private val tempMatrix3Buffer = MemoryUtil.memAllocFloat(9)

    //Render quad
    private val renderQuad = ModelLoader.loadRenderQuad()
    
    //Shadow Maps
    private val shadowMaps = hashMapOf<GameObject, ShadowMap>()
    private val SHADOW_MAP_RESOLUTION = 4096
    private val SHADOW_MAPS_START = 5
    
    init {

        attachGBufferTextures()

        drawBuffers.put(intArrayOf(GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2))
        drawBuffers.flip()
        
        Display.MAIN.onResize(this::updateTextureResolution)

    }
    
    private fun attachGBufferTextures() {
        gbufferFB.bind()
        gbufferFB.setTextureAttachment(GL_COLOR_ATTACHMENT0, albedoSpecularTexture)
        gbufferFB.setTextureAttachment(GL_COLOR_ATTACHMENT1, normalTexture)
        gbufferFB.setTextureAttachment(GL_COLOR_ATTACHMENT2, positionTexture)
        gbufferFB.setRenderbufferAttachment(GL_DEPTH_STENCIL_ATTACHMENT, depthRB)
        gbufferFB.unbind()
    }
    
    private fun updateTextureResolution(width: Int, height: Int) {
        albedoSpecularTexture.delete()
        albedoSpecularTexture = GLTexture2D(width, height, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)
        positionTexture.delete()
        positionTexture = GLTexture2D(width, height, GL_RGB, GL_RGB16F, GL_FLOAT)
        normalTexture.delete()
        normalTexture = GLTexture2D(width, height, GL_RGB, GL_RGB16F, GL_FLOAT)
        attachGBufferTextures()
    }

    fun addGameObject(gameObject: GameObject) {
        if (gameObject.hasComponent<Mesh>()) {
            val mesh = gameObject.getComponent<Mesh>()!!
            renderableObjects.put(mesh.vao, gameObject)
        } else if (gameObject.hasComponent<Light>() && lights.size < MAX_LIGHTS) {
            lights.add(gameObject)
        }
    }

    fun removeGameObject(gameObject: GameObject) {
        if (gameObject.hasComponent<Mesh>()) {
            val mesh = gameObject.getComponent<Mesh>()!!
            renderableObjects.remove(mesh.vao, gameObject)
        } else if (gameObject.hasComponent<PointLight>()) {
            lights.remove(gameObject)
        }
    }

    private fun loadCameraMatrix(activeCamera: GameObject?, shader: ShaderProgram) {
        activeCamera?.let {
            val camera = it.getComponent<Camera>() ?: return
            camera.getCameraMatrix().get(tempMatrix4Buffer)
            shader.uniformMat4("cameraMatrix", tempMatrix4Buffer)
            shader.uniform3f("viewPosition", it.position)
        }
    }

    fun renderShadowMap(gameObject: GameObject) {
        val map = shadowMaps.getOrPut(gameObject, { ShadowMap(SHADOW_MAP_RESOLUTION) })
        
        map.buffer.bind()
        shadowShader.start()
        
        glViewport(0, 0, SHADOW_MAP_RESOLUTION, SHADOW_MAP_RESOLUTION)
        
        glEnable(GL_DEPTH_TEST)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        //Load light matrix
        gameObject.getComponent<SpotLight>()!!.getLightMatrix().get(tempMatrix4Buffer)
        shadowShader.uniformMat4("lightMatrix", tempMatrix4Buffer)
        
        drawAllObjects(shadowShader)

        glDisable(GL_DEPTH_TEST)
        
        shadowShader.stop()
        map.buffer.unbind()
    }
    
    fun renderShadowMaps() {
        lights.filter { it.hasComponent<SpotLight>() }.forEach(this::renderShadowMap)
    }
    
    private fun loadLights(shader: ShaderProgram) {
        
        var pointCount = 0
        fun point(gameObject: GameObject, light: PointLight) {
            shader.uniform3f("pointLights[$pointCount].position", gameObject.position)
            shader.uniform3f("pointLights[$pointCount].color", light.color)
            shader.uniform1f("pointLights[$pointCount].constant", light.constant)
            shader.uniform1f("pointLights[$pointCount].linear", light.linear)
            shader.uniform1f("pointLights[$pointCount].quadratic", light.quadratic)
            pointCount++
        }
        
        var spotCount = 0
        fun spot(gameObject: GameObject, light: SpotLight) {
            //Properties
            shader.uniform3f("spotLights[$spotCount].position", gameObject.position)
            shader.uniform3f("spotLights[$spotCount].color", light.color)
            shader.uniform1f("spotLights[$spotCount].constant", light.constant)
            shader.uniform1f("spotLights[$spotCount].linear", light.linear)
            shader.uniform1f("spotLights[$spotCount].quadratic", light.quadratic)
            shader.uniform1f("spotLights[$spotCount].angle", light.angle)
            shader.uniform3f("spotLights[$spotCount].direction", light.getLightDirection())
            
            //Shadow map
            glActiveTexture(GL_TEXTURE0 + SHADOW_MAPS_START + spotCount)
            shader.uniform1i("spotLights[$spotCount].shadowMap", SHADOW_MAPS_START + spotCount)
            shadowMaps[gameObject]!!.texture.bind()
            //Matrix
            light.getLightMatrix().get(tempMatrix4Buffer)
            shader.uniformMat4("spotLights[$spotCount].matrix", tempMatrix4Buffer)
            
            spotCount++
        }
        
        lights.forEach {
            val light = it.getComponent<Light>()
            when(light) {
                is PointLight -> point(it, light)
                is SpotLight -> spot(it, light)
            }
        }
        
        shader.uniform1i("spotLightCount", spotCount)
        shader.uniform1i("pointLightCount", pointCount)

    }

    private fun loadMaterial(material: Material) {

        //Albedo
        glActiveTexture(GL_TEXTURE0)
        shaderProgram.uniform1i("material.albedoMap", 0)
        shaderProgram.uniformBool("material.useAlbedoMap", material.useAlbedoMap)
        if (material.useAlbedoMap && material.albedoMap != null) {
            material.albedoMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform3f("material.baseColor", material.baseColor!!)
        }

        //Normal
        glActiveTexture(GL_TEXTURE1)
        shaderProgram.uniform1i("material.normalMap", 1)
        shaderProgram.uniformBool("material.useNormalMap", material.useNormalMap)
        if (material.normalMap != null) {
            material.normalMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
        }

        //Metallic
        glActiveTexture(GL_TEXTURE2)
        shaderProgram.uniform1i("material.metallicMap", 2)
        shaderProgram.uniformBool("material.useMetallicMap", material.useMetallicMap)
        if (material.metallicMap != null) {
            material.metallicMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform1f("material.metallic", material.metallic!!)
        }

        //Roughness
        glActiveTexture(GL_TEXTURE3)
        shaderProgram.uniform1i("material.roughnessMap", 3)
        shaderProgram.uniformBool("material.useRoughnessMap", material.useRoughnessMap)
        if (material.roughnessMap != null) {
            material.roughnessMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform1f("material.roughness", material.roughness!!)
        }

    }

    private fun drawObject(shader: ShaderProgram, gameObject: GameObject) {

        val mesh = gameObject.getComponent<Mesh>() ?: return

        loadMaterial(gameObject.getComponent() ?: Material.DEFAULT)

        gameObject.modelMatrix.get(tempMatrix4Buffer)
        shader.uniformMat4("modelMatrix", tempMatrix4Buffer)

        //Load normal matrix
        Matrix3f(gameObject.modelMatrix).invert().transpose().get(tempMatrix3Buffer)
        shader.uniformMat3("normalMatrix", tempMatrix3Buffer)

        glDrawElements(GL_TRIANGLES, mesh.vao.vertexCount, GL_UNSIGNED_INT, 0)

    }

    private fun drawAllObjects(shader: ShaderProgram) {
        //For each VertexArrayObject
        renderableObjects.keySet().forEach { vao ->
            vao.bind()
            //For each Mesh with the same VertexArrayObject
            renderableObjects.get(vao).forEach {
                drawObject(shader, it)
            }
        }
    }

    private fun renderGBuffers(activeCamera: GameObject?) {

        gbuffersShader.start()

        gbufferFB.bind()

        glViewport(0, 0, Display.MAIN.width, Display.MAIN.height)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE)
            throw Exception("Frame buffer is not complete (${status.toString(16)})")

        glDrawBuffers(drawBuffers)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        loadCameraMatrix(activeCamera, gbuffersShader)

        drawAllObjects(gbuffersShader)

        gbufferFB.unbind()

    }

    private fun loadGBuffersToShader() {
        //Albedo
        glActiveTexture(GL_TEXTURE0)
        resultShader.uniform1i("albedoSpecular", 0)
        albedoSpecularTexture.bind()
        //Normal
        glActiveTexture(GL_TEXTURE1)
        resultShader.uniform1i("normal", 1)
        normalTexture.bind()
        //Position
        glActiveTexture(GL_TEXTURE2)
        resultShader.uniform1i("position", 2)
        positionTexture.bind()
    }

    private fun renderResult(activeCamera: GameObject?) {


        glViewport(0, 0, Display.MAIN.width, Display.MAIN.height)
        glDisable(GL_DEPTH_TEST)
        glDisable(GL_CULL_FACE)

        resultShader.start()

        loadGBuffersToShader()
        loadCameraMatrix(activeCamera, resultShader)
        loadLights(resultShader)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        renderQuad.bind()
        glDrawElements(GL_TRIANGLES, renderQuad.vertexCount, GL_UNSIGNED_INT, 0)
        renderQuad.unbind()

        resultShader.stop()

    }

    fun renderDeferred(activeCamera: GameObject?) {
        renderShadowMaps()
        renderGBuffers(activeCamera)
        renderResult(activeCamera)
    }

    fun render(activeCamera: GameObject?) {

        shaderProgram.start()
        
        //Load camera information
        loadCameraMatrix(activeCamera, shaderProgram)

        //load lights
        loadLights(shaderProgram)

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        drawAllObjects(shaderProgram)

        shaderProgram.stop()

    }

}