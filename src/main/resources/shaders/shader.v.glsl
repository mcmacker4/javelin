#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;

out vec3 _position;
out vec3 _normal;
out vec2 _texCoord;

uniform mat4 cameraMatrix;
uniform mat4 modelMatrix;
uniform mat4 normalMatrix;

void main(void) {
    vec4 vertexPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = cameraMatrix * vertexPosition;
    _position = vertexPosition.xyz;
    _normal = (normalMatrix * vec4(normal, 0.0)).xyz;
    _texCoord = texCoord;
}