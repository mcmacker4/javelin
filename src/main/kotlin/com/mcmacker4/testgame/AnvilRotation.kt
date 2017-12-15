package com.mcmacker4.testgame

import com.mcmacker4.javelin.gameobject.component.Script


class AnvilRotation : Script() {

    override fun update(delta: Float) {
        parent.rotation.y += 1f * delta
    }
    
}