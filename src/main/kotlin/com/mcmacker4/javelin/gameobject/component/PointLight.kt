package com.mcmacker4.javelin.gameobject.component

import org.joml.Vector3f


class PointLight(
        color: Vector3f,
        constant: Float,
        linear: Float,
        quadratic: Float
) : Light(color, constant, linear, quadratic)