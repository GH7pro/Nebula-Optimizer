package com.nebula.logic;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class LogicOptimizer implements NebulaModule {
    private static final LogicOptimizer INSTANCE = new LogicOptimizer();
    private boolean enabled = true;
    private int tickCounter = 0;
    
    private LogicOptimizer() {}
    
    public static LogicOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Logic Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("🧠 [Nebula] Logic Optimizer inicializado!");
        if (!NebulaConfig.enableLogicOptimization) return;
        
        System.out.println("🧠 [Nebula] ✅ Otimização da lógica ativada!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableLogicOptimization) return;
        
        tickCounter++;
        if (tickCounter % 20 != 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        optimizeMobAI(client);
    }
    
    private void optimizeMobAI(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null) return;
        
        // Converte Iterable para List
        List<Entity> entities = new ArrayList<>();
        for (Entity entity : client.world.getEntities()) {
            entities.add(entity);
        }
        
        int mobCount = 0;
        
        for (Entity entity : entities) {
            if (entity == player) continue;
            if (entity instanceof MobEntity) {
                mobCount++;
                double distance = player.distanceTo(entity);
                
                if (distance > 50) {
                    try {
                        ((MobEntity) entity).setAiDisabled(true);
                    } catch (Exception e) {}
                } else if (distance > 30) {
                    if (tickCounter % 2 == 0) {
                        try {
                            ((MobEntity) entity).setAiDisabled(false);
                        } catch (Exception e) {}
                    } else {
                        try {
                            ((MobEntity) entity).setAiDisabled(true);
                        } catch (Exception e) {}
                    }
                } else {
                    try {
                        ((MobEntity) entity).setAiDisabled(false);
                    } catch (Exception e) {}
                }
            }
        }
        
        if (NebulaConfig.debugMode && mobCount > 20 && tickCounter % 100 == 0) {
            System.out.println("🧠 [Nebula] Mobs ativos: " + mobCount);
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🧠 [Nebula] Logic Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableLogicOptimization;
    }
}
