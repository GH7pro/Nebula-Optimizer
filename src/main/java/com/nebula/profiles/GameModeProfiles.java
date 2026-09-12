package com.nebula.profiles;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.gamemodes.GameModeDetector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

public class GameModeProfiles implements NebulaModule {
    private static final GameModeProfiles INSTANCE = new GameModeProfiles();
    private boolean enabled = true;
    private GameModeDetector.GameMode currentMode = GameModeDetector.GameMode.EXPLORING;
    private GameModeDetector.GameMode lastMode = null;
    
    public enum Profile {
        PVP("⚔️ PvP", 0),
        PVE("🗡️ PvE", 1),
        BUILDING("🏗️ Building", 2),
        EXPLORING("🗺️ Exploring", 3),
        AFK("💤 AFK", 4);
        
        public final String name;
        public final int id;
        
        Profile(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }
    
    private GameModeProfiles() {}
    
    public static GameModeProfiles getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Game Mode Profiles";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎮 [Nebula] Game Mode Profiles inicializado!");
        if (!NebulaConfig.enableGameModeProfiles) return;
        
        System.out.println("🎮 [Nebula] ✅ Perfis de modo ativados!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableGameModeProfiles) return;
        
        GameModeDetector detector = GameModeDetector.getInstance();
        detector.tick();
        currentMode = detector.getCurrentMode();
        
        if (currentMode != lastMode) {
            lastMode = currentMode;
            applyProfileForMode(currentMode);
        }
    }
    
    private void applyProfileForMode(GameModeDetector.GameMode mode) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        Profile profile = getProfileForMode(mode);
        System.out.println("🎮 [Nebula] 🔄 Aplicando perfil: " + profile.name);
        
        switch (profile) {
            case PVP:
//                 client.options.getViewDistance().setValue(8);
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
                client.options.getMaxFps().setValue(120);
                client.options.getEntityShadows().setValue(false);
                break;
                
            case PVE:
//                 client.options.getViewDistance().setValue(12);
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
                client.options.getMaxFps().setValue(60);
                client.options.getEntityShadows().setValue(true);
                break;
                
            case BUILDING:
//                 client.options.getViewDistance().setValue(16);
                client.options.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
                client.options.getParticles().setValue(ParticlesMode.ALL);
                client.options.getMaxFps().setValue(60);
                client.options.getEntityShadows().setValue(true);
                break;
                
            case EXPLORING:
//                 client.options.getViewDistance().setValue(12);
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                client.options.getParticles().setValue(ParticlesMode.ALL);
                client.options.getMaxFps().setValue(60);
                client.options.getEntityShadows().setValue(true);
                break;
                
            case AFK:
//                 client.options.getViewDistance().setValue(4);
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getParticles().setValue(ParticlesMode.MINIMAL);
                client.options.getMaxFps().setValue(15);
                client.options.getEntityShadows().setValue(false);
                break;
        }
    }
    
    private Profile getProfileForMode(GameModeDetector.GameMode mode) {
        switch (mode) {
            case COMBAT:
                return Profile.PVP;
            case MINING:
                return Profile.PVE;
            case BUILDING:
                return Profile.BUILDING;
            case EXPLORING:
                return Profile.EXPLORING;
            case AFK:
                return Profile.AFK;
            default:
                return Profile.EXPLORING;
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎮 [Nebula] Game Mode Profiles desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableGameModeProfiles;
    }
}
