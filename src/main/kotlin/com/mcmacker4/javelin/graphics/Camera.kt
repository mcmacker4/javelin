package com.mcmacker4.javelin.graphics

import com.mcmacker4.javelin.input.Keyboard
import com.mcmacker4.javelin.input.Mouse
import com.mcmacker4.javelin.util.plus
import com.mcmacker4.javelin.util.times
import com.mcmacker4.javelin.util.unaryMinus
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

@Deprecated("Old Camera")
class Camera(
        private val position: Vector3f,
        private val rotation: Vector3f,
        fovy: Double,
        aspect: Float,
        near: Float = 0.01f,
        far: Float = 1000.0f) {

    var fovy = fovy
        set(value) {
            field = value
            updateMatrix()
        }

    var aspect = aspect
        set(value) {
            field = value
            updateMatrix()
        }

    var near = near
        set(value) {
            field = value
            updateMatrix()
        }

    var far = far
        set(value) {
            field = value
            updateMatrix()
        }
    
    val matrix: Matrix4f = Matrix4f()
    val matrixBuffer: FloatBuffer = MemoryUtil.memAllocFloat(4*4)
    
    private val speed = Vector3f()
    private val sensitivity = 0.005f
    
    init {
        //Mouse movement
        Mouse.onMove { x, y ->
            rotate((-y * sensitivity).toFloat(), (-x * sensitivity).toFloat(), 0f)
        }
        //Keyboard input
        Keyboard.onKeyDown { key, _ ->
            when(key) {
                GLFW_KEY_A -> speed.x -= 1.0f
                GLFW_KEY_D -> speed.x += 1.0f
                GLFW_KEY_W -> speed.z -= 1.0f
                GLFW_KEY_S -> speed.z += 1.0f
                GLFW_KEY_LEFT_SHIFT -> speed.y -= 1.0f
                GLFW_KEY_SPACE -> speed.y += 1.0f
            }
        }
        Keyboard.onKeyUp { key, _ ->
            when(key) {
                GLFW_KEY_A -> speed.x += 1.0f
                GLFW_KEY_D -> speed.x -= 1.0f
                GLFW_KEY_W -> speed.z += 1.0f
                GLFW_KEY_S -> speed.z -= 1.0f
                GLFW_KEY_LEFT_SHIFT -> speed.y += 1.0f
                GLFW_KEY_SPACE -> speed.y -= 1.0f
            }
        }
        updateMatrix()
    }
    
    fun move(delta: Vector3f) {
        position.add(delta)
        updateMatrix()
    }

    fun move(dx: Float, dy: Float, dz: Float) {
        position.x += dx
        position.y += dy
        position.z += dz
        updateMatrix()
    }
    
    fun setPosition(position: Vector3f) {
        this.position.set(position)
        updateMatrix()
    }
    
    fun rotate(delta: Vector3f) {
        rotation.add(delta)
        updateMatrix()
    }
    
    fun rotate(x: Float, y: Float, z: Float) {
        rotation.add(x, y, z)
        updateMatrix()
    }

    fun setRotation(rotation: Vector3f) {
        this.rotation.set(rotation)
        updateMatrix()
    }
    
    private fun forwardVector() : Vector3f {
        return Vector3f(0f, 0f, -1f)
                .rotateY(rotation.y)
    }
    
    private fun rightVector() : Vector3f {
        return Vector3f(1f, 0f, 0f)
                .rotateY(rotation.y)
    }
    
    private fun centerVector() : Vector3f {
        return Vector3f(0f, 0f, -1f)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z)
                .add(position)
    }
    
    private fun upVector(): Vector3f {
        return Vector3f(0f, 1f, 0f)
    }
    
    private fun updateMatrix() {
        matrix.setPerspective(fovy.toFloat(), aspect, near, far)
                .lookAt(position, centerVector(), upVector())
        matrix.get(matrixBuffer)
    }
    
    fun update(delta: Float) {
        val deltaPos = Vector3f(speed).rotateY(rotation.y)
        if(deltaPos.length() > 0) deltaPos.normalize()
        move(deltaPos * delta)
    }

}