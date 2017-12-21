package com.mcmacker4.testgame

import com.mcmacker4.javelin.Application
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.*
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.util.Color
import com.mcmacker4.javelin.util.Resources
import java.lang.Math.toRadians

class Javelin : Application() {
    
    init {

        //Create model GameObject
        val anvilAlbedo = GLTexture2D(Resources.loadImageData("textures/metal_iron_rusty_basecolor.png"))
        val anvilNormal = GLTexture2D(Resources.loadImageData("textures/Anvil-NormalMap.png"))
        val anvilRoughness = GLTexture2D(Resources.loadImageData("textures/metal_iron_rusty_roughness.png"))
        val vao = ModelLoader.loadObj("anvil-blender-guru")
        val anvil = GameObject(Mesh(vao), Material(albedoMap = anvilAlbedo, normalMap = anvilNormal, metallic = 1f, roughnessMap = anvilRoughness))
        world.addGameObject(anvil)

        val brickAlbedo = GLTexture2D(Resources.loadImageData("textures/brickwall.png"))
        val brickNormal = GLTexture2D(Resources.loadImageData("textures/brickwall_normal.png"))
        val plane = GameObject(
                Mesh(ModelLoader.plane(8f)),
                Material(albedoMap = brickAlbedo, normalMap = brickNormal, metallic = 0f, roughness = 0f)
        )
        plane.rotation.x = -(Math.PI.toFloat() / 2)
        plane.position.set(0f, 0f, 0f)
        plane.scale.set(16f)
        world.addGameObject(plane)

        //Create camera GameObject
        val camera = GameObject(Camera(), CameraControl())
        camera.position.set(-2f, 3.5f, 3f)
        camera.rotation.set(-0.622f, -0.41f, 0f)
        world.setActiveCamera(camera)
        
        val light = GameObject(PointLight(Color.hex(0x00bbff), 0.5f, 0.22f, 0.20f))
        //light.position.set(3f, 8f, 6f)
        light.position.set(-8f, 15f, -15f)
        world.addGameObject(light)
        
        val light2 = GameObject(SpotLight(
                Color.hex(0xffbb00),
                0.5f, 0.5f,
                0.8f,
                toRadians(20.0).toFloat()
        ))
        //light2.position.set(-8f, 15f, -15f)
        light2.position.set(0f, 10f, -5f)
        light2.rotation.set(-Math.PI.toFloat() / 2, 0f, 0f)
        world.addGameObject(light2)
        
    }
    
}