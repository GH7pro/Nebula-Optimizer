package com.nebula.particles;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;
import java.util.List;

public class ParticleKiller implements NebulaModule {
    private static final ParticleKiller INSTANCE = new ParticleKiller();
    private boolean enabled = true;
    private int tickCounter = 0;
    private int particlesKilled = 0;
    private Field particlesField = null;
    private boolean reflectionReady = false;
    
    private ParticleKiller() {}
    
    public static ParticleKiller getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Particle Killer";
    }
    
    @Override
    public void initialize() {
        System.out.println("💀 [Nebula] Particle Killer inicializado!");
        System.out.println("💀 [Nebula] ⚠️ Todas as partículas serão removidas!");
        setupReflection();
    }
    
    private void setupReflection() {
        try {
            // Tenta acessar o ParticleManager via reflection
            Class<?> particleManagerClass = Class.forName("net.minecraft.client.particle.ParticleManager");
            
            // Procura por campos que armazenam partículas
            Field[] fields = particleManagerClass.getDeclaredFields();
            for (Field field : fields) {
                if (List.class.isAssignableFrom(field.getType()) || 
                    field.getType().getName().contains("List") ||
                    field.getType().getName().contains("Queue")) {
                    field.setAccessible(true);
                    particlesField = field;
                    reflectionReady = true;
                    System.out.println("💀 [Nebula] ✅ Reflection preparado!");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("💀 [Nebula] ⚠️ Reflection não disponível. Usando método alternativo.");
        }
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableParticleKiller) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        
        tickCounter++;
        if (tickCounter % 5 != 0) return;
        
        try {
            // Método 1: Usa reflection para limpar partículas
            if (reflectionReady && particlesField != null) {
                Object particleManager = client.particleManager;
                if (particleManager != null) {
                    Object particlesList = particlesField.get(particleManager);
                    if (particlesList instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) particlesList;
                        particlesKilled += list.size();
                        list.clear();
                    }
                }
            }
            
            // Método 2: Tenta limpar via MinecraftClient diretamente
            if (client.particleManager != null) {
                try {
                    // Tenta chamar o método clearParticles se existir
                    java.lang.reflect.Method method = client.particleManager.getClass().getMethod("clearParticles");
                    method.setAccessible(true);
                    method.invoke(client.particleManager);
                } catch (Exception e) {
                    // Método não disponível, ignora
                }
            }
            
            if (particlesKilled > 0 && NebulaConfig.debugMode) {
                System.out.println("💀 [Nebula] " + particlesKilled + " partículas removidas!");
                particlesKilled = 0;
            }
            
        } catch (Exception e) {
            // Silencia erros
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("💀 [Nebula] Particle Killer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableParticleKiller;
    }
}
