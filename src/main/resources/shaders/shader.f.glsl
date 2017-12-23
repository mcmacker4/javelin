#version 430 core

#define PI 3.14159265359

//Fragment Shader Input
in VS_OUT {
    vec3 position;
    vec3 normal;
    vec2 texCoord;
    mat3 TBN;
} frag;

//Fragment Shader Output
out vec4 FragColor;

//View position
uniform vec3 viewPosition;

//Lighting
struct PointLight {
    vec3    position;
    vec3    color;
    
    float   constant;
    float   linear;
    float   quadratic;
};
uniform PointLight pointLights[10];
uniform int pointLightCount;

struct SpotLight {
    vec3 position;
    vec3 color;
    
    float constant;
    float linear;
    float quadratic;
    
    vec3 direction;
    float angle;
};
uniform SpotLight spotLights[10];
uniform int spotLightCount;

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

//Constants
const float defaultAmbient = 0.1;

vec3 getColor() {
    if(material.useAlbedoMap)
        return texture(material.albedoMap, frag.texCoord).rgb;
    return material.baseColor;
}

vec3 getNormal() {
    if(material.useNormalMap) {
        return normalize(texture(material.normalMap, frag.texCoord).rgb * 2.0 - 1.0);
    }
    return frag.normal;
}

float getRoughness() {
    if(material.useRoughnessMap) {
        return texture(material.roughnessMap, frag.texCoord).r;
    }
    return material.roughness;
}

float angleToDotValue(float angle) {
    return -(angle / PI * 2 - 1);
}

vec3 calculatePointLight(PointLight light, vec3 color, vec3 normal, vec3 viewDir, float roughness) {
    vec3 lightDir = normalize(light.position - frag.position);
    vec3 reflectDir = reflect(-lightDir, normal);
    
    float distance = length(light.position - frag.position);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    
    vec3 ambient = color * defaultAmbient;
    vec3 diffuse = light.color * max(dot(normal, lightDir), 0.0) * color;
    vec3 specular = light.color * pow(max(dot(viewDir, reflectDir), 0.0), 32) * color * (1 - roughness);
    
    return (ambient + diffuse + specular) * attenuation;
}

vec3 calculateSpotLight(SpotLight light, vec3 color, vec3 normal, vec3 viewDir, float roughness) {
    vec3 lightDir = normalize(light.position - frag.position);
    
    if(dot(-lightDir, light.direction) < angleToDotValue(light.angle))
        return vec3(0.0);
    
    vec3 reflectDir = reflect(-lightDir, normal);
    
    float distance = length(light.position - frag.position);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    
    vec3 ambient = color * defaultAmbient;
    vec3 diffuse = light.color * max(dot(normal, lightDir), 0.0) * color;
    vec3 specular = light.color * pow(max(dot(viewDir, reflectDir), 0.0), 32) * color * (1 - roughness);
    
    return (ambient + diffuse + specular) * attenuation;
}

void main(void) {

    vec3 normal = normalize(frag.TBN * getNormal());

    vec3 result = vec3(0.0);
    
    vec3 color = getColor();
    float roughness = getRoughness();
    vec3 viewDir = normalize(viewPosition - frag.position);
    
    for(int i = 0; i < pointLightCount; i++)
        result += calculatePointLight(pointLights[i], color, normal, viewDir, roughness);
    for(int i = 0; i < spotLightCount; i++)
        result += calculateSpotLight(spotLights[i], color, normal, viewDir, roughness);
    
    FragColor = vec4(result, 1.0);
//    FragColor = vec4(normal, 1.0);
    
}