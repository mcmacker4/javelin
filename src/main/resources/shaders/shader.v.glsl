#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;
layout(location = 4) in vec3 tangent;

out vec3 _position;
out vec3 _normal;
out vec2 _texCoord;
out mat3 _TBN;

out vec3 T;
out vec3 B;

uniform mat4 cameraMatrix;
uniform mat4 modelMatrix;
uniform mat3 normalMatrix;

void main(void) {
    
    vec4 vertexPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = cameraMatrix * vertexPosition;
    
    _position = vertexPosition.xyz;
    _texCoord = texCoord;
    
    vec3 N = normalize(normalMatrix * normal);
    vec3 T = normalize(normalMatrix * tangent);
    T = normalize(T - dot(T, N) * N);
    vec3 B = cross(N, T);
    
    _normal = N;
    _TBN = mat3(T, B, N);
    
}