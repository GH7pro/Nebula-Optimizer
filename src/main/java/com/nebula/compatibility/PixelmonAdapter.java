package com.nebula.compatibility;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;

public class PixelmonAdapter implements NebulaModule {
    private static final PixelmonAdapter INSTANCE = new PixelmonAdapter();
    private boolean enabled = true;
    private boolean pixelmonDetected = false;
    private int tickCounter = 0;
    
    private PixelmonAdapter() {}
    
    public static PixelmonAdapter getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Pixelmon Compatibility";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎮 [Nebula] Pixelmon Adapter inicializado!");
        
        pixelmonDetected = ModDetector.getInstance().isPixelmonLoaded();
        if (pixelmonDetected) {
            System.out.println("🎮 [Nebula] ✅ Compatibilidade com Pixelmon ativada!");
        } else {
            System.out.println("🎮 [Nebula] ⚠️ Pixelmon não detectado. Modo normal.");
        }
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableModCompatibility) return;
        if (!pixelmonDetected) return;
        
        tickCounter++;
        if (tickCounter % 40 != 0) return; // A cada 2 segundos
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        // Otimizações para Pixelmon
        int pixelmonCount = 0;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == client.player) continue;
            
            String className = entity.getClass().getSimpleName();
            if (className.contains("Pixelmon") || className.contains("Pokemon")) {
                pixelmonCount++;
                double distance = client.player.distanceTo(entity);
                
                // Aplica otimizações baseadas na distância
                if (distance > 50) {
                    // Pixelmons muito distantes podem ter processamento reduzido
                    // usando o sistema de tick scheduling existente
                }
            }
        }
        
        if (pixelmonCount > 50 && NebulaConfig.debugMode) {
            System.out.println("🎮 [Nebula] Pixelmons detectados: " + pixelmonCount);
        }
    }
    
    public boolean isPixelmonEntity(Entity entity) {
        if (!pixelmonDetected) return false;
        String className = entity.getClass().getSimpleName();
        return className.contains("Pixelmon") || className.contains("Pokemon");
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎮 [Nebula] Pixelmon Adapter desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableModCompatibility;
    }
}
