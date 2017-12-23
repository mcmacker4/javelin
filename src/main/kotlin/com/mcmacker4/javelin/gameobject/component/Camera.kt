package com.mcmacker4.javelin.gameobject.component

import com.mcmacker4.javelin.display.Display
import com.mcmacker4.javelin.gameobject.Component
import com.mcmacker4.javelin.util.unaryMinus
import org.joml.Matrix4f


class Camera(
        var fovy: Float = Math.toRadians(90.0).toFloat(),
        var nearPlane: Float = 0.001f,
        var farPlane: Float = 1000f)
    : Component() {
    
    fun getCameraMatrix() : Matrix4f {
        return Matrix4f().perspective(fovy, Display.MAIN.aspect, nearPlane, farPlane)
                .rotateX(-parent.rotation.x).rotateY(-parent.rotation.y).rotateZ(-parent.rotation.z)
                .translate(-parent.position)
    }
    
}