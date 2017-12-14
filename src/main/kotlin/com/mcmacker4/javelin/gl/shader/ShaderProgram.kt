package com.mcmacker4.javelin.gl.shader

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import java.nio.FloatBuffer


class ShaderProgram(private val vertexShader: Shader, private val fragmentShader: Shader) {
    
    private val id: Int = glCreateProgram()
    
    private val uniformLocations = hashMapOf<String, Int>()
    
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

    fun start() {
        glUseProgram(id)
    }

    fun stop() {
        glUseProgram(0)
    }
    
    fun loadUniformMat4(name: String, matrix: FloatBuffer) {
        val location = uniformLocations[name] ?: glGetUniformLocation(id, name)
        if(location != -1) {
            uniformLocations[name] = location
            glUniformMatrix4fv(location, false, matrix)
        }
    }

    fun delete() {
        vertexShader.delete()
        fragmentShader.delete()
        glDeleteProgram(id)
    }
    
}