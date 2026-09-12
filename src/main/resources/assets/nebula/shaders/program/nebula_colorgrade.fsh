#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform float saturation;
uniform float brightness;
uniform float contrast;
uniform float vignette;
uniform float sepia;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    if (sepia > 0.0) {
        float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
        vec3 sepiaColor = vec3(gray * 1.2, gray * 1.0, gray * 0.8);
        color.rgb = mix(color.rgb, sepiaColor, sepia);
    }

    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = mix(vec3(gray), color.rgb, saturation);

    color.rgb = color.rgb * brightness;
    color.rgb = (color.rgb - 0.5) * contrast + 0.5;

    if (vignette > 0.0) {
        vec2 uv = texCoord - 0.5;
        float dist = length(uv);
        float vignetteFactor = 1.0 - dist * vignette;
        color.rgb *= vignetteFactor;
    }

    fragColor = vec4(color.rgb, 1.0);
}
