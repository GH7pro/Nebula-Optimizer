package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

import java.io.File;
import java.io.FileWriter;

public class AutoTuner implements NebulaModule {
    private static final AutoTuner INSTANCE = new AutoTuner();
    private boolean enabled = true;
    private String hardwareProfile = "medium";
    private boolean tuned = false;
    
    private AutoTuner() {}
    
    public static AutoTuner getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Auto-Overclock";
    }
    
    @Override
    public void initialize() {
        System.out.println("📈 [Nebula] Auto-Tuner inicializado!");
        detectHardware();
    }
    
    @Override
    public void tick() {
        if (!enabled || tuned) return;
        
        applyOptimizedSettings();
        tuned = true;
    }
    
    private void detectHardware() {
        int processors = Runtime.getRuntime().availableProcessors();
        long maxMemory = Runtime.getRuntime().maxMemory();
        
        System.out.println("📈 [Nebula] Hardware detectado:");
        System.out.println("📈 [Nebula]   Processadores: " + processors);
        System.out.println("📈 [Nebula]   Memória máxima: " + (maxMemory / 1024 / 1024) + " MB");
        
        if (processors >= 8 && maxMemory >= 4_000_000_000L) {
            hardwareProfile = "high";
        } else if (processors >= 4 && maxMemory >= 2_000_000_000L) {
            hardwareProfile = "medium";
        } else {
            hardwareProfile = "low";
        }
        
        System.out.println("📈 [Nebula] Perfil de hardware: " + hardwareProfile);
    }
    
    private void applyOptimizedSettings() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        System.out.println("📈 [Nebula] Aplicando configurações para perfil: " + hardwareProfile);
        
        switch (hardwareProfile) {
            case "high":
//                 client.options.getViewDistance().setValue(16);
//                 client.options.getSimulationDistance().setValue(12);
                client.options.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
                client.options.getParticles().setValue(ParticlesMode.ALL);
                client.options.getMaxFps().setValue(120);
                System.out.println("📈 [Nebula] ✅ Perfil HIGH aplicado");
                break;
                
            case "medium":
//                 client.options.getViewDistance().setValue(12);
//                 client.options.getSimulationDistance().setValue(8);
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
                client.options.getMaxFps().setValue(60);
                System.out.println("📈 [Nebula] ✅ Perfil MEDIUM aplicado");
                break;
                
            case "low":
//                 client.options.getViewDistance().setValue(8);
//                 client.options.getSimulationDistance().setValue(6);
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getParticles().setValue(ParticlesMode.MINIMAL);
                client.options.getMaxFps().setValue(30);
                System.out.println("📈 [Nebula] ✅ Perfil LOW aplicado");
                break;
        }
        
        saveProfileToFile();
    }
    
    private void saveProfileToFile() {
        try {
            String configDir = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + "/config/";
            new File(configDir).mkdirs();
            
            FileWriter writer = new FileWriter(configDir + "nebula_profile.txt");
            writer.write("Hardware Profile: " + hardwareProfile + "\n");
            writer.write("Applied: " + java.time.LocalDateTime.now() + "\n");
            writer.close();
            
            System.out.println("📈 [Nebula] Perfil salvo em: " + configDir + "nebula_profile.txt");
        } catch (Exception e) {
            // Silencia erros
        }
    }
    
    public String getHardwareProfile() {
        return hardwareProfile;
    }
    
    @Override
    public void shutdown() {
        System.out.println("📈 [Nebula] Auto-Tuner desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableAutoTuner;
    }
}
