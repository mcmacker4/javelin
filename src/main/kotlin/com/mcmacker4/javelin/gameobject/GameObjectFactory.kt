package com.mcmacker4.javelin.gameobject

import com.mcmacker4.javelin.gameobject.component.Camera

object GameObjectFactory {
    
    fun createCamera() : GameObject {
        return GameObject(Camera())
    }
    
}