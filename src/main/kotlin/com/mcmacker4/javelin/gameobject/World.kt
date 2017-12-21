package com.mcmacker4.javelin.gameobject

import com.mcmacker4.javelin.gameobject.component.Camera
import com.mcmacker4.javelin.gameobject.component.Script
import com.mcmacker4.javelin.graphics.Renderer


class World(private var renderer: Renderer) {
    
    private val gameObjects = hashSetOf<GameObject>()
    private var activeCamera: GameObject? = null
    
    fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
        renderer.addGameObject(gameObject)
    }
    
    fun remvoeGameObject(gameObject: GameObject) {
        gameObjects.remove(gameObject)
        renderer.removeGameObject(gameObject)
    }
    
    fun setActiveCamera(gameObject: GameObject?) {
        if(gameObject == null || gameObject.hasComponent<Camera>())
            activeCamera = gameObject
    }
    
    fun update(delta: Float) {
        
        activeCamera?.let {
            it.getComponents<Script>().forEach { it.update(delta) }
            it.updateMatrix()
        }
        
        gameObjects.forEach { gameObject ->
            gameObject.getComponents<Script>().forEach {
                it.update(delta)
            }
            gameObject.updateMatrix()
        }
        
    }
    
    fun render() {
        renderer.renderDeferred(activeCamera)
    }
    
}