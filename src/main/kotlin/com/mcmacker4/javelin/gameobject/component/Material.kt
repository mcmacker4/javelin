package com.mcmacker4.javelin.gameobject.component

import com.mcmacker4.javelin.gameobject.Component
import com.mcmacker4.javelin.gl.texture.GLTexture2D
import org.joml.Vector3f

class Material(
        val albedoMap: GLTexture2D? = null,
        val normalMap: GLTexture2D? = null,
        val metallicMap: GLTexture2D? = null,
        val roughnessMap: GLTexture2D? = null,
        val baseColor: Vector3f? = null,
        val metallic: Float? = null,
        val roughness: Float? = null
) : Component() {
    
    val useAlbedoMap = albedoMap != null
    val useNormalMap = normalMap != null
    val useMetallicMap = metallicMap != null
    val useRoughnessMap = roughnessMap != null
    
    init {
        if(baseColor == null && albedoMap == null)
            throw Exception("Material needs either BaseColor or AlbedoMap.")
        if(metallic == null && metallicMap == null)
            throw Exception("Material needs either Metallic value or Metallic map.")
        if(roughness == null && roughnessMap == null)
            throw Exception("Material needs either Roughness value or Roughness map.")
    }
    
    companion object {
        val DEFAULT = Material(baseColor = Vector3f(1f), metallic = 1f, roughness = 0.5f)
    }
    
}