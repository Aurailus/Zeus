#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec4 texData;
layout (location=2) in vec3 vertexNormal;

out vec2 texBase;
out vec2 texCoord;
//out vec3 mvVertexNormal;
out vec3 mvVertexPos;
out vec3 fragNormal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    texBase = vec2(texData.x, texData.y);
    texCoord = vec2(texData.z, texData.w);
    //mvVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    mvVertexPos = mvPos.xyz;
    fragNormal = vertexNormal;
}