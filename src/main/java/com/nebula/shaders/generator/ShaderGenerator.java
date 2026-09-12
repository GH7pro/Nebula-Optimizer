package com.nebula.shaders.generator;

import java.util.HashMap;
import java.util.Map;

public class ShaderGenerator {
    private Map<String, String> uniforms = new HashMap<>();
    private String vertexShader = "";
    private String fragmentShader = "";
    
    public ShaderGenerator() {
        loadDefaultTemplates();
    }
    
    private void loadDefaultTemplates() {
        vertexShader = 
            "#version 150\n\n" +
            "in vec3 Position;\n" +
            "in vec2 UV;\n" +
            "in vec4 Color;\n" +
            "\n" +
            "uniform mat4 ProjMat;\n" +
            "uniform mat4 ModelViewMat;\n" +
            "\n" +
            "out vec2 texCoord;\n" +
            "out vec4 vertexColor;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);\n" +
            "    texCoord = UV;\n" +
            "    vertexColor = Color;\n" +
            "}\n";
        
        fragmentShader = 
            "#version 150\n\n" +
            "uniform sampler2D DiffuseSampler;\n" +
            "\n" +
            "in vec2 texCoord;\n" +
            "in vec4 vertexColor;\n" +
            "\n" +
            "out vec4 fragColor;\n" +
            "\n" +
            "uniform float time;\n" +
            "uniform float saturation = 1.0;\n" +
            "uniform float brightness = 1.0;\n" +
            "uniform float contrast = 1.0;\n" +
            "uniform float vignette = 0.0;\n" +
            "uniform float sepia = 0.0;\n" +
            "\n" +
            "void main() {\n" +
            "    vec4 color = texture(DiffuseSampler, texCoord);\n" +
            "    \n" +
            "    if (sepia > 0.0) {\n" +
            "        float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
            "        vec3 sepiaColor = vec3(gray * 1.2, gray * 1.0, gray * 0.8);\n" +
            "        color.rgb = mix(color.rgb, sepiaColor, sepia);\n" +
            "    }\n" +
            "    \n" +
            "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
            "    color.rgb = mix(vec3(gray), color.rgb, saturation);\n" +
            "    \n" +
            "    color.rgb = color.rgb * brightness;\n" +
            "    color.rgb = (color.rgb - 0.5) * contrast + 0.5;\n" +
            "    \n" +
            "    if (vignette > 0.0) {\n" +
            "        vec2 uv = texCoord - 0.5;\n" +
            "        float dist = length(uv);\n" +
            "        float vignetteFactor = 1.0 - dist * vignette;\n" +
            "        color.rgb *= vignetteFactor;\n" +
            "    }\n" +
            "    \n" +
            "    fragColor = color * vertexColor;\n" +
            "}\n";
    }
    
    public String generateVertexShader() {
        return vertexShader;
    }
    
    public String generateFragmentShader(ShaderPreset preset) {
        String shader = fragmentShader;
        
        for (Map.Entry<String, Float> entry : preset.getUniforms().entrySet()) {
            if (!shader.contains("uniform float " + entry.getKey())) {
                shader = shader.replace("void main() {", 
                    "uniform float " + entry.getKey() + " = " + entry.getValue() + ";\n\nvoid main() {");
            }
        }
        
        return shader;
    }
    
    public String generateFullShader(ShaderPreset preset) {
        StringBuilder sb = new StringBuilder();
        sb.append("// ============================================================\n");
        sb.append("// Shader: ").append(preset.getName()).append("\n");
        sb.append("// Author: ").append(preset.getAuthor()).append("\n");
        sb.append("// Description: ").append(preset.getDescription()).append("\n");
        sb.append("// ============================================================\n\n");
        sb.append("// ===== VERTEX SHADER =====\n");
        sb.append(vertexShader);
        sb.append("\n// ===== FRAGMENT SHADER =====\n");
        sb.append(generateFragmentShader(preset));
        
        return sb.toString();
    }
}
