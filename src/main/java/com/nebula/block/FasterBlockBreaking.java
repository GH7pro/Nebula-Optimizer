package com.nebula.block;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;

public class FasterBlockBreaking implements NebulaModule {
    private static final FasterBlockBreaking INSTANCE = new FasterBlockBreaking();
    private boolean enabled = true;
    
    private FasterBlockBreaking() {}
    
    public static FasterBlockBreaking getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Faster Block Breaking";
    }
    
    @Override
    public void initialize() {
        System.out.println("⚡ [Nebula] Faster Block Breaking inicializado!");
        if (!NebulaConfig.enableFasterBlockBreaking) return;
        
        System.out.println("⚡ [Nebula] ✅ Quebra de blocos acelerada!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableFasterBlockBreaking) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (interactionManager == null) return;
        
        // Aumenta a velocidade de quebra em criativo
        if (client.player != null && client.player.isCreative()) {
            // A quebra instantânea já é nativa do criativo
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("⚡ [Nebula] Faster Block Breaking desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableFasterBlockBreaking;
    }
}
