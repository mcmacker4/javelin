package com.mcmacker4.javelin.gl.shader

import com.mcmacker4.javelin.gl.GLObject
import com.mcmacker4.javelin.readFile
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*


class ShaderProgram(private val vertexShader: Shader, private val fragmentShader: Shader) {
    
    val id: Int = glCreateProgram()
    
    constructor(vSource: String, fSource: String)
            : this(Shader(vSource, Shader.VERTEX), Shader(fSource, Shader.FRAGMENT))
    
    init {
        glAttachShader(id, vertexShader.id)
        glAttachShader(id, fragmentShader.id)
        glLinkProgram(id)
        checkStatus(GL_LINK_STATUS)
        glValidateProgram(id)
        checkStatus(GL_VALIDATE_STATUS)
    }

    private fun checkStatus(status: Int) {
        if(glGetProgrami(id, status) != GL11.GL_TRUE)
            throw Exception(glGetProgramInfoLog(id))
    }

    fun use() {
        glUseProgram(id)
    }

    fun stop() {
        glUseProgram(0)
    }

    fun delete() {
        vertexShader.delete()
        fragmentShader.delete()
        glDeleteProgram(id)
    }
    
    companion object {
        
        fun load(name: String) : ShaderProgram {
            val vertexSource = readFile("shaders/$name.v.glsl")
            val fragmentSource = readFile("shaders/$name.f.glsl")
            return ShaderProgram(vertexSource, fragmentSource)
        }
        
    }

}