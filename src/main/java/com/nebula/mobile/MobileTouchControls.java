package com.nebula.mobile;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

public class MobileTouchControls implements NebulaModule {
    private static final MobileTouchControls INSTANCE = new MobileTouchControls();
    private boolean enabled = true;
    private boolean isMobile = false;
    private int tickCounter = 0;
    
    private MobileTouchControls() {}
    
    public static MobileTouchControls getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Mobile Touch Controls";
    }
    
    @Override
    public void initialize() {
        System.out.println("📱 [Nebula] Mobile Touch Controls inicializado!");
        if (!NebulaConfig.enableMobileTouchControls) return;
        
        detectMobile();
        
        if (isMobile) {
            applyTouchOptimizations();
            System.out.println("📱 [Nebula] ✅ Otimizações touch aplicadas!");
        } else {
            System.out.println("📱 [Nebula] 💻 Modo desktop detectado.");
        }
    }
    
    private void detectMobile() {
        // Detecta se está no Android
        String os = System.getProperty("os.name").toLowerCase();
        String vendor = System.getProperty("java.vendor").toLowerCase();
        
        isMobile = os.contains("android") || 
                    vendor.contains("android") ||
                    System.getProperty("java.runtime.name").toLowerCase().contains("android");
        
        // Verifica se está no PojavLauncher / FCL
        try {
            Class.forName("net.kdt.pojavlaunch.PojavLauncher");
            isMobile = true;
        } catch (ClassNotFoundException e) {}
    }
    
    private void applyTouchOptimizations() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        // ===== AUMENTA O TAMANHO DA INTERFACE =====
        client.options.getGuiScale().setValue(3);
        
        // ===== AUMENTA A SENSIBILIDADE DO MOUSE =====
        client.options.getMouseSensitivity().setValue(0.15);
        
        // ===== DESATIVA O VIEW BOBBING =====
        client.options.getBobView().setValue(false);
        
        // ===== ATIVA O TOUCHSCREEN =====
        System.setProperty("minecraft.touchscreen", "true");
        
        // ===== REDUZ A QUALIDADE PARA ECONOMIA =====
        if (NebulaConfig.enableMobilePowerSaving) {
//             client.options.getViewDistance().setValue(8);
            client.options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FAST);
            client.options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.DECREASED);
        }
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableMobileTouchControls) return;
        if (!isMobile) return;
        
        tickCounter++;
        if (tickCounter % 1200 != 0) return; // A cada 60 segundos
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        // Mantém as otimizações
        if (client.options.getGuiScale().getValue() < 3) {
            client.options.getGuiScale().setValue(3);
        }
    }
    
    public boolean isMobile() {
        return isMobile;
    }
    
    @Override
    public void shutdown() {
        System.out.println("📱 [Nebula] Mobile Touch Controls desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableMobileTouchControls;
    }
}
