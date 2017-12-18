package com.mcmacker4.testgame

import com.mcmacker4.javelin.Application
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.Camera
import com.mcmacker4.javelin.gameobject.component.Light
import com.mcmacker4.javelin.gameobject.component.Material
import com.mcmacker4.javelin.gameobject.component.Mesh
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.util.Resources
import org.joml.Vector3f


class Javelin : Application() {
    
    init {

        //Create model GameObject
        val texture = GLTexture2D(Resources.loadImageData("textures/metal_iron_rusty_basecolor.png"))
        
        val vao = ModelLoader.loadObj("anvil-blender-guru")
        val anvil = GameObject(Mesh(vao), Material(albedoMap = texture, metallic = 1f, roughness = 0.5f))
        world.addGameObject(anvil)

        val plane = GameObject(
                Mesh(ModelLoader.plane()),
                Material(baseColor = Vector3f(1f), metallic = 0f, roughness = 0.5f)
        )
        plane.rotation.x = -(Math.PI / 2).toFloat()
        plane.scale.set(100f)
        world.addGameObject(plane)

        //Create camera GameObject
        val camera = GameObject(Camera(), CameraControl())
        camera.position.set(0f, 3f, 0f)
        camera.position.set(-2f, 3.5f, 3f)
        camera.rotation.set(-0.622f, -0.41f, 0f)
        world.setActiveCamera(camera)
        
        val light = GameObject(Light(Vector3f(1f)), CirclePath())
        world.addGameObject(light)
        
    }
    
}