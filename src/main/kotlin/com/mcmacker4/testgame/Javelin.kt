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
        val anvilAlbedo = GLTexture2D(Resources.loadImageData("textures/metal_iron_rusty_basecolor.png"))
        val anvilNormal = GLTexture2D(Resources.loadImageData("textures/Anvil-NormalMap.png"))
        val vao = ModelLoader.loadObj("anvil-blender-guru")
        val anvil = GameObject(Mesh(vao), Material(albedoMap = anvilAlbedo, normalMap = anvilNormal, metallic = 1f, roughness = 0.5f))
        world.addGameObject(anvil)

        val brickAlbedo = GLTexture2D(Resources.loadImageData("textures/brickwall.png"))
        val brickNormal = GLTexture2D(Resources.loadImageData("textures/brickwall_normal.png"))
        val plane = GameObject(
                Mesh(ModelLoader.plane()),
                Material(albedoMap = brickAlbedo, normalMap = brickNormal, metallic = 0f, roughness = 0.5f)
        )
        plane.rotation.x = -(Math.PI.toFloat() / 2)
        plane.position.set(0f, 0f, 0f)
        plane.scale.set(4f)
        world.addGameObject(plane)

        //Create camera GameObject
        val camera = GameObject(Camera(), CameraControl())
        camera.position.set(-2f, 3.5f, 3f)
        camera.rotation.set(-0.622f, -0.41f, 0f)
        world.setActiveCamera(camera)
        
        val light = GameObject(Light(Vector3f(1f), 0.5f, 0.22f, 0.20f))
        light.position.set(0f, 5f, 3f)
        world.addGameObject(light)

        val light2 = GameObject(Light(Vector3f(1f), 0.5f, 0.5f, 0.8f))
        light2.position.set(4f, 8f, -3f)
        world.addGameObject(light2)
        
    }
    
}