#version 430

//Vertex shader output
in VS_OUT {
    vec3 position;
    vec3 normal;
    vec2 texCoord;
    mat3 TBN;
} frag;

//GBuffer outputs
layout (location = 0) out vec4 albedoSpecular;
layout (location = 1) out vec3 normal;
layout (location = 2) out vec3 position;

//Materials
struct Material {

    //Booleans
    bool useAlbedoMap;
    bool useNormalMap;
    bool useMetallicMap;
    bool useRoughnessMap;
    
    //Color
    sampler2D albedoMap;
    vec3 baseColor;
    
    //Normal
    sampler2D normalMap;
    
    //Metallic
    sampler2D metallicMap;
    float metallic;
    
    //Roughness
    sampler2D roughnessMap;
    float roughness;
    
};
uniform Material material;

vec3 getColor() {
    if(material.useAlbedoMap)
        return texture(material.albedoMap, frag.texCoord).rgb;
    return material.baseColor;
}

vec3 getNormal() {
    if(material.useNormalMap)
        return normalize(texture(material.normalMap, frag.texCoord).rgb * 2.0 - 1.0);
    return frag.normal;
}

float getRoughness() {
    if(material.useRoughnessMap)
        return texture(material.roughnessMap, frag.texCoord).r;
    return material.roughness;
}

void main() {

    //Albedo & Specular
    albedoSpecular = vec4(getColor(), 1 - getRoughness());
    
    //Normal
    normal = normalize(frag.TBN * getNormal());
    
    //Position
    position = frag.position;

}
