package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.shader.ShaderProgram
import com.mcmacker4.javelin.graphics.Camera
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays


class Renderer(var shaderProgram: ShaderProgram, var camera: Camera) {
    
    fun draw(model: Model) {
        
        //Start shader
        shaderProgram.start()
        
        //Load uniforms
        shaderProgram.loadUniformMat4("cameraMatrix", camera.matrixBuffer)
        
        //Bind model
        model.vao.bind()
        
        //Draw Model
        glDrawArrays(GL_TRIANGLES, 0, model.vao.vertexCount)
        
        //unbind everything
        model.vao.unbind()
        shaderProgram.stop()
    }
    
}