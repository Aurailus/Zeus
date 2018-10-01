#version 330

in vec3 mvVertexPos;
in vec3 fragNormal;
in vec2 outTexCoord;

in float fragDist;

out vec4 fragColor;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform float repeat_scale;
uniform float atlas_scale;
uniform float specularPower;

vec4 color;

void main()
{
    color = texture(texture_sampler, outTexCoord);

    float light = min(0.80f + 0.10f * abs(fragNormal.z) + 0.25f * abs(fragNormal.y), 1.1);

    color = color * vec4(vec3(light), 1);

    if (color.a < 0.5) discard;

    float maxRenderDist = (16*12) - 8;

    float dist_mult = pow(min(1, max(0, fragDist/maxRenderDist)), 2);

    vec4 sky_color = vec4(0.4235, 0.7058, 0.9686, 1);
    color = color * (1 - dist_mult) + sky_color * (dist_mult);
    
    fragColor = color;
}