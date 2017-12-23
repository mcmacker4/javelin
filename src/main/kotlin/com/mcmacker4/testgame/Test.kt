package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.cos
import kotlin.math.sin


class Test : Script() {

    override fun update(delta: Float) {
        parent.rotation.y += 1f * delta
        parent.position.x = sin(glfwGetTime()).toFloat() * 10f
    }
    
}