package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.vertex.VertexArrayObject

@Deprecated("Use VertexArrayObject directly.")
class Model(val vao: VertexArrayObject) {
    
    fun delete() {
        vao.delete()
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Model)
            return false
        return vao.id == other.vao.id
    }

    override fun hashCode(): Int {
        return vao.id
    }

}