package com.mcmacker4.javelin.gameobject.component

import com.mcmacker4.javelin.gameobject.Component


abstract class Script : Component() {
    
    open fun update(delta: Float) {}
    
}