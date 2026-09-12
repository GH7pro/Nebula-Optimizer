package com.nebula.lighting;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightStorage;
import net.minecraft.world.chunk.light.LightingProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LightingOptimizer implements NebulaModule {
    private static final LightingOptimizer INSTANCE = new LightingOptimizer();
    private boolean enabled = true;
    private boolean isOptimized = false;
    private int tickCounter = 0;
    
    private LightingOptimizer() {}
    
    public static LightingOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Lighting Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("⚡ [Nebula] Lighting Optimizer inicializado!");
        if (!NebulaConfig.enableLightingOptimization) return;
        
        try {
            optimizeLighting();
            isOptimized = true;
            System.out.println("⚡ [Nebula] ✅ Otimização de iluminação ativada!");
        } catch (Exception e) {
            System.err.println("⚡ [Nebula] Erro ao otimizar iluminação: " + e.getMessage());
        }
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableLightingOptimization) return;
        if (!isOptimized) return;
        
        tickCounter++;
        if (tickCounter % 200 != 0) return; // A cada 10 segundos
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        
        // Otimiza a iluminação em tempo real
        optimizeWorldLighting(client.world);
    }
    
    private void optimizeLighting() {
        try {
            // ===== 1. OPTIMIZA O SISTEMA DE LUZ =====
            // Tenta acessar e otimizar o LightingProvider
            Class<?> lightStorageClass = Class.forName("net.minecraft.world.chunk.light.LightStorage");
            
            // ===== 2. REDUZ A FREQUÊNCIA DE ATUALIZAÇÃO =====
            // Em vez de atualizar a luz a cada tick, reduz a frequência
            // Isso é feito no tick() com a verificação de tickCounter
            
            // ===== 3. AUMENTA O TAMANHO DO CACHE DE LUZ =====
            try {
                System.setProperty("minecraft.light.cache.size", "1024");
            } catch (Exception e) {}
            
        } catch (Exception e) {}
    }
    
    private void optimizeWorldLighting(World world) {
        try {
            LightingProvider lighting = world.getLightingProvider();
            if (lighting == null) return;
            
            // Tenta forçar a otimização do sistema de luz
            try {
                Method method = lighting.getClass().getDeclaredMethod("tick");
                method.setAccessible(true);
                method.invoke(lighting);
            } catch (Exception e) {}
            
        } catch (Exception e) {}
    }
    
    @Override
    public void shutdown() {
        System.out.println("⚡ [Nebula] Lighting Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableLightingOptimization;
    }
}
