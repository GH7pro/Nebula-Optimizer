package com.nebula.visual;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;

public class PumpkinOverlayDisabler implements NebulaModule {
    private static final PumpkinOverlayDisabler INSTANCE = new PumpkinOverlayDisabler();
    private boolean enabled = true;
    
    private PumpkinOverlayDisabler() {}
    
    public static PumpkinOverlayDisabler getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Pumpkin Overlay Disabler";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎃 [Nebula] Pumpkin Overlay Disabler inicializado!");
        if (!NebulaConfig.enablePumpkinOverlayDisabler) return;
        
        System.out.println("🎃 [Nebula] ✅ Overlay de abóbora desativado!");
    }
    
    @Override
    public void tick() {
        // O trabalho é feito no mixin
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎃 [Nebula] Pumpkin Overlay Disabler desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enablePumpkinOverlayDisabler;
    }
}
