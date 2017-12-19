package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script
import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.input.Mouse
import com.mcmacker4.javelin.util.times
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*


class CameraControl : Script() {
    
    private val direction = Vector3f()
    private val speed = 3f
    private val sensitivity = 0.005f
    
    init {
        //Mouse Input
        Mouse.onMove { x, y ->
            parent.rotation.add((-y * sensitivity).toFloat(), (-x * sensitivity).toFloat(), 0f)
            if(parent.rotation.x > Math.PI / 2)
                parent.rotation.x = Math.PI.toFloat() / 2
            if(parent.rotation.x < -Math.PI / 2)
                parent.rotation.x = -Math.PI.toFloat() / 2
        }
        //Keyboard input
        Keyboard.onKeyDown { key, _ ->
            when(key) {
                GLFW_KEY_A -> direction.x -= 1.0f
                GLFW_KEY_D -> direction.x += 1.0f
                GLFW_KEY_W -> direction.z -= 1.0f
                GLFW_KEY_S -> direction.z += 1.0f
                GLFW_KEY_LEFT_SHIFT -> direction.y -= 1.0f
                GLFW_KEY_SPACE -> direction.y += 1.0f
            }
        }
        Keyboard.onKeyUp { key, _ ->
            when(key) {
                GLFW_KEY_A -> direction.x += 1.0f
                GLFW_KEY_D -> direction.x -= 1.0f
                GLFW_KEY_W -> direction.z += 1.0f
                GLFW_KEY_S -> direction.z -= 1.0f
                GLFW_KEY_LEFT_SHIFT -> direction.y += 1.0f
                GLFW_KEY_SPACE -> direction.y -= 1.0f
            }
        }
    }

    override fun update(delta: Float) {
        //TODO: when JOML 1.9.7 is released, update build.gradle and remove " * 2" in the angle.
        val deltaPos = Vector3f(direction).rotateY(parent.rotation.y * 2)
        if(deltaPos.length() > 0)
            deltaPos.normalize()
        parent.position.add(deltaPos * delta * speed)
    }
    
}