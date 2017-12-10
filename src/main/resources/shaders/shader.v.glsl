#version 330 core

layout(location = 0) in vec3 position;

out vec3 _color;

uniform mat4 cameraMatrix;

void main(void) {
    gl_Position = cameraMatrix * vec4(position, 1.0);
    _color = vec3(position.x + 0.5, position.y + 0.5, 0.0);
}