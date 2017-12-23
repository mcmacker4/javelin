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
uniform PointLight pointLights[32];
uniform int pointLightCount;

struct SpotLight {
    vec3 position;
    vec3 color;
    
    float constant;
    float linear;
    float quadratic;
    
    vec3 direction;
    float angle;
    
    mat4 matrix;
    sampler2D shadowMap;
};
uniform SpotLight spotLights[32];
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
    return cos(angle);
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
    
    return (diffuse + specular + ambient) * attenuation;
}

vec3 calculateSpotLight(SpotLight light, Fragment frag, vec3 viewDir) {
    
    vec3 lightDir = normalize(light.position - frag.position); //Points to light
    
//    if(dot(-lightDir, normalize(light.direction)) < angleToDotValue(light.angle))
//        return vec3(0.0);
        
    //Check if is under shadow
    //Position to uv coordinates
    vec4 lightSpacePos = light.matrix * vec4(frag.position, 1.0);
    vec3 projection = lightSpacePos.xyz / lightSpacePos.w; // [-1, 1] ?
    //Circle
    if(length(projection) > 1.0) return vec3(0.0);
    //
    projection = projection * 0.5 + 0.5;
    //Get depth
    float rayDepth = texture(light.shadowMap, projection.xy).r;
    float dist = length(light.position - frag.position);
    //Compare depth
    if(projection.z - 0.001 > rayDepth)
        return vec3(0.0);
    
    
    //diffuse
    vec3 diffuse = dot(frag.normal, lightDir) * frag.color * light.color;
    
    //specular
    vec3 reflectDir = normalize(reflect(-lightDir, frag.normal));
    vec3 specular =  pow(max(dot(viewDir, reflectDir), 0.0), 32) * light.color * frag.color * frag.specular;
    
    float attenuation = 1.0 / (light.constant + light.linear * dist + light.quadratic * (dist * dist));
    
    vec3 ambient = defaultAmbient * light.color * frag.color;
    
    return (diffuse + specular + ambient) * attenuation;
    
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
//    FragColor = vec4(vec3(texture(spotLights[0].shadowMap, texCoord).r) * 5 - 4, 1.0);
//    FragColor = vec4(frag.position * 0.01, 1.0);

}
