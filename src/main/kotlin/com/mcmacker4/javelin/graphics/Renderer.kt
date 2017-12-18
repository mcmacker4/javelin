package com.mcmacker4.javelin.graphics

import com.google.common.collect.MultimapBuilder
import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.Camera
import com.mcmacker4.javelin.gameobject.component.Light
import com.mcmacker4.javelin.gameobject.component.Material
import com.mcmacker4.javelin.gameobject.component.Mesh
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.util.unaryMinus
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.system.MemoryUtil
import kotlin.math.cos
import kotlin.math.sin


class Renderer(var shaderProgram: ShaderProgram) {

    private val renderableObjects = MultimapBuilder.hashKeys().hashSetValues().build<VertexArrayObject, GameObject>()
    private val lights = arrayListOf<GameObject>()

    private val tempMatrixBuffer = MemoryUtil.memAllocFloat(16)

    fun addGameObject(gameObject: GameObject) {
        if(gameObject.hasComponent<Mesh>()) {
            val mesh = gameObject.getComponent<Mesh>()!!
            renderableObjects.put(mesh.vao, gameObject)
        } else if(gameObject.hasComponent<Light>()) {
            lights.add(gameObject)
        }
    }
    
    fun removeGameObject(gameObject: GameObject) {
        if(gameObject.hasComponent<Mesh>()) {
            val mesh = gameObject.getComponent<Mesh>()!!
            renderableObjects.remove(mesh.vao, gameObject)
        } else if(gameObject.hasComponent<Light>()) {
            lights.remove(gameObject)
        }
    }
    
    private fun loadCameraMatrix(activeCamera: GameObject?) {
        activeCamera?.let {
            val camera = it.getComponent<Camera>() ?: return
            val projectionMatrix = Matrix4f().perspective(camera.fovy, Display.MAIN.aspect, camera.nearPlane, camera.farPlane)
            val viewMatrix = Matrix4f()
                    .rotateX(-it.rotation.x).rotateY(-it.rotation.y).rotateZ(-it.rotation.z)
                    .translate(-it.position)
            val cameraMatrix = Matrix4f()
            projectionMatrix.mul(viewMatrix, cameraMatrix)
            cameraMatrix.get(tempMatrixBuffer)
            shaderProgram.uniformMat4("cameraMatrix", tempMatrixBuffer)
            shaderProgram.uniform3f("viewPosition", it.position)
        }
    }
    
    private fun loadLights() {
        
        lights.forEachIndexed { i, gameObject ->
            val light = gameObject.getComponent<Light>()!!
            shaderProgram.uniform3f("lights[$i].position", gameObject.position)
            shaderProgram.uniform3f("lights[$i].color", light.color)
        }
        
        shaderProgram.uniform1i("lightCount", lights.size)
        
    }
    
    private fun loadMaterial(material: Material) {

        //Albedo
        glActiveTexture(GL_TEXTURE0)
        shaderProgram.uniformBool("useAlbedoMap", material.useAlbedoMap)
        if(material.useAlbedoMap && material.albedoMap != null) {
            material.albedoMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform3f("baseColor", material.baseColor!!)
        }
        
        //Normal
        glActiveTexture(GL_TEXTURE1)
        shaderProgram.uniformBool("useNormalMap", material.useNormalMap)
        if(material.normalMap != null) {
            material.normalMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
        }

        //Metallic
        glActiveTexture(GL_TEXTURE2)
        shaderProgram.uniformBool("useMetallicMap", material.useMetallicMap)
        if(material.metallicMap != null) {
            material.metallicMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform1f("metallic", material.metallic!!)
        }

        //Roughness
        glActiveTexture(GL_TEXTURE3)
        shaderProgram.uniformBool("useRoughnessMap", material.useRoughnessMap)
        if(material.roughnessMap != null) {
            material.roughnessMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform1f("roughness", material.roughness!!)
        }
        
    }
    
    private fun drawObject(gameObject: GameObject) {
        
        val mesh = gameObject.getComponent<Mesh>() ?: return
        
        loadMaterial(gameObject.getComponent() ?: Material.DEFAULT)

        gameObject.modelMatrix.get(tempMatrixBuffer)
        shaderProgram.uniformMat4("modelMatrix", tempMatrixBuffer)
        
        //Load normal matrix
        Matrix4f(gameObject.modelMatrix).invert().transpose().get(tempMatrixBuffer)
        shaderProgram.uniformMat4("normalMatrix", tempMatrixBuffer)

        glDrawArrays(GL_TRIANGLES, 0, mesh.vao.vertexCount)

    }

    fun render(activeCamera: GameObject?) {
        
        shaderProgram.start()

        //Load camera information
        loadCameraMatrix(activeCamera)
        
        //load lights
        loadLights()
        
        //For each VertexArrayObject
        renderableObjects.keySet().forEach { vao ->

            vao.bind()

            //For each Mesh with the same VertexArrayObject
            renderableObjects.get(vao).forEach gameObject@ {
                
                drawObject(it)
                
            }

        }
        
        shaderProgram.stop()

    }

}