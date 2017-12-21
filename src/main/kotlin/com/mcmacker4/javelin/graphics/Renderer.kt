package com.mcmacker4.javelin.graphics

import com.google.common.collect.MultimapBuilder
import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.*
import com.mcmacker4.javelin.gl.frame.FrameBuffer
import com.mcmacker4.javelin.gl.frame.RenderBuffer
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.util.Resources
import com.mcmacker4.javelin.util.unaryMinus
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.glDrawBuffers
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil


class Renderer {

    //Shaders
    private val shaderProgram = Resources.loadShader("shader")
    private val gbuffersShader = Resources.loadShader("gbuffers")
    private val resultShader = Resources.loadShader("result")

    //RenderTextures
    private val albedoSpecularTexture = GLTexture2D(Display.MAIN.width, Display.MAIN.height, GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE)
    private val positionTexture = GLTexture2D(Display.MAIN.width, Display.MAIN.height, GL_RGB, GL_RGB16F, GL_FLOAT)
    private val normalTexture = GLTexture2D(Display.MAIN.width, Display.MAIN.height, GL_RGB, GL_RGB16F, GL_FLOAT)
    
    //RenderBuffers
    private val depthRB = RenderBuffer(Display.MAIN.width, Display.MAIN.height, GL_DEPTH24_STENCIL8)

    //FrameBuffers
    private val gbufferFB = FrameBuffer()
    private val drawBuffers = MemoryUtil.memAllocInt(4)

    //Game Objects
    private val renderableObjects = MultimapBuilder.hashKeys().hashSetValues().build<VertexArrayObject, GameObject>()
    private val lights = arrayListOf<GameObject>()

    //Memory Buffers
    private val tempMatrix4Buffer = MemoryUtil.memAllocFloat(16)
    private val tempMatrix3Buffer = MemoryUtil.memAllocFloat(9)

    init {

        gbufferFB.bind()
        gbufferFB.setTextureAttachment(GL_COLOR_ATTACHMENT0, albedoSpecularTexture)
        gbufferFB.setTextureAttachment(GL_COLOR_ATTACHMENT1, normalTexture)
        gbufferFB.setTextureAttachment(GL_COLOR_ATTACHMENT2, positionTexture)
        gbufferFB.setRenderbufferAttachment(GL_DEPTH_STENCIL_ATTACHMENT, depthRB)
        gbufferFB.unbind()

        drawBuffers.put(intArrayOf(GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2))
        drawBuffers.flip()

    }

    //Render quad
    private val renderQuad = ModelLoader.loadRenderQuad()

    fun addGameObject(gameObject: GameObject) {
        if (gameObject.hasComponent<Mesh>()) {
            val mesh = gameObject.getComponent<Mesh>()!!
            renderableObjects.put(mesh.vao, gameObject)
        } else if (gameObject.hasComponent<Light>()) {
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
            val projectionMatrix = Matrix4f().perspective(camera.fovy, Display.MAIN.aspect, camera.nearPlane, camera.farPlane)
            val viewMatrix = Matrix4f()
                    .rotateX(-it.rotation.x).rotateY(-it.rotation.y).rotateZ(-it.rotation.z)
                    .translate(-it.position)
            val cameraMatrix = Matrix4f()
            projectionMatrix.mul(viewMatrix, cameraMatrix)
            cameraMatrix.get(tempMatrix4Buffer)
            shader.uniformMat4("cameraMatrix", tempMatrix4Buffer)
            shader.uniform3f("viewPosition", it.position)
        }
    }

    private fun loadLights(shader: ShaderProgram) {

        //Point Lights
        val pointLights = lights.filter { it.hasComponent<PointLight>() }
        pointLights.forEachIndexed { i, gameObject ->
            val light = gameObject.getComponent<PointLight>()!!
            shader.uniform3f("pointLights[$i].position", gameObject.position)
            shader.uniform3f("pointLights[$i].color", light.color)
            shader.uniform1f("pointLights[$i].constant", light.constant)
            shader.uniform1f("pointLights[$i].linear", light.linear)
            shader.uniform1f("pointLights[$i].quadratic", light.quadratic)
        }
        shader.uniform1i("pointLightCount", pointLights.size)

        //Spot Lights
        val spotLights = lights.filter { it.hasComponent<SpotLight>() }
        spotLights.forEachIndexed { i, gameObject ->
            val light = gameObject.getComponent<SpotLight>()!!
            shader.uniform3f("spotLights[$i].position", gameObject.position)
            shader.uniform3f("spotLights[$i].color", light.color)
            shader.uniform1f("spotLights[$i].constant", light.constant)
            shader.uniform1f("spotLights[$i].linear", light.linear)
            shader.uniform1f("spotLights[$i].quadratic", light.quadratic)
            shader.uniform1f("spotLights[$i].angle", light.angle)
            shader.uniform3f("spotLights[$i].direction", light.getLightDirection())
        }
        shader.uniform1i("spotLightCount", spotLights.size)

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

    private fun drawObject(gameObject: GameObject) {

        val mesh = gameObject.getComponent<Mesh>() ?: return

        loadMaterial(gameObject.getComponent() ?: Material.DEFAULT)

        gameObject.modelMatrix.get(tempMatrix4Buffer)
        shaderProgram.uniformMat4("modelMatrix", tempMatrix4Buffer)

        //Load normal matrix
        Matrix3f(gameObject.modelMatrix).invert().transpose().get(tempMatrix3Buffer)
        shaderProgram.uniformMat3("normalMatrix", tempMatrix3Buffer)

        glDrawElements(GL_TRIANGLES, mesh.vao.vertexCount, GL_UNSIGNED_INT, 0)

    }

    private fun drawAllObjects() {
        //For each VertexArrayObject
        renderableObjects.keySet().forEach { vao ->
            vao.bind()
            //For each Mesh with the same VertexArrayObject
            renderableObjects.get(vao).forEach {
                drawObject(it)
            }
        }
    }

    private fun renderGBuffers(activeCamera: GameObject?) {

        gbuffersShader.start()

        gbufferFB.bind()

        val status = glCheckFramebufferStatus(GL_FRAMEBUFFER)
        if (status != GL_FRAMEBUFFER_COMPLETE)
            throw Exception("Frame buffer is not complete (${status.toString(16)})")

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        glDrawBuffers(drawBuffers)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        loadCameraMatrix(activeCamera, gbuffersShader)

        drawAllObjects()

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
        renderGBuffers(activeCamera)
        renderResult(activeCamera)
    }

    fun render(activeCamera: GameObject?) {

        shaderProgram.start()

        //Load camera information
        loadCameraMatrix(activeCamera, shaderProgram)

        //load lights
        loadLights(shaderProgram)

        drawAllObjects()

        shaderProgram.stop()

    }

}