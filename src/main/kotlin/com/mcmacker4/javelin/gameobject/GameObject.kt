package com.mcmacker4.javelin.gameobject

import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.*

open class GameObject(vararg components: Component) {
    
    val position = Vector3f()
    val rotation = Vector3f()
    val scale = Vector3f(1f, 1f, 1f)
    
    val modelMatrix = Matrix4f()
    
    val componentsList: List<Component> = Collections.unmodifiableList(listOf(*components))
    
    init {
        componentsList.forEach { it.parent = this }
    }
    
    inline fun <reified T : Component> getComponent() : T? {
        return componentsList.firstOrNull { it is T } as T?
    }
    
    inline fun <reified T : Component> getComponents() : List<T> {
        return componentsList.filterIsInstance<T>()
    }
    
    inline fun <reified T : Component> hasComponent() : Boolean {
        return !componentsList.none { it is T }
    }
    
    internal fun updateMatrix() {
        modelMatrix.identity()
                .scale(scale)
                .translate(position)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z)
    }
    
}
