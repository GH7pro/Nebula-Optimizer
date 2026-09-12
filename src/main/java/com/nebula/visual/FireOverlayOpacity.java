package com.nebula.visual;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

public class FireOverlayOpacity implements NebulaModule {
    private static final FireOverlayOpacity INSTANCE = new FireOverlayOpacity();
    private boolean enabled = true;
    private float fireOpacity = 0.3f;
    
    private FireOverlayOpacity() {}
    
    public static FireOverlayOpacity getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Fire Overlay Opacity";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔥 [Nebula] Fire Overlay Opacity inicializado!");
        if (!NebulaConfig.enableFireOverlayOpacity) return;
        
        System.out.println("🔥 [Nebula] ✅ Opacidade do fogo reduzida!");
    }
    
    @Override
    public void tick() {
        // O trabalho é feito no mixin
    }
    
    public float getOpacity() {
        return fireOpacity;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🔥 [Nebula] Fire Overlay Opacity desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableFireOverlayOpacity;
    }
}
