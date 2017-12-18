package com.mcmacker4.javelin.gameobject.component

import com.mcmacker4.javelin.gameobject.Component
import org.joml.Vector3f


class Light(
        val color: Vector3f,
        val constant: Float,
        val linear: Float,
        val quadratic: Float
) : Component()