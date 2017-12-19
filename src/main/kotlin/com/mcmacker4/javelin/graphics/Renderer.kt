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
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.system.MemoryUtil


class Renderer(var shaderProgram: ShaderProgram) {

    private val renderableObjects = MultimapBuilder.hashKeys().hashSetValues().build<VertexArrayObject, GameObject>()
    private val lights = arrayListOf<GameObject>()

    private val tempMatrix4Buffer = MemoryUtil.memAllocFloat(16)
    private val tempMatrix3Buffer = MemoryUtil.memAllocFloat(9)

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
            cameraMatrix.get(tempMatrix4Buffer)
            shaderProgram.uniformMat4("cameraMatrix", tempMatrix4Buffer)
            shaderProgram.uniform3f("viewPosition", it.position)
        }
    }
    
    private fun loadLights() {
        
        lights.forEachIndexed { i, gameObject ->
            val light = gameObject.getComponent<Light>()!!
            shaderProgram.uniform3f("lights[$i].position", gameObject.position)
            shaderProgram.uniform3f("lights[$i].color", light.color)
            shaderProgram.uniform1f("lights[$i].constant", light.constant)
            shaderProgram.uniform1f("lights[$i].linear", light.linear)
            shaderProgram.uniform1f("lights[$i].quadratic", light.quadratic)
        }
        
        shaderProgram.uniform1i("lightCount", lights.size)
        
    }
    
    private fun loadMaterial(material: Material) {

        //Albedo
        glActiveTexture(GL_TEXTURE0)
        shaderProgram.uniform1i("material.albedoMap", 0)
        shaderProgram.uniformBool("material.useAlbedoMap", material.useAlbedoMap)
        if(material.useAlbedoMap && material.albedoMap != null) {
            material.albedoMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform3f("material.baseColor", material.baseColor!!)
        }
        
        //Normal
        glActiveTexture(GL_TEXTURE1)
        shaderProgram.uniform1i("material.normalMap", 1)
        shaderProgram.uniformBool("material.useNormalMap", material.useNormalMap)
        if(material.normalMap != null) {
            material.normalMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
        }

        //Metallic
        glActiveTexture(GL_TEXTURE2)
        shaderProgram.uniform1i("material.metallicMap", 2)
        shaderProgram.uniformBool("material.useMetallicMap", material.useMetallicMap)
        if(material.metallicMap != null) {
            material.metallicMap.bind()
        } else {
            glBindTexture(GL_TEXTURE_2D, 0)
            shaderProgram.uniform1f("material.metallic", material.metallic!!)
        }

        //Roughness
        glActiveTexture(GL_TEXTURE3)
        shaderProgram.uniform1i("material.roughnessMap", 3)
        shaderProgram.uniformBool("material.useRoughnessMap", material.useRoughnessMap)
        if(material.roughnessMap != null) {
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
            renderableObjects.get(vao).forEach {
                
                drawObject(it)
                
            }

        }
        
        shaderProgram.stop()

    }

}