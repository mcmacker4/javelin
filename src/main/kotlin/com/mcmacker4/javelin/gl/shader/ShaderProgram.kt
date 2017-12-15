package com.mcmacker4.javelin.gl.shader

import org.joml.Vector3f
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
    
    fun location(name: String) : Int {
        if(uniformLocations.containsKey(name))
            return uniformLocations[name] ?: -1
        val loc = glGetUniformLocation(id, name)
        uniformLocations[name] = loc
        return loc
    }
    
    fun uniformBool(name: String, value: Boolean) {
        glUniform1i(location(name), if(value) 1 else 0)
    }
    
    fun uniform1f(name: String, value: Float) {
        glUniform1f(location(name), value)
    }
    
    fun uniform3f(name: String, vector: Vector3f) {
        glUniform3f(location(name), vector.x, vector.y, vector.z)
    }
    
    fun uniformMat4(name: String, matrix: FloatBuffer) {
        glUniformMatrix4fv(location(name), false, matrix)
    }

    fun delete() {
        vertexShader.delete()
        fragmentShader.delete()
        glDeleteProgram(id)
    }
    
}