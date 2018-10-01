#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 vertexNormal;
layout (location=2) in vec2 texCoord;

out vec2 outTexCoord;
out vec3 mvVertexPos;
out vec3 fragNormal;
out float fragDist;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform float time;

void main()
{
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    outTexCoord = texCoord;

    mvVertexPos = mvPos.xyz;
    fragNormal = vertexNormal;

    fragDist = distance(vec3(0,0,0), vec3(mvPos));
}
