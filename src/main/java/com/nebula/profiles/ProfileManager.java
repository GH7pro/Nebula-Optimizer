package com.nebula.profiles;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

public class ProfileManager {
    private static final ProfileManager INSTANCE = new ProfileManager();
    private DeviceDetector.DeviceType currentProfile = DeviceDetector.DeviceType.UNKNOWN;
    private boolean applied = false;
    
    private ProfileManager() {}
    
    public static ProfileManager getInstance() {
        return INSTANCE;
    }
    
    public void initialize() {
        System.out.println("📱 [Nebula] Profile Manager inicializado!");
        
        if (!NebulaConfig.enableAutoProfile) {
            System.out.println("📱 [Nebula] ⚠️ Auto-Profile desativado!");
            return;
        }
        
        DeviceDetector detector = DeviceDetector.getInstance();
        currentProfile = detector.getDeviceType();
        
        System.out.println("📱 [Nebula] Perfil detectado: " + currentProfile.getDisplayName());
        
        // Aplica o perfil no próximo tick (depois do jogo iniciar)
        applyProfileDelayed();
    }
    
    private void applyProfileDelayed() {
        // Espera o jogo inicializar completamente
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Espera 1 segundo
            } catch (InterruptedException e) {}
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                applyProfile();
            }
        }).start();
    }
    
    public void applyProfile() {
        if (!NebulaConfig.enableAutoProfile) return;
        if (applied) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            // Tenta novamente depois
            applyProfileDelayed();
            return;
        }
        
        System.out.println("📱 [Nebula] Aplicando perfil: " + currentProfile.getDisplayName());
        
        switch (currentProfile) {
            case GAMING_PC:
                applyGamingProfile(client);
                break;
            case DESKTOP:
                applyDesktopProfile(client);
                break;
            case LAPTOP:
                applyLaptopProfile(client);
                break;
            case MOBILE:
                applyMobileProfile(client);
                break;
            case LOW_END:
                applyLowEndProfile(client);
                break;
            default:
                applyDefaultProfile(client);
                break;
        }
        
        applied = true;
        System.out.println("📱 [Nebula] ✅ Perfil aplicado com sucesso!");
    }
    
    private void applyGamingProfile(MinecraftClient client) {
        System.out.println("📱 [Nebula] 🎮 Perfil GAMING PC:");
        System.out.println("📱 [Nebula]   - Qualidade máxima");
        System.out.println("📱 [Nebula]   - FPS: 120");
        System.out.println("📱 [Nebula]   - Render Distance: 16");
        
//         client.options.getViewDistance().setValue(16);
//         client.options.getSimulationDistance().setValue(12);
        client.options.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
        client.options.getParticles().setValue(ParticlesMode.ALL);
        client.options.getMaxFps().setValue(120);
        client.options.getEntityShadows().setValue(true);
    }
    
    private void applyDesktopProfile(MinecraftClient client) {
        System.out.println("📱 [Nebula] 🖥️ Perfil DESKTOP:");
        System.out.println("📱 [Nebula]   - Qualidade alta");
        System.out.println("📱 [Nebula]   - FPS: 60");
        System.out.println("📱 [Nebula]   - Render Distance: 12");
        
//         client.options.getViewDistance().setValue(12);
//         client.options.getSimulationDistance().setValue(10);
        client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
        client.options.getParticles().setValue(ParticlesMode.ALL);
        client.options.getMaxFps().setValue(60);
        client.options.getEntityShadows().setValue(true);
    }
    
    private void applyLaptopProfile(MinecraftClient client) {
        System.out.println("📱 [Nebula] 💻 Perfil LAPTOP:");
        System.out.println("📱 [Nebula]   - Qualidade média");
        System.out.println("📱 [Nebula]   - FPS: 45");
        System.out.println("📱 [Nebula]   - Render Distance: 10");
        
//         client.options.getViewDistance().setValue(10);
//         client.options.getSimulationDistance().setValue(8);
        client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
        client.options.getParticles().setValue(ParticlesMode.DECREASED);
        client.options.getMaxFps().setValue(45);
        client.options.getEntityShadows().setValue(true);
    }
    
    private void applyMobileProfile(MinecraftClient client) {
        System.out.println("📱 [Nebula] 📱 Perfil MOBILE:");
        System.out.println("📱 [Nebula]   - Qualidade baixa");
        System.out.println("📱 [Nebula]   - FPS: 30");
        System.out.println("📱 [Nebula]   - Render Distance: 6");
        
//         client.options.getViewDistance().setValue(6);
//         client.options.getSimulationDistance().setValue(4);
        client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
        client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        client.options.getMaxFps().setValue(30);
        client.options.getEntityShadows().setValue(false);
    }
    
    private void applyLowEndProfile(MinecraftClient client) {
        System.out.println("📱 [Nebula] ⚠️ Perfil LOW END:");
        System.out.println("📱 [Nebula]   - Qualidade mínima");
        System.out.println("📱 [Nebula]   - FPS: 20");
        System.out.println("📱 [Nebula]   - Render Distance: 4");
        
//         client.options.getViewDistance().setValue(4);
//         client.options.getSimulationDistance().setValue(4);
        client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
        client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        client.options.getMaxFps().setValue(20);
        client.options.getEntityShadows().setValue(false);
    }
    
    private void applyDefaultProfile(MinecraftClient client) {
        System.out.println("📱 [Nebula] Perfil DEFAULT:");
        System.out.println("📱 [Nebula]   - Configurações padrão");
        
//         client.options.getViewDistance().setValue(12);
//         client.options.getSimulationDistance().setValue(8);
        client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
        client.options.getParticles().setValue(ParticlesMode.ALL);
        client.options.getMaxFps().setValue(60);
        client.options.getEntityShadows().setValue(true);
    }
    
    public DeviceDetector.DeviceType getCurrentProfile() {
        return currentProfile;
    }
    
    public void reset() {
        applied = false;
    }
}
