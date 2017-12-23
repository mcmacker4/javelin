package com.mcmacker4.javelin.graphics

import com.mcmacker4.javelin.gl.frame.GLFrameBuffer
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*


class ShadowMap(resolution: Int) {
    
    val texture = GLTexture2D(resolution, resolution, GL_DEPTH_COMPONENT, type = GL_FLOAT)
    val buffer = GLFrameBuffer()
    
    init {
        buffer.bind()
        buffer.setTextureAttachment(GL_DEPTH_ATTACHMENT, texture)
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw Exception("Shadow Map Error: ${glGetError().toString(16)}")
        buffer.unbind()
    }
    
}