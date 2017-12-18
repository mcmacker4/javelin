#version 330 core

struct Light {
    vec3    position;
    vec3    color;
};

in vec3 _normal;
in vec2 _texCoord;
in vec3 _position;

out vec4 FragColor;

uniform vec3 viewPosition;

uniform bool useAlbedoMap;
uniform bool useNormalMap;
uniform bool useMetallicMap;
uniform bool useRoughnessMap;

uniform sampler2D albedoMap;
uniform vec3 baseColor;

uniform sampler2D normalMap;

uniform sampler2D metallicMap;
uniform float metallic;

uniform sampler2D roughnessMap;
uniform float roughness;

uniform Light lights[10];
uniform int lightCount;

vec3 getColor(vec2 coord) {
    if(useAlbedoMap) return texture(albedoMap, coord).xyz;
    return baseColor;
}

void main(void) {

    vec3 result = vec3(0.0);
    
    vec3 color = getColor(_texCoord);
    vec3 viewDir = normalize(viewPosition - _position);
    
    float ambient = 0.1f;
    
    for(int i = 0; i < lightCount; i++) {
    
        Light light = lights[i];
        vec3 lightDir = normalize(light.position - _position);
        
        vec3 reflectDir = reflect(-lightDir, _normal);
        
        float distance = length(light.position - _position);
        //float attenuation = 1.0 / (distance * distance);
        float attenuation = 1.0;
        
        vec3 diffuse = light.color * max(dot(_normal, lightDir), 0.0) * color;
        vec3 specular = pow(max(dot(viewDir, reflectDir), 0.0), 32) * color;
        
        result += ((diffuse + specular) * attenuation) + ambient;
    
    }
    
    FragColor = vec4(result, 1.0);
    
}