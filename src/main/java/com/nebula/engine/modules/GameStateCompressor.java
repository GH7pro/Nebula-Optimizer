package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;

public class GameStateCompressor implements NebulaModule {
    private static final GameStateCompressor INSTANCE = new GameStateCompressor();
    private boolean enabled = true;
    
    private GameStateCompressor() {}
    
    public static GameStateCompressor getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Game State Compression";
    }
    
    @Override
    public void initialize() {
        System.out.println("📦 [Nebula] Game State Compressor inicializado!");
    }
    
    @Override
    public void tick() {
        // O trabalho real seria feito nos Mixins do World
    }
    
    @Override
    public void shutdown() {
        System.out.println("📦 [Nebula] Game State Compressor desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableStateCompression;
    }
}
