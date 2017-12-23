package com.mcmacker4.javelin.gameobject.component

import com.mcmacker4.javelin.util.unaryMinus
import org.joml.Matrix4f
import org.joml.Vector3f


class SpotLight(
        color: Vector3f,
        
        constant: Float,
        linear: Float,
        quadratic: Float,

        val angle: Float
) : Light(color, constant, linear, quadratic) {
    
    fun getLightDirection() : Vector3f {
        return Vector3f(0f, 0f, -1f)
                .rotateX(parent.rotation.x * 2)
                .rotateY(parent.rotation.y * 2)
                .rotateZ(parent.rotation.z * 2)
    }
    
    fun getLightMatrix() : Matrix4f {
        return Matrix4f().perspective(angle, 1f, 1f, 1000f)
                .rotateX(-parent.rotation.x).rotateY(-parent.rotation.y).rotateZ(-parent.rotation.z)
                .translate(-parent.position)
    }
    
}