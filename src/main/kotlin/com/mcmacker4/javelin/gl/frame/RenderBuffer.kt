package com.mcmacker4.javelin.gl.frame

import com.mcmacker4.javelin.gl.GLObject
import org.lwjgl.opengl.GL30.*


class RenderBuffer(width: Int, height: Int, format: Int) : GLObject() {
    
    override val id: Int = glGenRenderbuffers()
    
    init {
        bind()
        glRenderbufferStorage(GL_RENDERBUFFER, format, width, height)
        unbind()
    }

    override fun bind() {
        glBindRenderbuffer(GL_RENDERBUFFER, id)
    }

    override fun unbind() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0)
    }

    override fun delete() {
        glDeleteRenderbuffers(id)
    }

}