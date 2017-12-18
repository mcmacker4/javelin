package com.mcmacker4.javelin.util

import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f


operator fun Quaternionf.not() : Quaternionf {
    return Quaternionf(-this.x, -this.y, -this.z, this.w)
}

operator fun Vector3f.unaryMinus() : Vector3f {
    return Vector3f(-this.x, -this.y, -this.z)
}

operator fun Vector3f.plus(right: Vector3f) : Vector3f {
    return Vector3f(this.x + right.x, this.y + right.y, this.z + right.z)
}

operator fun Vector3f.minus(right: Vector3f) : Vector3f {
    return Vector3f(this.x - right.x, this.y - right.y, this.z - right.z)
}

operator fun Vector3f.times(right: Vector3f) : Vector3f {
    return Vector3f(this.x * right.x, this.y * right.y, this.z * right.z)
}

operator fun Vector3f.times(scalar: Float) : Vector3f {
    return Vector3f(this.x * scalar, this.y * scalar, this.z * scalar)
}

operator fun Vector2f.minus(right: Vector2f): Vector2f {
    return Vector2f(this.x - right.x, this.y - right.y)
}