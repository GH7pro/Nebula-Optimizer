package com.nebula.fps;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.PerformanceMonitor;
import net.minecraft.client.MinecraftClient;

public class SmoothFPS implements NebulaModule {
    private static final SmoothFPS INSTANCE = new SmoothFPS();
    private boolean enabled = true;
    private int tickCounter = 0;
    private int currentFpsCap = 60;
    private float frameTimeHistory = 0;
    private int frameCount = 0;
    private boolean initialized = false;
    
    private static final int TARGET_FPS = 60;
    private static final int MIN_FPS = 30;
    private static final int MAX_FPS = 120;
    
    private SmoothFPS() {}
    
    public static SmoothFPS getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Smooth FPS";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎯 [Nebula] Smooth FPS inicializado!");
        if (!NebulaConfig.enableSmoothFPS) return;
        
        // Não tenta acessar client.options aqui
        // Vamos fazer na primeira execução do tick
        initialized = true;
        System.out.println("🎯 [Nebula] ✅ Smooth FPS aguardando inicialização do jogo...");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableSmoothFPS) return;
        if (!initialized) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;
        
        // Só executa uma vez para desbloquear o FPS
        if (tickCounter == 0) {
            client.options.getMaxFps().setValue(260);
            System.out.println("🎯 [Nebula] ✅ FPS desbloqueado!");
        }
        
        tickCounter++;
        if (tickCounter % 20 != 0) return;
        
        double fps = PerformanceMonitor.getInstance().getCurrentFPS();
        
        smoothFramerate(client, fps);
        
        if (NebulaConfig.enableAdaptiveFPS) {
            adaptiveFPS(client, fps);
        }
    }
    
    private void smoothFramerate(MinecraftClient client, double fps) {
        frameCount++;
        frameTimeHistory += (float) (1.0 / Math.max(fps, 1.0));
        
        if (frameCount >= 10) {
            float avgFrameTime = frameTimeHistory / frameCount;
            float smoothFps = 1.0f / avgFrameTime;
            
            if (smoothFps > 0 && smoothFps < 120) {
                currentFpsCap = (int) Math.round(smoothFps);
            }
            
            frameCount = 0;
            frameTimeHistory = 0;
        }
    }
    
    private void adaptiveFPS(MinecraftClient client, double fps) {
        int newCap = currentFpsCap;
        
        if (fps < MIN_FPS) {
            newCap = Math.max(20, (int)fps);
        } else if (fps > TARGET_FPS + 20) {
            newCap = Math.min(MAX_FPS, (int)fps + 10);
        } else if (fps > TARGET_FPS && fps < TARGET_FPS + 20) {
            newCap = TARGET_FPS;
        }
        
        if (newCap != currentFpsCap && NebulaConfig.debugMode) {
            System.out.println("🎯 [Nebula] FPS cap ajustado: " + currentFpsCap + " -> " + newCap);
        }
        currentFpsCap = newCap;
    }
    
    public int getCurrentFpsCap() {
        return currentFpsCap;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎯 [Nebula] Smooth FPS desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableSmoothFPS;
    }
}
