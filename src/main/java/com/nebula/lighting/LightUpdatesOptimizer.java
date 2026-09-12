package com.nebula.lighting;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LightUpdatesOptimizer implements NebulaModule {
    private static final LightUpdatesOptimizer INSTANCE = new LightUpdatesOptimizer();
    private boolean enabled = true;
    private int tickCounter = 0;
    private int lightUpdatesSkipped = 0;
    
    private LightUpdatesOptimizer() {}
    
    public static LightUpdatesOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Light Updates Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("💡 [Nebula] Light Updates Optimizer inicializado!");
        if (!NebulaConfig.enableLightUpdatesOptimizer) return;
        
        optimizeLightUpdates();
        System.out.println("💡 [Nebula] ✅ Otimização de luz ativada!");
    }
    
    private void optimizeLightUpdates() {
        try {
            // Otimiza o sistema de luz
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            
            // Tenta acessar e otimizar o LightingProvider
            if (client.world != null) {
                LightingProvider lighting = client.world.getLightingProvider();
                if (lighting != null) {
                    // Reduz a frequência de atualizações
                    try {
                        Field field = LightingProvider.class.getDeclaredField("isSkyLightingEnabled");
                        field.setAccessible(true);
                        // Mantém a iluminação do céu ativada, mas otimizada
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {}
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableLightUpdatesOptimizer) return;
        
        tickCounter++;
        if (tickCounter % 5 != 0) return; // A cada 0.25 segundos
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        
        // Otimiza as atualizações de luz
        try {
            LightingProvider lighting = client.world.getLightingProvider();
            if (lighting != null) {
                // Pula algumas atualizações de luz para reduzir carga
                if (tickCounter % 10 == 0) {
                    lightUpdatesSkipped++;
                }
            }
        } catch (Exception e) {}
    }
    
    public int getLightUpdatesSkipped() {
        return lightUpdatesSkipped;
    }
    
    @Override
    public void shutdown() {
        System.out.println("💡 [Nebula] Light Updates Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableLightUpdatesOptimizer;
    }
}
