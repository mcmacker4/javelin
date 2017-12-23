package com.mcmacker4.javelin.gl.texture

import com.mcmacker4.javelin.util.ImageData
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11.GL_RGB
import org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT
import org.lwjgl.opengl.GL11.glPixelStorei
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.opengl.GL42.glTexStorage2D
import org.lwjgl.system.MemoryUtil.NULL


class GLTexture2D private constructor() : GLTexture() {
    
    constructor(width: Int, height: Int, format: Int, internalFormat: Int = format, type: Int = GL_UNSIGNED_BYTE, mipLevels: Int = 0) : this() {
        bind()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, if(mipLevels > 0) GL_NEAREST_MIPMAP_NEAREST else GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, if(mipLevels > 0) GL_NEAREST_MIPMAP_LINEAR else GL_NEAREST)
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, NULL)
        unbind()
    }
    
    constructor(image: ImageData, mipLevels: Int = 0) : this() {
        bind()

        if (image.width and 3 != 0)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (image.width and 1))
        
        if(mipLevels > 0) {
            glTexStorage2D(GL_TEXTURE_2D, mipLevels, GL_RGB8, image.width, image.height)
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, image.width, image.height, GL_RGB, GL_UNSIGNED_BYTE, image.pixels)
            glGenerateMipmap(GL_TEXTURE_2D)
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, image.width, image.height, 0, GL_RGB, GL_UNSIGNED_BYTE, image.pixels)

            val err = glGetError()
            if(err != GL_NO_ERROR)
                throw Exception("Texture Error: ${err.toString(16)}")
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, if(mipLevels > 0) GL_NEAREST_MIPMAP_NEAREST else GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, if(mipLevels > 0) GL_NEAREST_MIPMAP_LINEAR else GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        unbind()
    }
    
    override fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    override fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
    
}