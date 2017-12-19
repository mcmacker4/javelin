#version 430 core

//Vertex shader input
layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;
layout(location = 4) in vec3 tangent;

//Vertex shader output
out VS_OUT {
    vec3 position;
    vec3 normal;
    vec2 texCoord;
    mat3 TBN;
} vs_out;

//Matrices
uniform mat4 cameraMatrix;
uniform mat4 modelMatrix;
uniform mat3 normalMatrix;

void main(void) {
    
    vec4 vertexPosition = modelMatrix * vec4(position, 1.0);
    gl_Position = cameraMatrix * vertexPosition;
    
    vs_out.position = vertexPosition.xyz;
    vs_out.texCoord = texCoord;
    
    vec3 N = normalize(normalMatrix * normal);
    vec3 T = normalize(normalMatrix * tangent);
    T = normalize(T - dot(T, N) * N);
    vec3 B = cross(N, T);
    
    vs_out.normal = N;
    vs_out.TBN = mat3(T, B, N);
    
}