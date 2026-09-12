package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.PerformanceMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

public class DynamicShaders implements NebulaModule {
    private static final DynamicShaders INSTANCE = new DynamicShaders();
    private boolean enabled = true;
    private int currentQuality = 2;
    private int tickCounter = 0;
    
    private static final int CHECK_INTERVAL = 40;
    
    private DynamicShaders() {}
    
    public static DynamicShaders getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Dynamic Shader Quality";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎨 [Nebula] Dynamic Shaders inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;
        
        double fps = PerformanceMonitor.getInstance().getCurrentFPS();
        int targetFPS = NebulaConfig.get().targetFps;
        
        int newQuality = currentQuality;
        
        if (fps < targetFPS * 0.5) {
            newQuality = 0;
        } else if (fps < targetFPS * 0.7) {
            newQuality = 1;
        } else {
            newQuality = 2;
        }
        
        if (newQuality != currentQuality) {
            applyQuality(newQuality);
        }
    }
    
    private void applyQuality(int quality) {
        currentQuality = quality;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        switch (quality) {
            case 0:
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getParticles().setValue(ParticlesMode.MINIMAL);
                System.out.println("🎨 [Nebula] Qualidade: Baixa");
                break;
                
            case 1:
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
                System.out.println("🎨 [Nebula] Qualidade: Média");
                break;
                
            case 2:
                client.options.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
                client.options.getParticles().setValue(ParticlesMode.ALL);
                System.out.println("🎨 [Nebula] Qualidade: Alta");
                break;
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎨 [Nebula] Dynamic Shaders desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableDynamicShaders;
    }
}
