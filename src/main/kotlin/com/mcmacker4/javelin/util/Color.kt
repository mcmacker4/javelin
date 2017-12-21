package com.mcmacker4.javelin.util

import org.joml.Vector3f

object Color {
    
    fun hex(value: Int) : Vector3f {
        val r = ((value and 0xFF0000) shr 16) / 0xFF.toFloat()
        val g = ((value and 0x00FF00) shr  8) / 0xFF.toFloat()
        val b = (value and 0x0000FF) / 0xFF.toFloat()
        return Vector3f(r, g, b)
    }
    
}