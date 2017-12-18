package com.mcmacker4.javelin.gl.vertex

import com.mcmacker4.javelin.gl.GLObject
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import org.lwjgl.opengl.GL30.*


class VertexArrayObject(
        private val indicesVBO: VertexBufferObject,
        private val attributes: HashMap<Int, VertexAttribute>
) : GLObject() {

    override val id = glGenVertexArrays()
    val vertexCount = indicesVBO.length
    
    init {
        bind()
        indicesVBO.bind()
        attributes.forEach { index, attribute ->
            attribute.bind()
            glVertexAttribPointer(index, attribute.size, GL_FLOAT, attribute.normalized, attribute.stride, attribute.offset)
            glEnableVertexAttribArray(index)
            attribute.unbind()
        }
        unbind()
    }
    
    override fun bind() {
        glBindVertexArray(id)
        boundVAO = id
    }

    override fun unbind() {
        glBindVertexArray(0)
        boundVAO = 0
    }
    
    companion object {
        private var boundVAO = 0
    }

    override fun delete() {
        attributes.values.forEach { it.delete() }
        glDeleteVertexArrays(id)
    }

    override fun equals(other: Any?): Boolean {
        return other is VertexArrayObject && this.id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

}