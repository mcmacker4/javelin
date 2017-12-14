#version 330 core

in vec3 _normal;

out vec4 FragColor;

void main(void) {
    vec3 lightDir = normalize(vec3(-1, -1, -1));
    float brightness = dot(_normal, -lightDir);
    FragColor = vec4(brightness, brightness, brightness, 1.0);
}