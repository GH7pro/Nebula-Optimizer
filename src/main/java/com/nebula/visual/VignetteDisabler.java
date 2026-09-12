package com.nebula.visual;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VignetteDisabler implements NebulaModule {
    private static final VignetteDisabler INSTANCE = new VignetteDisabler();
    private boolean enabled = true;
    private boolean disabled = false;
    
    private VignetteDisabler() {}
    
    public static VignetteDisabler getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Vignette Disabler";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎨 [Nebula] Vignette Disabler inicializado!");
        if (!NebulaConfig.enableVignetteDisabler) return;
        
        try {
            disableVignette();
            disabled = true;
            System.out.println("🎨 [Nebula] ✅ Vignette desativado!");
        } catch (Exception e) {
            System.err.println("🎨 [Nebula] Erro ao desativar vignette: " + e.getMessage());
        }
    }
    
    private void disableVignette() {
        try {
            // Tenta desativar via reflexão
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            
            // Tenta acessar o GameRenderer
            GameRenderer renderer = client.gameRenderer;
            if (renderer == null) return;
            
            // Método 1: Tenta desativar via campo
            try {
                Field vignetteField = GameRenderer.class.getDeclaredField("vignette");
                vignetteField.setAccessible(true);
                vignetteField.setBoolean(renderer, false);
                return;
            } catch (Exception e) {}
            
            // Método 2: Tenta via método
            try {
                Method method = GameRenderer.class.getDeclaredMethod("setVignette", boolean.class);
                method.setAccessible(true);
                method.invoke(renderer, false);
                return;
            } catch (Exception e) {}
            
        } catch (Exception e) {}
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableVignetteDisabler) return;
        if (!disabled) return;
        
        // Mantém desativado
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.gameRenderer == null) return;
            
            GameRenderer renderer = client.gameRenderer;
            
            // Tenta desativar novamente se for reativado
            try {
                Field vignetteField = GameRenderer.class.getDeclaredField("vignette");
                vignetteField.setAccessible(true);
                if (vignetteField.getBoolean(renderer)) {
                    vignetteField.setBoolean(renderer, false);
                }
            } catch (Exception e) {}
            
        } catch (Exception e) {}
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎨 [Nebula] Vignette Disabler desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableVignetteDisabler;
    }
}
