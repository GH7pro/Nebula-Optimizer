package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

public class SmartSleepMode implements NebulaModule {
    private static final SmartSleepMode INSTANCE = new SmartSleepMode();
    private boolean enabled = true;
    private int idleTicks = 0;
    private boolean sleeping = false;
    
    private static final int IDLE_THRESHOLD = 600; // 30 segundos
    
    private SmartSleepMode() {}
    
    public static SmartSleepMode getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Smart Sleep Mode";
    }
    
    @Override
    public void initialize() {
        System.out.println("💤 [Nebula] Smart Sleep Mode inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        boolean isIdle = client.player.getVelocity().length() < 0.01;
        
        if (isIdle) {
            idleTicks++;
            if (idleTicks > IDLE_THRESHOLD && !sleeping) {
                sleeping = true;
                enterSleepMode();
            }
        } else {
            idleTicks = 0;
            if (sleeping) {
                sleeping = false;
                exitSleepMode();
            }
        }
    }
    
    private void enterSleepMode() {
        System.out.println("💤 [Nebula] Jogador parado! Entrando em modo sleep...");
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        client.options.getMaxFps().setValue(15);
//         client.options.getViewDistance().setValue(6);
    }
    
    private void exitSleepMode() {
        System.out.println("💤 [Nebula] Jogador ativo! Saindo do modo sleep...");
    }
    
    @Override
    public void shutdown() {
        System.out.println("💤 [Nebula] Smart Sleep Mode desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableSmartSleep;
    }
}
