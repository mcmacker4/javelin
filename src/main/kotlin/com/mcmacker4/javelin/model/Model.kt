package com.mcmacker4.javelin.model

import com.mcmacker4.javelin.gl.vertex.VertexArrayObject


class Model(val vao: VertexArrayObject) {
    
    fun delete() {
        vao.delete()
    }
    
}