package com.nebula.camera;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

public class ReduceShakeBobbing implements NebulaModule {
    private static final ReduceShakeBobbing INSTANCE = new ReduceShakeBobbing();
    private boolean enabled = true;
    private boolean originalBobbing = true;
    
    private ReduceShakeBobbing() {}
    
    public static ReduceShakeBobbing getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Reduce Shake & Bobbing";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎯 [Nebula] Reduce Shake & Bobbing inicializado!");
        if (!NebulaConfig.enableReduceShakeBobbing) return;
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.options == null) return;
            
            originalBobbing = client.options.getBobView().getValue();
            client.options.getBobView().setValue(false);
            
            System.out.println("🎯 [Nebula] ✅ Balanço reduzido!");
        } catch (Exception e) {
            System.err.println("🎯 [Nebula] Erro ao reduzir shake: " + e.getMessage());
        }
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableReduceShakeBobbing) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;
        
        if (client.options.getBobView().getValue()) {
            client.options.getBobView().setValue(false);
        }
    }
    
    @Override
    public void shutdown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null) {
            client.options.getBobView().setValue(originalBobbing);
        }
        System.out.println("🎯 [Nebula] Reduce Shake & Bobbing desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableReduceShakeBobbing;
    }
}
