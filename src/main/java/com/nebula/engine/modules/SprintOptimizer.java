package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class SprintOptimizer implements NebulaModule {
    private static final SprintOptimizer INSTANCE = new SprintOptimizer();
    private boolean enabled = true;
    private int tickCounter = 0;
    private boolean wasSprinting = false;
    
    // Configurações
    private static final int CHECK_INTERVAL = 10; // A cada 0.5 segundos
    private static final int SPRINT_RENDER_DISTANCE = 8; // Render distance durante corrida
    
    private SprintOptimizer() {}
    
    public static SprintOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Sprint Optimization";
    }
    
    @Override
    public void initialize() {
        System.out.println("🏃 [Nebula] Sprint Optimizer inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableSprintOptimization) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;
        
        ClientPlayerEntity player = client.player;
        boolean isSprinting = player.isSprinting();
        
        if (isSprinting && !wasSprinting) {
            enterSprintMode(client);
            wasSprinting = true;
        } else if (!isSprinting && wasSprinting) {
            exitSprintMode(client);
            wasSprinting = false;
        }
    }
    
    private void enterSprintMode(MinecraftClient client) {
        if (NebulaConfig.debugMode) {
            System.out.println("🏃 [Nebula] 🏃 Correndo! Ativando otimizações...");
        }
        
        try {
            // ===== REDUZ DISTÂNCIA DE RENDERIZAÇÃO =====
            int currentDistance = client.options.getViewDistance().getValue();
            if (currentDistance > SPRINT_RENDER_DISTANCE) {
//                 client.options.getViewDistance().setValue(SPRINT_RENDER_DISTANCE);
                if (NebulaConfig.debugMode) {
                    System.out.println("🏃 [Nebula] ✅ Render distance: " + SPRINT_RENDER_DISTANCE);
                }
            }
            
            // ===== REDUZ FPS DURANTE CORRIDA =====
            int currentFps = client.options.getMaxFps().getValue();
            if (currentFps > 45) {
                client.options.getMaxFps().setValue(45);
                if (NebulaConfig.debugMode) {
                    System.out.println("🏃 [Nebula] ✅ FPS limitado a 45");
                }
            }
            
        } catch (Exception e) {
            if (NebulaConfig.debugMode) {
                System.err.println("🏃 [Nebula] Erro ao ativar modo corrida: " + e.getMessage());
            }
        }
    }
    
    private void exitSprintMode(MinecraftClient client) {
        if (NebulaConfig.debugMode) {
            System.out.println("🏃 [Nebula] 🚶 Parou de correr! Restaurando...");
        }
        
        try {
            // ===== RESTAURA DISTÂNCIA (para o que o jogador realmente escolheu, não um número fixo) =====
//             client.options.getViewDistance().setValue(NebulaConfig.userRenderDistance);
            
            // ===== RESTAURA FPS =====
            client.options.getMaxFps().setValue(60);
            
            if (NebulaConfig.debugMode) {
                System.out.println("🏃 [Nebula] ✅ Configurações restauradas!");
            }
            
        } catch (Exception e) {
            if (NebulaConfig.debugMode) {
                System.err.println("🏃 [Nebula] Erro ao restaurar: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🏃 [Nebula] Sprint Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableSprintOptimization;
    }
}
