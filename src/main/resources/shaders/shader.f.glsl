#version 430 core

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
struct Light {
    vec3    position;
    vec3    color;
    float   constant;
    float   linear;
    float   quadratic;
};
uniform Light lights[10];
uniform int lightCount;

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
    if(material.useNormalMap)
        return normalize(texture(material.normalMap, frag.texCoord).rgb * 2.0 - 1.0);
    return frag.normal;
}

void main(void) {

    vec3 normal = normalize(frag.TBN * getNormal());

    vec3 result = vec3(0.0);
    
    vec3 color = getColor();
    vec3 viewDir = normalize(viewPosition - frag.position);
    
    for(int i = 0; i < lightCount; i++) {
    
        Light light = lights[i];
        
        vec3 lightDir = normalize(light.position - frag.position);
        vec3 reflectDir = reflect(-lightDir, normal);
        
        float distance = length(light.position - frag.position);
        float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
        
        vec3 ambient = color * defaultAmbient;
        vec3 diffuse = light.color * max(dot(normal, lightDir), 0.0) * color;
        vec3 specular = pow(max(dot(viewDir, reflectDir), 0.0), 32) * color;
        
        result += (ambient + diffuse + specular);
    
    }
    
    FragColor = vec4(result, 1.0);
    //FragColor = vec4(normal * 0.5 + 0.5, 1.0);
    
}