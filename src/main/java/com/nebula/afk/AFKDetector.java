package com.nebula.afk;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class AFKDetector implements NebulaModule {
    private static final AFKDetector INSTANCE = new AFKDetector();
    private boolean enabled = true;
    private Vec3d lastPosition = Vec3d.ZERO;
    private int stationaryTicks = 0;
    private boolean isAFK = false;
    private int afkTickCounter = 0;
    
    private static final int AFK_THRESHOLD = 1200; // 60 segundos parado
    private static final int CHECK_INTERVAL = 20;  // A cada 1 segundo
    
    private AFKDetector() {}
    
    public static AFKDetector getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Smart AFK Detection";
    }
    
    @Override
    public void initialize() {
        System.out.println("💤 [Nebula] Smart AFK Detection inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableAFKDetection) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        afkTickCounter++;
        if (afkTickCounter % CHECK_INTERVAL != 0) return;
        
        Vec3d currentPos = client.player.getPos();
        
        if (currentPos.distanceTo(lastPosition) < 0.01) {
            stationaryTicks++;
        } else {
            stationaryTicks = 0;
            if (isAFK) {
                exitAFKMode(client);
            }
        }
        
        lastPosition = currentPos;
        
        if (stationaryTicks > AFK_THRESHOLD && !isAFK) {
            enterAFKMode(client);
        }
    }
    
    private void enterAFKMode(MinecraftClient client) {
        isAFK = true;
        System.out.println("💤 [Nebula] Jogador AFK detectado! Ativando modo economia...");
        
        // Reduz drasticamente o consumo
        client.options.getMaxFps().setValue(10);
//         client.options.getViewDistance().setValue(4);
        client.options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FAST);
        client.options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.MINIMAL);
        
    }
    
    private void exitAFKMode(MinecraftClient client) {
        isAFK = false;
        System.out.println("💤 [Nebula] Jogador voltou! Restaurando configurações...");
        
        // Restaura configurações
        client.options.getMaxFps().setValue(60);
//         client.options.getViewDistance().setValue(12);
        client.options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FANCY);
        client.options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.ALL);
    }
    
    public boolean isAFK() {
        return isAFK;
    }
    
    @Override
    public void shutdown() {
        System.out.println("💤 [Nebula] Smart AFK Detection desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableAFKDetection;
    }
}
