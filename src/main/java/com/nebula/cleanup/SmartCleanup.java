package com.nebula.cleanup;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SmartCleanup implements NebulaModule {
    private static final SmartCleanup INSTANCE = new SmartCleanup();
    private boolean enabled = true;
    private int tickCounter = 0;
    private ConcurrentHashMap<UUID, Integer> entityAge = new ConcurrentHashMap<>();
    private int cleanedCount = 0;
    
    // Configurações
    private static final int MAX_ITEMS = 50;
    private static final int ITEM_LIFETIME = 600; // 30 segundos
    private static final int MOB_STUCK_TIMEOUT = 1200; // 60 segundos
    private static final int MAX_MOBS = 80;
    private static final int MAX_ENTITIES = 200;
    
    private SmartCleanup() {}
    
    public static SmartCleanup getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Smart Cleanup";
    }
    
    @Override
    public void initialize() {
        System.out.println("🧹 [Nebula] Smart Cleanup inicializado!");
        if (!NebulaConfig.enableSmartCleanupAdvanced) return;
        
        System.out.println("🧹 [Nebula] ✅ Limpeza inteligente ativada!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableSmartCleanupAdvanced) return;
        
        tickCounter++;
        if (tickCounter % 100 != 0) return; // A cada 5 segundos
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        PlayerEntity player = client.player;
        List<Entity> toRemove = new ArrayList<>();
        int itemCount = 0;
        int mobCount = 0;
        int totalEntities = 0;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            
            totalEntities++;
            UUID id = entity.getUuid();
            int age = entityAge.getOrDefault(id, 0) + 1;
            entityAge.put(id, age);
            double distance = player.distanceTo(entity);
            
            // ===== 1. LIMPEZA DE ITENS =====
            if (entity instanceof ItemEntity) {
                itemCount++;
                if (itemCount > MAX_ITEMS || age > ITEM_LIFETIME) {
                    toRemove.add(entity);
                }
            }
            
            // ===== 2. LIMPEZA DE MOBS PRESOS =====
            if (entity instanceof MobEntity) {
                mobCount++;
                if (mobCount > MAX_MOBS) {
                    toRemove.add(entity);
                } else if (age > MOB_STUCK_TIMEOUT && distance > 20) {
                    toRemove.add(entity);
                }
            }
            
            // ===== 3. LIMPEZA GERAL =====
            if (totalEntities > MAX_ENTITIES && distance > 30) {
                toRemove.add(entity);
            }
        }
        
        // ===== REMOVE ENTIDADES =====
        for (Entity entity : toRemove) {
            try {
                entity.remove(Entity.RemovalReason.DISCARDED);
                cleanedCount++;
                entityAge.remove(entity.getUuid());
            } catch (Exception e) {}
        }
        
        if (NebulaConfig.debugMode && cleanedCount > 0 && tickCounter % 500 == 0) {
            System.out.println("🧹 [Nebula] Limpeza: " + cleanedCount + " entidades removidas");
            System.out.println("🧹 [Nebula] 📊 Itens: " + itemCount + "/" + MAX_ITEMS + " | Mobs: " + mobCount + "/" + MAX_MOBS);
        }
    }
    
    public int getCleanedCount() {
        return cleanedCount;
    }
    
    public void resetStats() {
        cleanedCount = 0;
    }
    
    @Override
    public void shutdown() {
        entityAge.clear();
        System.out.println("🧹 [Nebula] Smart Cleanup desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableSmartCleanupAdvanced;
    }
}
