package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;

public class MemoryManager implements NebulaModule {
    private static final MemoryManager INSTANCE = new MemoryManager();
    private boolean enabled = true;
    private boolean emergencyMode = false;
    private long maxMemory;
    private long usedMemory;
    private int tickCounter = 0;
    
    private static final double WARNING_THRESHOLD = 0.75;
    private static final double CRITICAL_THRESHOLD = 0.85;
    private static final int CHECK_INTERVAL = 100;
    
    private MemoryManager() {}
    
    public static MemoryManager getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Adaptive Memory Management";
    }
    
    @Override
    public void initialize() {
        maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("💾 [Nebula] Memory Manager inicializado!");
        System.out.println("💾 [Nebula] Memória máxima: " + (maxMemory / 1024 / 1024) + " MB");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;
        
        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double usagePercent = (double) usedMemory / maxMemory;
        
        if (usagePercent > CRITICAL_THRESHOLD && !emergencyMode) {
            activateEmergency();
        } else if (usagePercent < WARNING_THRESHOLD && emergencyMode) {
            deactivateEmergency();
        } else if (usagePercent > WARNING_THRESHOLD) {
            performCleanup();
        }
    }
    
    private void activateEmergency() {
        emergencyMode = true;
        System.out.println("⚠️ [Nebula] EMERGÊNCIA: Memória crítica! Ativando modo econômico...");
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
//             client.options.getViewDistance().setValue(8);
            client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        }
    }
    
    private void deactivateEmergency() {
        emergencyMode = false;
        System.out.println("✅ [Nebula] Memória normalizada. Saindo do modo econômico...");
        
        // Antes, isto só imprimia a mensagem e nunca restaurava a render
        // distance de verdade — ficava travada em 8 pra sempre depois de
        // uma emergência. Agora volta pro valor real do jogador.
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
//             client.options.getViewDistance().setValue(NebulaConfig.userRenderDistance);
            client.options.getParticles().setValue(ParticlesMode.ALL);
        }
    }
    
    private void performCleanup() {
        if (tickCounter % (CHECK_INTERVAL * 2) == 0) {
        }
    }
    
    public double getMemoryUsage() {
        usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return (double) usedMemory / maxMemory * 100;
    }
    
    @Override
    public void shutdown() {
        System.out.println("💾 [Nebula] Memory Manager desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableMemoryManagement;
    }
}
