package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.shader.ShaderProgram
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays


class Renderer(var shaderProgram: ShaderProgram) {
    
    fun draw(model: Model) {
        shaderProgram.use()
        model.vao.bind()
        glDrawArrays(GL_TRIANGLES, 0, model.vao.vertexCount)
        model.vao.unbind()
        shaderProgram.stop()
    }
    
}