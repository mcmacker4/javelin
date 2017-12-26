package com.mcmacker4.javelin.display

import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.input.Mouse
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.system.MemoryUtil.NULL

class Display(
        var width: Int,
        var height: Int,
        title: String,
        share: Display? = null) {
    
    val window: Long
    
    var aspect: Float = width / height.toFloat()
        get() { return width / height.toFloat() }
    
    private val resizeListeners: ArrayList<(Int, Int) -> Unit> = arrayListOf()
    
    private var focused = true
    
    init {
        
        //Set Properties
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        
        //Create Window
        window = glfwCreateWindow(width, height, title, NULL, share?.window ?: NULL)
        if(window == NULL)
            throw IllegalStateException("Could not create Window.")
        
        //If it's the primary window (not shared)
        if(share == null) {
            
            Display.MAIN = this

            //Center window on screen
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - width) / 2,
                    (vidmode.height() - height) / 2
            )

            glfwMakeContextCurrent(window)
            glfwShowWindow(window)

            GL.createCapabilities()

            glfwSetWindowSizeCallback(window) { _, width, height ->
                this.width = width
                this.height = height

                glViewport(0, 0, width, height)
                
                resizeListeners.forEach { it(width, height) }
            }
            
            glfwSetWindowFocusCallback(window) { _, focused ->
                this.focused = focused
            }
            
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
            
            glfwSetCursorPos(window, width / 2.0, height / 2.0)
            
            glfwSwapInterval(0)
            
            glfwSetKeyCallback(window, Keyboard::keyboardCallback)
            glfwSetMouseButtonCallback(window, Mouse::mouseButtonCallback)
            glfwSetCursorPosCallback(window, Mouse::mousePosCallback)

            glViewport(0, 0, width, height)

            glClearColor(0f, 0f, 0f, 1.0f)

        }
        
    }
    
    fun update() {
        while(!focused){ glfwPollEvents() }
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
    
    fun onResize(listener: (Int, Int) -> Unit) {
        resizeListeners.add(listener)
    }
    
    fun setTitle(title: String) {
        glfwSetWindowTitle(window, title)
    }
    
    companion object {
        lateinit var MAIN: Display
    }

}
