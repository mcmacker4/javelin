#version 430

#define PI 3.14159265359

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D albedoSpecular;
uniform sampler2D normal;
uniform sampler2D position;

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

//Constants
const float defaultAmbient = 0.1;

struct Fragment {
    vec3 position;
    vec3 color;
    vec3 normal;
    float specular;
} frag;

float angleToDotValue(float angle) {
    return -(angle / PI * 2 - 1);
}

vec3 calculatePointLight(PointLight light, Fragment frag, vec3 viewDir) {
    vec3 lightDir = normalize(light.position - frag.position); //Points to light
        
    float dist = length(light.position - frag.position);
    
    //diffuse
    vec3 diffuse = dot(frag.normal, lightDir) * frag.color * light.color;
    
    //specular
    vec3 reflectDir = normalize(reflect(-lightDir, frag.normal));
    vec3 specular =  pow(max(dot(viewDir, reflectDir), 0.0), 32) * light.color * frag.color * frag.specular;
    
    float attenuation = 1.0 / (light.constant + light.linear * dist + light.quadratic * (dist * dist));
    
    vec3 ambient = defaultAmbient * light.color * frag.color;
    
    return (diffuse + specular + ambient);
}

vec3 calculateSpotLight(SpotLight light, Fragment frag, vec3 viewDir) {
    
    vec3 lightDir = normalize(light.position - frag.position); //Points to light
    
    if(dot(-lightDir, light.direction) < angleToDotValue(light.angle))
        return vec3(0.0);
    
    float dist = length(light.position - frag.position);
    
    //diffuse
    vec3 diffuse = dot(frag.normal, lightDir) * frag.color * light.color;
    
    //specular
    vec3 reflectDir = normalize(reflect(-lightDir, frag.normal));
    vec3 specular =  pow(max(dot(viewDir, reflectDir), 0.0), 32) * light.color * frag.color * frag.specular;
    
    float attenuation = 1.0 / (light.constant + light.linear * dist + light.quadratic * (dist * dist));
    
    vec3 ambient = defaultAmbient * light.color * frag.color;
    
    return (diffuse + specular + ambient);
    
}

void main() {

    frag.position = texture(position, texCoord).xyz;
    frag.color = texture(albedoSpecular, texCoord).xyz;
    frag.normal = normalize(texture(normal, texCoord).xyz);
    frag.specular = texture(albedoSpecular, texCoord).w;
    
    vec3 viewDir = normalize(viewPosition - frag.position); //Points to eye
    
    vec3 result = vec3(0.0);
    
    for(int i = 0; i < pointLightCount; i++) {
        result += calculatePointLight(pointLights[i], frag, viewDir);
    }
    
    for(int i = 0; i < spotLightCount; i++) {
        result += calculateSpotLight(spotLights[i], frag, viewDir);
    }
    
    FragColor = vec4(result, 1.0);

}
