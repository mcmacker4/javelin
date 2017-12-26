package com.mcmacker4.testgame

import com.mcmacker4.javelin.Application
import com.mcmacker4.javelin.gameobject.GameObject
import com.mcmacker4.javelin.gameobject.component.*
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import com.mcmacker4.javelin.model.ModelLoader
import com.mcmacker4.javelin.util.Color
import com.mcmacker4.javelin.util.Resources
import org.joml.Vector3f
import java.lang.Math.random
import java.lang.Math.toRadians

class Javelin : Application() {
    
    init {

        //Create model GameObject
        val anvilAlbedo = GLTexture2D(Resources.loadImageData("textures/metal_iron_rusty_basecolor.png"), 4)
        val anvilNormal = GLTexture2D(Resources.loadImageData("textures/Anvil-NormalMap.png"), 4)
        val anvilRoughness = GLTexture2D(Resources.loadImageData("textures/metal_iron_rusty_roughness.png"), 4)
        val vao = ModelLoader.loadObj("anvil-blender-guru")
//        val anvil = GameObject(Mesh(vao), Material(albedoMap = anvilAlbedo, normalMap = anvilNormal, metallic = 1f, roughnessMap = anvilRoughness))
//        world.addGameObject(anvil)

        for(i in 0..6) {
            for(j in 0..5) {
                val anvil = GameObject(Mesh(vao), Material(albedoMap = anvilAlbedo, normalMap = anvilNormal, metallic = 1f, roughnessMap = anvilRoughness))
                anvil.position.set(i.toFloat() * 8 - (6*8/2f), 0f, j.toFloat() * 8 - (5*8/2f))
                anvil.rotation.y = (Math.random() * Math.PI * 2).toFloat()
                world.addGameObject(anvil)
            }
        }
        
        val monkeyMesh = ModelLoader.loadObj("monkey")
        val monkey = GameObject(Mesh(monkeyMesh), Material(baseColor = Vector3f(1f, 0f, 0f), metallic = 1f, roughness = 0.7f))
        monkey.scale.set(4f)
        world.addGameObject(monkey)

        val brickAlbedo = GLTexture2D(Resources.loadImageData("textures/brickwall.png"), 4)
        val brickNormal = GLTexture2D(Resources.loadImageData("textures/brickwall_normal.png"), 4)
        val plane = GameObject(
                Mesh(ModelLoader.plane(8f * 4)),
                Material(albedoMap = brickAlbedo, normalMap = brickNormal, metallic = 0f, roughness = 0f)
        )
        plane.rotation.x = -(Math.PI.toFloat() / 2)
        plane.position.set(0f, 0f, 0f)
        plane.scale.set(16f * 4)
        world.addGameObject(plane)
        
        val spotLight = GameObject(SpotLight(
                Color.hex(0xffe989),
                1f, 0.022f,
                0.0019f,
                toRadians(120.0).toFloat()
        ), Test())
        spotLight.position.set(-10f, 10f, 10f)
        spotLight.rotation.set(-Math.PI.toFloat() / 4, -Math.PI.toFloat() / 4, 0f)
        world.addGameObject(spotLight)
        
        for(i in 0 until 2) {
            for(j in 0 until 2) {
                val pointLight = GameObject(PointLight(
                        Vector3f(random().toFloat() * 0.5f + 0.5f, random().toFloat() * 0.5f + 0.5f, random().toFloat() * 0.5f + 0.5f),
                        1f, 0.045f,	0.0075f))
                pointLight.position.set(i.toFloat() * 30 - 30, 15f, j.toFloat() * 30 - 30)
                world.addGameObject(pointLight)
            }
        }

        //Create camera GameObject
        val camera = GameObject(Camera(), CameraControl())
        camera.position.set(-2f, 3.5f, 3f)
        camera.rotation.set(-0.622f, -0.41f, 0f)
        world.setActiveCamera(camera)
        
    }
    
}