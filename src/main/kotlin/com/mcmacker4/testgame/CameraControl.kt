package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script
import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.input.Mouse
import com.mcmacker4.javelin.util.times
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW


class CameraControl : Script() {
    
    private val speed = Vector3f()
    private val sensitivity = 0.005f
    
    init {
        //Mouse Input
        Mouse.onMove { x, y ->
            parent.rotation.add((-y * sensitivity).toFloat(), (-x * sensitivity).toFloat(), 0f)
        }
        //Keyboard input
        Keyboard.onKeyDown { key, _ ->
            when(key) {
                GLFW.GLFW_KEY_A -> speed.x -= 1.0f
                GLFW.GLFW_KEY_D -> speed.x += 1.0f
                GLFW.GLFW_KEY_W -> speed.z -= 1.0f
                GLFW.GLFW_KEY_S -> speed.z += 1.0f
                GLFW.GLFW_KEY_LEFT_SHIFT -> speed.y -= 1.0f
                GLFW.GLFW_KEY_SPACE -> speed.y += 1.0f
            }
        }
        Keyboard.onKeyUp { key, _ ->
            when(key) {
                GLFW.GLFW_KEY_A -> speed.x += 1.0f
                GLFW.GLFW_KEY_D -> speed.x -= 1.0f
                GLFW.GLFW_KEY_W -> speed.z += 1.0f
                GLFW.GLFW_KEY_S -> speed.z -= 1.0f
                GLFW.GLFW_KEY_LEFT_SHIFT -> speed.y += 1.0f
                GLFW.GLFW_KEY_SPACE -> speed.y -= 1.0f
            }
        }
    }

    override fun update(delta: Float) {
        val deltaPos = Vector3f(speed).rotateY(parent.rotation.y)
        if(deltaPos.length() > 0) deltaPos.normalize()
        parent.position.add(deltaPos * delta)
    }
    
}