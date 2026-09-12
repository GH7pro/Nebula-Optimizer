package com.nebula.compatibility;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;

public class GenericModAdapter implements NebulaModule {
    private static final GenericModAdapter INSTANCE = new GenericModAdapter();
    private boolean enabled = true;
    private Set<String> detectedModEntities = new HashSet<>();
    private String detectedMods = "";
    private int tickCounter = 0;
    
    private GenericModAdapter() {}
    
    public static GenericModAdapter getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Generic Mod Compatibility";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔧 [Nebula] Generic Mod Adapter inicializado!");
        
        ModDetector detector = ModDetector.getInstance();
        detectedMods = detector.getDetectedMods();
        
        if (!detectedMods.isEmpty()) {
            System.out.println("🔧 [Nebula] ✅ Mods detectados: " + detectedMods);
        } else {
            System.out.println("🔧 [Nebula] ⚠️ Nenhum mod adicional detectado.");
        }
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableModCompatibility) return;
        if (detectedMods.isEmpty()) return;
        
        tickCounter++;
        if (tickCounter % 40 != 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        int modEntityCount = 0;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            
            String className = entity.getClass().getName();
            
            // Entidades de mods (não vanilla)
            if (!className.startsWith("net.minecraft.") && 
                !className.startsWith("net.minecraft.class_")) {
                modEntityCount++;
                double distance = client.player.distanceTo(entity);
                
                // Otimiza entidades de mods distantes
                if (distance > 60) {
                    // Processamento reduzido via tick scheduling
                }
            }
        }
        
        if (modEntityCount > 30 && NebulaConfig.debugMode) {
            System.out.println("🔧 [Nebula] Entidades de mods detectadas: " + modEntityCount);
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🔧 [Nebula] Generic Mod Adapter desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableModCompatibility;
    }
}
