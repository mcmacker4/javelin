package com.mcmacker4.javelin.graphics

import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.model.Entity
import com.mcmacker4.javelin.model.Model
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer


@Deprecated("Old Entity Renderer")
class RendererOld(var shaderProgram: ShaderProgram, var camera: Camera) {
    
    val matrixBuffer: FloatBuffer = MemoryUtil.memAllocFloat(16)
    
    private val entities = hashMapOf<Model, ArrayList<Entity>>()
    
    fun addEntity(entity: Entity) {
        if(entities.containsKey(entity.model)) {
            entities[entity.model]?.add(entity)
        } else {
            val list = arrayListOf(entity)
            entities[entity.model] = list
        }
    }
    
    fun removeEntity(entity: Entity) {
        if(entities.containsKey(entity.model))
            entities[entity.model]!!.remove(entity)
    }
    
    fun draw() {
        
        //Start Shader
        shaderProgram.start()
        
        //Load Camera matrix
        camera.matrix.get(matrixBuffer)
        shaderProgram.loadUniformMat4("cameraMatrix", matrixBuffer)
        
        //For each model
        for(model in entities.keys) {
            
            //Bind model
            model.vao.bind()
            
            //For each entity
            entities[model]?.forEach { entity ->
                
                entity.modelMatrix.get(matrixBuffer)
                shaderProgram.loadUniformMat4("modelMatrix", matrixBuffer)
                
                glDrawArrays(GL_TRIANGLES, 0, model.vao.vertexCount)
                
            }
            
            model.vao.unbind()
            
        }
        
        shaderProgram.stop()
        
    }
    
}