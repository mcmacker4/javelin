package com.mcmacker4.javelin.display

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil.NULL

class Display(
        var width: Int,
        var height: Int,
        var title: String,
        share: Display? = null) {
    
    val window: Long
    
    init {
        
        //Set Properties
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        
        //Create Window
        window = glfwCreateWindow(width, height, title, NULL, share?.window ?: NULL)
        if(window == NULL)
            throw IllegalStateException("Could not create Window.")
        
        //Center window on screen
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        glfwSetWindowPos(
                window,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        )
        
        glfwSetWindowSizeCallback(window) { _, width, height ->
            this.width = width
            this.height = height
            glViewport(0, 0, width, height)
        }
        
        glfwMakeContextCurrent(window)
        glfwShowWindow(window)
        
        GL.createCapabilities()
        
        glViewport(0, 0, width, height)
        
        glClearColor(0.3f, 0.6f, 0.9f, 1.0f)
        
    }
    
    fun update() {
        glfwSwapBuffers(window)
    }
    
    fun destroy() {
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)
    }
    
    fun close() {
        glfwSetWindowShouldClose(window, true)
    }

    fun willClose() = glfwWindowShouldClose(window)

}
