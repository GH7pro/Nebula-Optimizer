package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.world.World;

public class NightVisionOptimizer implements NebulaModule {
    private static final NightVisionOptimizer INSTANCE = new NightVisionOptimizer();
    private boolean enabled = true;
    private int tickCounter = 0;
    private boolean wasNight = false;
    private GraphicsMode originalGraphicsMode = GraphicsMode.FANCY;
    private int originalBrightness = 50;
    
    private NightVisionOptimizer() {}
    
    public static NightVisionOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Night Vision Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("🌙 [Nebula] Night Vision Optimizer inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableNightVisionOptimizer) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        tickCounter++;
        if (tickCounter % 40 != 0) return;
        
        World world = client.world;
        long time = world.getTimeOfDay() % 24000;
        boolean isNight = time > 13000 && time < 23000;
        
        if (isNight && !wasNight) {
            enterNightMode(client);
            wasNight = true;
        } else if (!isNight && wasNight) {
            exitNightMode(client);
            wasNight = false;
        }
    }
    
    private void enterNightMode(MinecraftClient client) {
        if (NebulaConfig.debugMode) {
            System.out.println("🌙 [Nebula] 🌃 Noite detectada! Ativando otimizações...");
        }
        
        try {
            // ===== SALVA CONFIGURAÇÕES ORIGINAIS =====
            originalGraphicsMode = client.options.getGraphicsMode().getValue();
            
            // ===== REDUZ QUALIDADE GRÁFICA =====
            // Gráficos: FAST (menos processamento de luz)
            client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
            
            // Aumenta brilho (menos processamento de sombras)
            client.options.getGamma().setValue(1.5);
            
            if (NebulaConfig.debugMode) {
                System.out.println("🌙 [Nebula] ✅ Gráficos: FAST");
            }
            
        } catch (Exception e) {
            if (NebulaConfig.debugMode) {
                System.err.println("🌙 [Nebula] Erro ao ativar modo noturno: " + e.getMessage());
            }
        }
    }
    
    private void exitNightMode(MinecraftClient client) {
        if (NebulaConfig.debugMode) {
            System.out.println("🌙 [Nebula] ☀️ Dia! Restaurando configurações...");
        }
        
        try {
            // ===== RESTAURA CONFIGURAÇÕES =====
            client.options.getGraphicsMode().setValue(originalGraphicsMode);
            client.options.getGamma().setValue(0.5);
            
            if (NebulaConfig.debugMode) {
                System.out.println("🌙 [Nebula] ✅ Configurações restauradas!");
            }
            
        } catch (Exception e) {
            if (NebulaConfig.debugMode) {
                System.err.println("🌙 [Nebula] Erro ao restaurar: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🌙 [Nebula] Night Vision Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNightVisionOptimizer;
    }
}
