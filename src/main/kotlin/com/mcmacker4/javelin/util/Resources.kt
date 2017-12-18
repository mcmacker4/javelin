package com.mcmacker4.javelin.util

import com.mcmacker4.javelin.gl.shader.ShaderProgram
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryUtil
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.stream.Collectors
import org.lwjgl.system.MemoryStack.stackPush
import java.lang.Math.round
import kotlin.experimental.and


object Resources {
    
    private val loadedImages = arrayListOf<ByteBuffer>()
    
    fun loadShader(name: String) : ShaderProgram {
        val vertex = loadTextFile("shaders/$name.v.glsl")
        val fragment = loadTextFile("shaders/$name.f.glsl")
        return ShaderProgram(vertex, fragment)
    }
    
    fun loadTextFile(path: String) : String {
        ClassLoader.getSystemResourceAsStream(path)?.use {
            return it.bufferedReader()
                    .lines()
                    .collect(Collectors.joining("\n"))
        }
        throw FileNotFoundException(path)
    }
    
    fun loadImageData(path: String) : ImageData {
        
        val startTime = glfwGetTime()
        
        val imageBuffer = loadIntoByteBuffer(path)
        
        stackPush().use { stack ->

            val width = stack.mallocInt(1)
            val height = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            val image = stbi_load_from_memory(imageBuffer, width, height, channels, 3)
                ?: throw RuntimeException("Failed to load image: ${stbi_failure_reason()}")

            /**
             * Comment reason: Loading RGBA (4 channel) images will not work, that's why
             * 3 channels is specified in stbi_load.
             */
//            if(channels.get(0) == 4)
//                premultiplyAlpha(image, width.get(0), height.get(0))

            val imageData = ImageData(width.get(0), height.get(0), 3, image)
            
            val elapsedTime = Math.round((glfwGetTime() - startTime) * 1000)
            println("Loaded IMG: $path - ${channels.get(0)} channels ($elapsedTime ms)")
            
            return imageData

        }
    }

    private fun premultiplyAlpha(image: ByteBuffer, w: Int, h: Int) {
        val stride = w * 4
        for (y in 0 until h) {
            for (x in 0 until w) {
                val i = y * stride + x * 4
                val alpha = (image.get(i + 3) and (0xFF).toByte()) / 255.0f
                image.put(i + 0, round((image.get(i + 0) and 0xFF.toByte()) * alpha).toByte())
                image.put(i + 1, round((image.get(i + 1) and 0xFF.toByte()) * alpha).toByte())
                image.put(i + 2, round((image.get(i + 2) and 0xFF.toByte()) * alpha).toByte())
            }
        }
    }
    
    fun loadIntoByteBuffer(path: String) : ByteBuffer {
        
        ClassLoader.getSystemResourceAsStream(path)?.use { source ->
            Channels.newChannel(source).use { channel ->
                var buffer = MemoryUtil.memAlloc(8 * 1024) // 8KB initial size
                while (true) {
                    val bytes = channel.read(buffer)
                    if (bytes == -1) break
                    if (buffer.remaining() == 0)
                        buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 3 / 2) // 50%
                }
                buffer.flip()
                loadedImages.add(buffer)
                return buffer
            }
        }
        
        throw FileNotFoundException(path)
        
    }
    
    fun cleanUp() {
        loadedImages.forEach {
            stbi_image_free(it)
        }
    }
    
}