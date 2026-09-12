package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class AdaptiveEntityLOD implements NebulaModule {
    private static final AdaptiveEntityLOD INSTANCE = new AdaptiveEntityLOD();
    private boolean enabled = true;
    private int entityCount = 0;
    private int currentLOD = 2;
    
    private AdaptiveEntityLOD() {}
    
    public static AdaptiveEntityLOD getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Adaptive Entity LOD";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎯 [Nebula] Adaptive Entity LOD inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        // Conta entidades no mundo
        int count = 0;
        for (Entity entity : client.world.getEntities()) {
            count++;
        }
        entityCount = count;
        
        int newLOD = calculateLOD();
        if (newLOD != currentLOD) {
            currentLOD = newLOD;
            applyLOD(currentLOD);
        }
    }
    
    private int calculateLOD() {
        if (entityCount > 100) {
            return 0;
        } else if (entityCount > 50) {
            return 1;
        } else {
            return 2;
        }
    }
    
    private void applyLOD(int lod) {
        if (NebulaConfig.get().debugMode) {
            System.out.println("🎯 [Nebula] LOD ajustado: " + lod + " (Entidades: " + entityCount + ")");
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎯 [Nebula] Adaptive Entity LOD desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableAdaptiveEntityLOD;
    }
}
