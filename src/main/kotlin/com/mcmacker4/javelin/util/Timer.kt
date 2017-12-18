package com.mcmacker4.javelin.util

import com.mcmacker4.javelin.display.Display
import org.lwjgl.glfw.GLFW.glfwGetTime


class Timer {
    
    var now = glfwGetTime()
        private set
    var delta = 0f
        private set
    
    private var last = now
    private var lastFPS = now
    private var frameCount = 0
    
    fun update() : Float {
        now = glfwGetTime()
        delta = (now - last).toFloat()
        last = now
        
        if(now - lastFPS >= 1.0) {
            //println("FPS: $frameCount")
            Display.MAIN.setTitle("FPS: $frameCount")
            lastFPS = now
            frameCount = 0
        }
        
        frameCount++
        return delta
    }
    
}