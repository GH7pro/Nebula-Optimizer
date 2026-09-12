package com.nebula.entity;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ReducedCollisions implements NebulaModule {
    private static final ReducedCollisions INSTANCE = new ReducedCollisions();
    private boolean enabled = true;
    private int tickCounter = 0;
    private int collisionsReduced = 0;
    
    private ReducedCollisions() {}
    
    public static ReducedCollisions getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Reduced Collisions";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎯 [Nebula] Reduced Collisions inicializado!");
        if (!NebulaConfig.enableReducedCollisions) return;
        
        System.out.println("🎯 [Nebula] ✅ Colisões reduzidas ativadas!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableReducedCollisions) return;
        
        tickCounter++;
        if (tickCounter % 20 != 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        PlayerEntity player = client.player;
        List<Entity> entities = new ArrayList<>();
        
        for (Entity entity : client.world.getEntities()) {
            entities.add(entity);
        }
        
        for (Entity entity : entities) {
            if (entity == player) continue;
            if (entity instanceof MobEntity) {
                double distance = player.distanceTo(entity);
                
                // Reduz colisões para entidades distantes
                if (distance > 20) {
                    try {
                        // Reduz a sensibilidade de colisão
                        entity.setNoGravity(true);
                    } catch (Exception e) {}
                } else if (distance > 10) {
                    try {
                        entity.setNoGravity(false);
                    } catch (Exception e) {}
                }
            }
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎯 [Nebula] Reduced Collisions desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableReducedCollisions;
    }
}
