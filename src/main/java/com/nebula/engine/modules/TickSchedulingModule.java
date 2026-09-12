package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TickSchedulingModule implements NebulaModule {
    private static final TickSchedulingModule INSTANCE = new TickSchedulingModule();
    private Map<UUID, Integer> entityTickCounter = new ConcurrentHashMap<>();
    private boolean enabled = true;
    
    private TickSchedulingModule() {}
    
    public static TickSchedulingModule getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Intelligent Tick Scheduling";
    }
    
    @Override
    public void initialize() {
        System.out.println("⏱️ [Nebula] Tick Scheduling Module inicializado!");
    }
    
    @Override
    public void tick() {
        // Limpa o cache periodicamente
        if (entityTickCounter.size() > 1000) {
            entityTickCounter.clear();
        }
    }
    
    public boolean shouldTickEntity(Entity entity) {
        if (!enabled) return true;
        
        NebulaConfig config = NebulaConfig.get();
        if (!config.enableIntelligentTicking) return true;
        
        // Sempre atualiza o jogador
        if (entity instanceof PlayerEntity) return true;
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.player == null) return true;
            
            UUID id = entity.getUuid();
            double distance = client.player.distanceTo(entity);
            int reductionDistance = config.entityTickReductionDistance;
            
            // Entidades muito distantes: NUNCA atualizam (já é tratado no Mixin)
            if (distance > reductionDistance) {
                return false;
            }
            
            // Entidades moderadamente distantes: atualiza a cada 2 ticks
            if (distance > 30) {
                int counter = entityTickCounter.getOrDefault(id, 0);
                if (counter >= 2) {
                    entityTickCounter.put(id, 0);
                    return true;
                } else {
                    entityTickCounter.put(id, counter + 1);
                    return false;
                }
            }
            
            // Entidades próximas: atualiza normalmente
            return true;
            
        } catch (Exception e) {
            return true;
        }
    }
    
    @Override
    public void shutdown() {
        entityTickCounter.clear();
        System.out.println("⏱️ [Nebula] Tick Scheduling Module desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableIntelligentTicking;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
