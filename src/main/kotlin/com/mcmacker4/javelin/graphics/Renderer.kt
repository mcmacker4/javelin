package com.mcmacker4.javelin.graphics

import com.google.common.collect.MultimapBuilder
import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.Camera
import com.mcmacker4.javelin.gameobject.component.Material
import com.mcmacker4.javelin.gameobject.component.Mesh
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.util.unaryMinus
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.system.MemoryUtil


class Renderer(var shaderProgram: ShaderProgram) {

    private val renderableObjects = MultimapBuilder.hashKeys().hashSetValues().build<VertexArrayObject, GameObject>()

    private val tempMatrixBuffer = MemoryUtil.memAllocFloat(16)

    fun addGameObject(gameObject: GameObject) {
        val mesh = gameObject.getComponent<Mesh>() ?: return
        renderableObjects.put(mesh.vao, gameObject)
    }
    
    fun removeGameObject(gameObject: GameObject) {
        val mesh = gameObject.getComponent<Mesh>() ?: return
        renderableObjects.remove(mesh.vao, gameObject)
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
        }
    }

    fun render(activeCamera: GameObject?) {
        
        shaderProgram.start()

        //Load camera information
        loadCameraMatrix(activeCamera)
        
        //For each VertexArrayObject
        renderableObjects.keySet().forEach { vao ->

            vao.bind()

            //For each Mesh with the same VertexArrayObject
            renderableObjects.get(vao).forEach gameObject@ { gameObject ->
                
                val mesh = gameObject.getComponent<Mesh>() ?: return@gameObject
                
                val material = gameObject.getComponent<Material>()
                if(material != null) {
                    shaderProgram.uniformBool("useColorTexture", true)
                    glActiveTexture(GL_TEXTURE0)
                    material.texture.bind()
                } else {
                    shaderProgram.uniformBool("useColorTexture", false)
                }
                
                gameObject.modelMatrix.get(tempMatrixBuffer)
                shaderProgram.uniformMat4("modelMatrix", tempMatrixBuffer)
                
                glDrawArrays(GL_TRIANGLES, 0, mesh.vao.vertexCount)

                material?.texture?.unbind()

            }

        }
        
        shaderProgram.stop()

    }

}