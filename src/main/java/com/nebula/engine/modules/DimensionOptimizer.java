package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;

public class DimensionOptimizer implements NebulaModule {
    private static final DimensionOptimizer INSTANCE = new DimensionOptimizer();
    private boolean enabled = true;
    private String currentDimension = "overworld";
    
    private DimensionOptimizer() {}
    
    public static DimensionOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Dimension Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("🌍 [Nebula] Dimension Optimizer inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        String dim = client.world.getDimensionKey().getValue().getPath();
        
        if (!dim.equals(currentDimension)) {
            currentDimension = dim;
            optimizeDimension(dim);
        }
    }
    
    private void optimizeDimension(String dimension) {
        System.out.println("🌍 [Nebula] Otimizando dimensão: " + dimension);
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        switch (dimension) {
            case "the_nether":
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
//                 client.options.getViewDistance().setValue(10);
                System.out.println("🌍 [Nebula] 🔥 Nether: Partículas reduzidas");
                break;
                
            case "the_end":
//                 client.options.getViewDistance().setValue(12);
                System.out.println("🌍 [Nebula] 🌌 End: Otimizações aplicadas");
                break;
                
            default:
                client.options.getParticles().setValue(ParticlesMode.ALL);
//                 client.options.getViewDistance().setValue(12);
                System.out.println("🌍 [Nebula] 🌿 Overworld: Configurações normais");
                break;
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🌍 [Nebula] Dimension Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableDimensionOptimizer;
    }
}
