package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.vertex.VertexArrayObject
import com.mcmacker4.javelin.gl.vertex.VertexAttribute
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW
import org.lwjgl.system.MemoryUtil


object ModelLoader {
    
    fun loadMesh(vertices: FloatArray) : VertexArrayObject {
        val buffer = MemoryUtil.memAllocFloat(vertices.size).put(vertices)
        buffer.flip()
        val vbo = VertexAttribute(buffer, GL_ARRAY_BUFFER, GL_STATIC_DRAW, 3)
        return VertexArrayObject(hashMapOf(Pair(VertexAttribute.ATTRIB_POSITION, vbo)))
    }
    
}