package com.nebula.shaders.presets;

import com.nebula.shaders.generator.ShaderPreset;

public class ShaderPresets {
    
    public static ShaderPreset getDefaultPreset() {
        return new ShaderPreset("Default", "Nebula", "Shader padrão")
            .addUniform("saturation", 1.0f)
            .addUniform("brightness", 1.0f)
            .addUniform("contrast", 1.0f)
            .addUniform("vignette", 0.0f)
            .addUniform("sepia", 0.0f);
    }
    
    public static ShaderPreset getVibrantPreset() {
        return new ShaderPreset("Vibrant", "Nebula", "Cores vibrantes")
            .addUniform("saturation", 1.5f)
            .addUniform("brightness", 1.1f)
            .addUniform("contrast", 1.2f)
            .addUniform("vignette", 0.0f)
            .addUniform("sepia", 0.0f);
    }
    
    public static ShaderPreset getRetroPreset() {
        return new ShaderPreset("Retro", "Nebula", "Efeito retrô")
            .addUniform("saturation", 0.6f)
            .addUniform("brightness", 0.9f)
            .addUniform("contrast", 1.1f)
            .addUniform("vignette", 0.4f)
            .addUniform("sepia", 0.8f);
    }
    
    public static ShaderPreset getCinematicPreset() {
        return new ShaderPreset("Cinematic", "Nebula", "Efeito cinematográfico")
            .addUniform("saturation", 0.8f)
            .addUniform("brightness", 0.9f)
            .addUniform("contrast", 1.3f)
            .addUniform("vignette", 0.6f)
            .addUniform("sepia", 0.0f);
    }
    
    public static ShaderPreset getNightVisionPreset() {
        return new ShaderPreset("Night Vision", "Nebula", "Visão noturna")
            .addUniform("saturation", 0.0f)
            .addUniform("brightness", 1.5f)
            .addUniform("contrast", 1.0f)
            .addUniform("vignette", 0.0f)
            .addUniform("sepia", 0.0f);
    }
    
    public static ShaderPreset[] getAllPresets() {
        return new ShaderPreset[] {
            getDefaultPreset(),
            getVibrantPreset(),
            getRetroPreset(),
            getCinematicPreset(),
            getNightVisionPreset()
        };
    }
}
