package com.mcmacker4.javelin.input

import org.lwjgl.glfw.GLFW.*


object Keyboard {
    
    private val keys = hashMapOf<Int, Boolean>()
    private val keyDownListeners = arrayListOf<(key: Int, mods: Int)-> Unit>()
    private val keyUpListeners = arrayListOf<(key: Int, mods: Int)-> Unit>()
    
    fun isKeyDown(key: Int) : Boolean {
        return keys[key] ?: false
    }
    
    fun onKeyDown(listener: (key: Int, mods: Int) -> Unit) {
        keyDownListeners.add(listener)
    }
    
    fun onKeyUp(listener: (key: Int, mods: Int) -> Unit) {
        keyUpListeners.add(listener)
    }
    
    fun removeListener(listener: (key: Int, mods: Int) -> Unit) {
        keyDownListeners.remove(listener)
        keyUpListeners.remove(listener)
    }
    
    fun keyboardCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if(action == GLFW_RELEASE) {
            keys[key] = false
            keyUpListeners.forEach { it(key, mods) }
        } else if(action == GLFW_PRESS) {
            keys[key] = true
            keyDownListeners.forEach { it(key, mods) }
        }
    }
    
}