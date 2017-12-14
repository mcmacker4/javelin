#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 texCoord;

out vec3 _normal;

uniform mat4 cameraMatrix;
uniform mat4 modelMatrix;

void main(void) {
    gl_Position = cameraMatrix * modelMatrix * vec4(position, 1.0);
    _normal = normalize((modelMatrix * vec4(normal, 1.0)).xyz);
}