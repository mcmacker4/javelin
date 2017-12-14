package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script


class RotateScript(val speed: Float) : Script() {

    override fun update(delta: Float) {
        parent.rotation.y += speed * delta
    }
    
}