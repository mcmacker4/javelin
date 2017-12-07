package com.mcmacker4.javelin.gl.shader

import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*


class Shader(source: String, type: Int) {

    val id: Int = glCreateShader(type)
    
    init {
        glShaderSource(id, source)
        glCompileShader(id)
        checkStatus(GL_COMPILE_STATUS)
    }
    
    private fun checkStatus(status: Int) {
        if(glGetShaderi(id, status) != GL_TRUE)
            throw Exception("Could not compile shader: ${glGetShaderInfoLog(id)}")
    }

    fun delete() {
        glDeleteShader(id)
    }

    companion object {
        val VERTEX = GL_VERTEX_SHADER
        val FRAGMENT = GL_FRAGMENT_SHADER
    }

}