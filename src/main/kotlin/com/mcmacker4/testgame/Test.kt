package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.math.cos
import kotlin.math.sin


class Test : Script() {
    
    val speed = Math.random() * 2 - 1

    override fun update(delta: Float) {
        val time = glfwGetTime().toFloat()
        parent.position.x = sin(time) * 10f
        //parent.rotation.y += 1f * delta * speed.toFloat()
        //parent.position.x = sin(glfwGetTime()).toFloat() * 10f
    }
    
}