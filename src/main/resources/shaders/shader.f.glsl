#version 330 core

in vec3 _normal;
in vec2 _texCoord;

out vec4 FragColor;

uniform sampler2D colorTexture;
uniform bool useColorTexture;

void main(void) {

    vec3 lightDir = normalize(vec3(-1, -1, -1));
    float brightness = dot(_normal, -lightDir);

    vec4 baseColor;
    if(useColorTexture) {
        baseColor = texture(colorTexture, _texCoord);
    } else {
        baseColor = vec4(1.0);
    }
    
    FragColor = baseColor * brightness;
    
}