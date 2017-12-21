package com.mcmacker4.javelin.gl.texture

import com.mcmacker4.javelin.util.ImageData
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT
import org.lwjgl.opengl.GL11.glPixelStorei
import org.lwjgl.system.MemoryUtil.NULL


class GLTexture2D private constructor() : GLTexture() {
    
    constructor(width: Int, height: Int, format: Int, internalFormat: Int = format, type: Int = GL_UNSIGNED_BYTE) : this() {
        bind()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, NULL)
        unbind()
    }
    
    constructor(image: ImageData) : this() {
        bind()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        if (image.width and 3 != 0)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (image.width and 1))
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.width, image.height, 0, GL_RGB, GL_UNSIGNED_BYTE, image.pixels)
        unbind()
    }
    
    override fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    override fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
    
}