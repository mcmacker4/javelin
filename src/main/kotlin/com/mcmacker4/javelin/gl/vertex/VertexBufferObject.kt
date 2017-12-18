package com.mcmacker4.javelin.gl.vertex

import com.mcmacker4.javelin.gl.GLObject
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer
import java.nio.IntBuffer


open class VertexBufferObject private constructor(
        val target: Int,
        val usage: Int,
        val type: Int,
        val length: Int
) : GLObject() {
    
    override val id = glGenBuffers()
    
    constructor(data: FloatBuffer, target: Int, usage: Int) : this(target, usage, GL_FLOAT, data.capacity()) {
        bind()
        setData(data)
        unbind()
    }

    constructor(data: IntBuffer, target: Int, usage: Int) : this(target, usage, GL_FLOAT, data.capacity()) {
        bind()
        setData(data)
        unbind()
    }
    
    fun setData(data: FloatBuffer) {
        checkBoundState()
        glBufferData(target, data, usage)
    }

    fun setData(data: IntBuffer) {
        checkBoundState()
        glBufferData(target, data, usage)
    }
    
    fun setSubData(data: FloatBuffer, offset: Long) {
        checkBoundState()
        glBufferSubData(target, offset, data)
    }

    fun setSubData(data: IntBuffer, offset: Long) {
        checkBoundState()
        glBufferSubData(target, offset, data)
    }
    
    private fun checkBoundState() {
        if(boundVBO != id)
            throw IllegalStateException("VBO missmatch: Currently bound VBO " +
                    "is not the same as the one you are trying to modify.")
    }
    
    final override fun bind() {
        glBindBuffer(target, id)
        boundVBO = id
    }

    final override fun unbind() {
        glBindBuffer(target, 0)
        boundVBO = 0
    }

    final override fun delete() {
        glDeleteBuffers(id)
    }
    
    companion object {
        private var boundVBO = 0
    }

}