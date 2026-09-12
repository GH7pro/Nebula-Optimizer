package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLOD implements NebulaModule {
    private static final EntityLOD INSTANCE = new EntityLOD();
    private boolean enabled = true;
    private ConcurrentHashMap<UUID, Integer> entityLODLevel = new ConcurrentHashMap<>();
    
    private static final int LOD_FULL = 2;
    private static final int LOD_SIMPLE = 1;
    private static final int LOD_MINIMAL = 0;
    
    private EntityLOD() {}
    
    public static EntityLOD getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Entity LOD System";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎯 [Nebula] Entity LOD System inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            
            double distance = client.player.distanceTo(entity);
            UUID id = entity.getUuid();
            int newLOD = calculateLOD(distance);
            int currentLOD = entityLODLevel.getOrDefault(id, LOD_FULL);
            
            if (newLOD != currentLOD) {
                applyLOD(entity, newLOD);
                entityLODLevel.put(id, newLOD);
            }
        }
    }
    
    private int calculateLOD(double distance) {
        if (distance < 20) {
            return LOD_FULL;
        } else if (distance < 50) {
            return LOD_SIMPLE;
        } else {
            return LOD_MINIMAL;
        }
    }
    
    private void applyLOD(Entity entity, int lod) {
        // Em 1.20.1, não temos setAiDisabled diretamente
        // Usamos a abordagem de desativar AI via NBT ou simplesmente ignoramos
        // O culling já faz um bom trabalho
        
        if (lod == LOD_MINIMAL && entity instanceof MobEntity) {
            // MobEntity tem setAiDisabled
            try {
                ((MobEntity) entity).setAiDisabled(true);
            } catch (Exception e) {
                // Ignora
            }
        } else if (entity instanceof MobEntity) {
            try {
                ((MobEntity) entity).setAiDisabled(false);
            } catch (Exception e) {
                // Ignora
            }
        }
    }
    
    @Override
    public void shutdown() {
        entityLODLevel.clear();
        System.out.println("🎯 [Nebula] Entity LOD System desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableEntityLOD;
    }
}
