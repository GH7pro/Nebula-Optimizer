package com.nebula.entity;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityCleanup implements NebulaModule {
    private static final EntityCleanup INSTANCE = new EntityCleanup();
    private boolean enabled = true;
    private int tickCounter = 0;
    private Map<UUID, Integer> entityAge = new HashMap<>();
    private int cleanedEntities = 0;
    
    private static final int ITEM_LIFETIME = 300; // 15 segundos
    private static final int MOB_STUCK_TIMEOUT = 600; // 30 segundos
    private static final int MAX_ITEMS = 100;
    
    private EntityCleanup() {}
    
    public static EntityCleanup getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Entity Cleanup";
    }
    
    @Override
    public void initialize() {
        System.out.println("🧹 [Nebula] Entity Cleanup inicializado!");
        if (!NebulaConfig.enableEntityCleanup) return;
        
        System.out.println("🧹 [Nebula] ✅ Limpeza de entidades ativada!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableEntityCleanup) return;
        
        tickCounter++;
        if (tickCounter % 100 != 0) return; // A cada 5 segundos
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        PlayerEntity player = client.player;
        List<Entity> toRemove = new ArrayList<>();
        int itemCount = 0;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            
            UUID id = entity.getUuid();
            int age = entityAge.getOrDefault(id, 0) + 1;
            entityAge.put(id, age);
            
            // ===== LIMPEZA DE ITENS =====
            if (entity instanceof ItemEntity) {
                itemCount++;
                double distance = player.distanceTo(entity);
                
                // Itens muito distantes ou velhos
                if (distance > 30 || age > ITEM_LIFETIME) {
                    toRemove.add(entity);
                }
            }
            
            // ===== LIMPEZA DE MOBS PRESOS =====
            if (entity instanceof MobEntity) {
                double distance = player.distanceTo(entity);
                
                // Mobs presos (parados por muito tempo)
                if (age > MOB_STUCK_TIMEOUT && distance > 20) {
                    toRemove.add(entity);
                }
            }
        }
        
        // ===== REMOVE ENTIDADES =====
        for (Entity entity : toRemove) {
            try {
                entity.remove(Entity.RemovalReason.DISCARDED);
                cleanedEntities++;
                entityAge.remove(entity.getUuid());
            } catch (Exception e) {}
        }
        
        // ===== LIMPEZA DE ITENS EXCESSIVOS =====
        if (itemCount > MAX_ITEMS) {
            // Remove itens extras
            for (Entity entity : client.world.getEntities()) {
                if (entity instanceof ItemEntity && !toRemove.contains(entity)) {
                    if (itemCount <= MAX_ITEMS) break;
                    try {
                        entity.remove(Entity.RemovalReason.DISCARDED);
                        cleanedEntities++;
                        itemCount--;
                    } catch (Exception e) {}
                }
            }
        }
        
        if (NebulaConfig.debugMode && cleanedEntities > 0 && tickCounter % 200 == 0) {
            System.out.println("🧹 [Nebula] Entidades limpas: " + cleanedEntities);
        }
    }
    
    public int getCleanedEntities() {
        return cleanedEntities;
    }
    
    @Override
    public void shutdown() {
        entityAge.clear();
        System.out.println("🧹 [Nebula] Entity Cleanup desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableEntityCleanup;
    }
}
