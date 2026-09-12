package com.nebula.entity;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarEntitySimplifier implements NebulaModule {
    private static final FarEntitySimplifier INSTANCE = new FarEntitySimplifier();
    private boolean enabled = true;
    private int tickCounter = 0;
    private Map<UUID, Integer> entityLOD = new HashMap<>();
    
    private static final int LOD_FULL = 2;
    private static final int LOD_SIMPLE = 1;
    private static final int LOD_MINIMAL = 0;
    
    private FarEntitySimplifier() {}
    
    public static FarEntitySimplifier getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Far Entity Simplifier";
    }
    
    @Override
    public void initialize() {
        System.out.println("📡 [Nebula] Far Entity Simplifier inicializado!");
        if (!NebulaConfig.enableFarEntitySimplifier) return;
        
        System.out.println("📡 [Nebula] ✅ Simplificação de entidades ativada!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableFarEntitySimplifier) return;
        
        tickCounter++;
        if (tickCounter % 20 != 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        PlayerEntity player = client.player;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (entity instanceof MobEntity) {
                double distance = player.distanceTo(entity);
                UUID id = entity.getUuid();
                int newLOD = calculateLOD(distance);
                int currentLOD = entityLOD.getOrDefault(id, LOD_FULL);
                
                if (newLOD != currentLOD) {
                    applyLOD(entity, newLOD);
                    entityLOD.put(id, newLOD);
                }
            }
        }
    }
    
    private int calculateLOD(double distance) {
        if (distance < 20) {
            return LOD_FULL; // Entidade completa
        } else if (distance < 50) {
            return LOD_SIMPLE; // Versão simplificada
        } else {
            return LOD_MINIMAL; // Mínimo processamento
        }
    }
    
    private void applyLOD(Entity entity, int lod) {
        if (entity instanceof MobEntity) {
            MobEntity mob = (MobEntity) entity;
            
            switch (lod) {
                case LOD_FULL:
                    // Entidade normal
                    try {
                        mob.setAiDisabled(false);
                        mob.setNoGravity(false);
                    } catch (Exception e) {}
                    break;
                    
                case LOD_SIMPLE:
                    // IA reduzida
                    try {
                        mob.setAiDisabled(true);
                        mob.setNoGravity(false);
                    } catch (Exception e) {}
                    break;
                    
                case LOD_MINIMAL:
                    // Entidade quase estática
                    try {
                        mob.setAiDisabled(true);
                        mob.setNoGravity(true);
                    } catch (Exception e) {}
                    break;
            }
        }
    }
    
    @Override
    public void shutdown() {
        entityLOD.clear();
        System.out.println("📡 [Nebula] Far Entity Simplifier desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableFarEntitySimplifier;
    }
}
