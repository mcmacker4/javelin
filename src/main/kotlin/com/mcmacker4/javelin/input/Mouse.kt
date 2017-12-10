package com.mcmacker4.javelin.input

import org.lwjgl.glfw.GLFW.GLFW_PRESS


object Mouse {
    
    private val buttons = hashMapOf<Int, Boolean>()
    private val buttonUpListeners = arrayListOf<(button: Int, mods: Int) -> Unit>()
    private val buttonDownListeners = arrayListOf<(button: Int, mods: Int) -> Unit>()
    private val cursorPosListeners = arrayListOf<(dx: Double, dy: Double) -> Unit>()
    
    var xpos: Double = 0.0
        private set
    var ypos: Double = 0.0
        private set
    
    fun isButtonDown(button: Int) : Boolean {
        return buttons[button] ?: false
    }
    
    fun onButtonDown(listener: (button: Int, mods: Int) -> Unit) {
        buttonDownListeners.add(listener)
    }
    
    fun onButtonUp(listener: (button: Int, mods: Int) -> Unit) {
        buttonUpListeners.add(listener)
    }
    
    fun removeButtonListener(listener: (button: Int, mods: Int) -> Unit) {
        buttonUpListeners.remove(listener)
        buttonDownListeners.remove(listener)
    }
    
    fun removePosListener(listener: (dx: Double, dy: Double) -> Unit) {
        cursorPosListeners.remove(listener)
    }
    
    fun onMove(listener: (dx: Double, dy: Double) -> Unit) {
        cursorPosListeners.add(listener)
    }
    
    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if(action == GLFW_PRESS) {
            buttons[button] = true
            buttonUpListeners.forEach { it(button, mods) }
        } else {
            buttons[button] = false
            buttonDownListeners.forEach { it(button, mods) }
        }
    }
    
    fun mousePosCallback(window: Long, x: Double, y: Double) {
        val dx = x - xpos
        val dy = y - ypos
        xpos = x
        ypos = y
        cursorPosListeners.forEach { it(dx, dy) }
    }
    
}