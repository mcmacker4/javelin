package com.mcmacker4.javelin.gl.vertex

import com.mcmacker4.javelin.gl.GLObject
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*


class VertexArrayObject(
        private val attributes: HashMap<Int, VertexAttribute>)
    : GLObject() {

    override val id = glGenVertexArrays()
    val vertexCount = attributes[0]?.count ?: attributes.values.first().count
    
    init {
        bind()
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