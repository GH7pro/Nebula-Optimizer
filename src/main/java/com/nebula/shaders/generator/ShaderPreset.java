package com.nebula.shaders.generator;

import java.util.HashMap;
import java.util.Map;

public class ShaderPreset {
    private String name;
    private String author;
    private String description;
    private Map<String, Float> uniforms = new HashMap<>();
    
    public ShaderPreset(String name, String author, String description) {
        this.name = name;
        this.author = author;
        this.description = description;
    }
    
    public ShaderPreset addUniform(String name, float value) {
        uniforms.put(name, value);
        return this;
    }
    
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public Map<String, Float> getUniforms() { return uniforms; }
}
