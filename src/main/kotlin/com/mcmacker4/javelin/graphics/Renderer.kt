package com.mcmacker4.javelin.graphics

import com.google.common.collect.MultimapBuilder
import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.Camera
import com.mcmacker4.javelin.gameobject.component.Mesh
import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.system.MemoryUtil


class Renderer(var shaderProgram: ShaderProgram) {

    private val renderableObjects = MultimapBuilder.hashKeys().hashSetValues().build<VertexArrayObject, Mesh>()

    private val tempMatrixBuffer = MemoryUtil.memAllocFloat(16)

    fun addGameObject(gameObject: GameObject) {
        val mesh = gameObject.getComponent<Mesh>() ?: return
        renderableObjects.put(mesh.vao, mesh)
    }
    
    fun removeGameObject(gameObject: GameObject) {
        val mesh = gameObject.getComponent<Mesh>() ?: return
        renderableObjects.remove(mesh.vao, mesh)
    }
    
    private fun loadCameraMatrix(activeCamera: GameObject?) {
        activeCamera?.let {
            val camera = it.getComponent<Camera>() ?: return
            val center = Vector3f(0f, 0f, -1f)
                    .rotateX(it.rotation.x)
                    .rotateY(it.rotation.y)
                    .rotateZ(it.rotation.z)
                    .add(it.position)
            val cameraMatrix = Matrix4f()
                    .setPerspective(camera.fovy, Display.MAIN.aspect, camera.nearPlane, camera.farPlane)
                    .lookAt(it.position, center, Vector3f(0f, 1f, 0f))
            cameraMatrix.get(tempMatrixBuffer)
            shaderProgram.loadUniformMat4("cameraMatrix", tempMatrixBuffer)
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
            renderableObjects.get(vao).forEach { mesh ->

                mesh.parent.modelMatrix.get(tempMatrixBuffer)
                shaderProgram.loadUniformMat4("modelMatrix", tempMatrixBuffer)
                
                glDrawArrays(GL_TRIANGLES, 0, mesh.vao.vertexCount)

            }

        }
        
        shaderProgram.stop()

    }

}