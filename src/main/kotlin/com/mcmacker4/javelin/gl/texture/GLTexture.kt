package com.mcmacker4.javelin.gl.texture

import com.mcmacker4.javelin.gl.GLObject
import org.lwjgl.opengl.GL11.*


abstract class GLTexture : GLObject() {
    
    override val id: Int = glGenTextures()
    
    override fun delete() {
        glDeleteTextures(id)
    }
    
}