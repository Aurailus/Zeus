#version 330

//in vec2 texBase;
//in vec2 texCoord;
//in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in vec3 fragNormal;
in vec2 outTexCoord;

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
//uniform Material material;

vec4 color;

void main()
{
    //vec2 mult = vec2(mod(texCoord.x * repeat_scale - 0.00001, 1), mod(texCoord.y * repeat_scale - 0.00001, 1));
    //color = texture(texture_sampler, vec2(texBase.x + (atlas_scale * mult.x), texBase.y + (atlas_scale * mult.y)));

    color = texture(texture_sampler, outTexCoord);

    float light = min(0.80f + 0.10f * abs(fragNormal.z) + 0.25f * abs(fragNormal.y), 1.1);

    color = color * vec4(vec3(light), 1);

    if (color.a < 0.5) discard;
    
    fragColor = color;
}