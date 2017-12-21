package com.mcmacker4.javelin.gl.frame

import com.mcmacker4.javelin.gl.GLObject
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL30.*


class FrameBuffer : GLObject() {
    
    override val id: Int = glGenFramebuffers()

    override fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id)
    }

    override fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    override fun delete() {
        glDeleteFramebuffers(id)
    }
    
    fun setTextureAttachment(attachment: Int, texture: GLTexture2D) {
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture.id, 0)
    }

}