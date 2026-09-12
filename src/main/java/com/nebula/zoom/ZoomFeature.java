package com.nebula.zoom;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomFeature implements NebulaModule {
    private static final ZoomFeature INSTANCE = new ZoomFeature();
    private boolean enabled = true;
    private boolean isZooming = false;
    private float currentZoom = 1.0f;
    private float targetZoom = 1.0f;
    private KeyBinding zoomKey;
    
    private ZoomFeature() {}
    
    public static ZoomFeature getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Zoom Feature";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔍 [Nebula] Zoom Feature inicializado!");
        
        // Registra a tecla SEMPRE, não importa se está ligado ou desligado na config.
        // Assim evitamos o erro de NullPointer se o jogador ligar a opção depois no menu.
        zoomKey = new KeyBinding(
            "key.nebula.zoom",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.category.nebula"
        );
        KeyBindingHelper.registerKeyBinding(zoomKey);
        
        System.out.println("🔍 [Nebula] ✅ Zoom ativado! Pressione C para zoom");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableZoomFeature) return;
        
        // PROTEÇÃO: Se a tecla ainda não foi criada por algum motivo, não faz nada.
        if (zoomKey == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        // Verifica se a tecla de zoom está pressionada
        if (zoomKey.isPressed()) {
            isZooming = true;
            targetZoom = 0.15f; // Zoom de 15%
        } else {
            isZooming = false;
            targetZoom = 1.0f;
        }
        
        // Suaviza o zoom
        currentZoom = currentZoom + (targetZoom - currentZoom) * 0.1f;
        if (Math.abs(currentZoom - targetZoom) < 0.001f) {
            currentZoom = targetZoom;
        }
    }
    
    public float getZoom() {
        return currentZoom;
    }
    
    public boolean isZooming() {
        return isZooming;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🔍 [Nebula] Zoom Feature desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableZoomFeature;
    }
}
