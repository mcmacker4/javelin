package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.cos
import kotlin.math.sin


class CirclePath : Script() {

    override fun update(delta: Float) {
        val time = glfwGetTime().toFloat()
        parent.position.set(sin(time) * 3f, cos(time) * 3f, 3f)
    }
    
}