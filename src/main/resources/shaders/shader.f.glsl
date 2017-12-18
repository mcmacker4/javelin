#version 330 core

struct Light {
    vec3    position;
    vec3    color;
    float   constant;
    float   linear;
    float   quadratic;
};

in vec3 _position;
in vec3 _normal;
in vec2 _texCoord;
in mat3 _TBN;

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

vec3 getColor() {
    if(useAlbedoMap)
        return texture(albedoMap, _texCoord).rgb;
    return baseColor;
}

vec3 getNormal() {
    if(useNormalMap)
        return normalize(texture(normalMap, _texCoord).rgb * 2.0 - 1.0);
    return _normal;
}

void main(void) {

    vec3 normal = normalize(_TBN * getNormal());

    vec3 result = vec3(0.0);
    
    vec3 color = getColor();
    vec3 viewDir = normalize(viewPosition - _position);
    
    float ambient = 0.1f;
    
    for(int i = 0; i < lightCount; i++) {
    
        Light light = lights[i];
        
        vec3 lightDir = normalize(light.position - _position);
        vec3 reflectDir = reflect(-lightDir, normal);
        
        float distance = length(light.position - _position);
        float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
        
        vec3 ambient = ambient * color;
        vec3 diffuse = light.color * max(dot(normal, lightDir), 0.0) * color;
        vec3 specular = pow(max(dot(viewDir, reflectDir), 0.0), 32) * color;
        
        result += (ambient + diffuse + specular);
    
    }
    
    FragColor = vec4(result, 1.0);
    //FragColor = vec4(getNormal() * 0.5 + 0.5, 1.0);
    
}