package com.mcmacker4.javelin.gl.vertex

import java.nio.FloatBuffer


class VertexAttribute(
        data: FloatBuffer,
        target: Int,
        usage: Int,
        val size: Int,
        val normalized: Boolean = false,
        val stride: Int = 0,
        val offset: Long = 0)
    : VertexBufferObject(data, target, usage) {
    
    val count = length / size
    
    companion object {
        val ATTRIB_POSITION = 0
        val ATTRIB_NORMAL = 1
        val ATTRIB_TEXTURE_COORD = 2
        val ATTRIB_COLOR = 3
    }
    
}