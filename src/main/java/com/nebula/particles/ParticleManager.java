package com.nebula.particles;

import java.util.HashMap;
import java.util.Map;

public class ParticleManager {
    private static final ParticleManager INSTANCE = new ParticleManager();
    private Map<String, Boolean> particleSettings = new HashMap<>();
    
    // Lista de todas as partículas - DEFINIDA ANTES DO CONSTRUTOR
    public static final String[] PARTICLE_TYPES = {
        "FLAME", "SMOKE", "WATER", "LAVA", "EXPLOSION", "FIREWORK",
        "PORTAL", "MAGIC", "DRIP", "RAIN", "SNOW", "CLOUD",
        "CRITICAL", "ENCHANT", "BEACON", "CONDUIT", "HEART",
        "SPELL", "BUBBLE", "FISHING", "FOOTSTEP", "SWEEP",
        "COMPOSTER", "BONE_MEAL", "EGG_CRACK", "SCULK"
    };
    
    private ParticleManager() {
        // Inicializa com todas as partículas ativadas
        for (String type : PARTICLE_TYPES) {
            particleSettings.put(type, true);
        }
        loadSettings();
    }
    
    public static ParticleManager getInstance() {
        return INSTANCE;
    }
    
    public boolean isParticleEnabled(String type) {
        return particleSettings.getOrDefault(type, true);
    }
    
    public void setParticleEnabled(String type, boolean enabled) {
        particleSettings.put(type, enabled);
        saveSettings();
    }
    
    public Map<String, Boolean> getAllSettings() {
        return new HashMap<>(particleSettings);
    }
    
    public void saveSettings() {
        try {
            java.io.File file = new java.io.File("config/nebula_particles.properties");
            file.getParentFile().mkdirs();
            
            java.util.Properties props = new java.util.Properties();
            for (Map.Entry<String, Boolean> entry : particleSettings.entrySet()) {
                props.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
            
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            props.store(fos, "Nebula Particle Settings");
            fos.close();
            
        } catch (Exception e) {
            System.err.println("💀 [Nebula] Erro ao salvar configurações de partículas: " + e.getMessage());
        }
    }
    
    public void loadSettings() {
        try {
            java.io.File file = new java.io.File("config/nebula_particles.properties");
            if (!file.exists()) return;
            
            java.util.Properties props = new java.util.Properties();
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            props.load(fis);
            fis.close();
            
            for (String type : PARTICLE_TYPES) {
                String value = props.getProperty(type);
                if (value != null) {
                    particleSettings.put(type, Boolean.parseBoolean(value));
                }
            }
            
            System.out.println("💀 [Nebula] Configurações de partículas carregadas!");
            
        } catch (Exception e) {
            System.err.println("💀 [Nebula] Erro ao carregar configurações: " + e.getMessage());
        }
    }
    
    public void resetAll() {
        for (String type : PARTICLE_TYPES) {
            particleSettings.put(type, true);
        }
        saveSettings();
    }
    
    public void disableAll() {
        for (String type : PARTICLE_TYPES) {
            particleSettings.put(type, false);
        }
        saveSettings();
    }
}
