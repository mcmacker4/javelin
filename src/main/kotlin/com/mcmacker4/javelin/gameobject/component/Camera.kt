package com.mcmacker4.javelin.gameobject.component

import com.mcmacker4.javelin.gameobject.Component


class Camera(
        var fovy: Float = Math.toRadians(90.0).toFloat(),
        var nearPlane: Float = 0.001f,
        var farPlane: Float = 1000f)
    : Component()