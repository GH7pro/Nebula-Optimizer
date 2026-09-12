package com.nebula.fullbright;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FullbrightToggle implements NebulaModule {
    private static final FullbrightToggle INSTANCE = new FullbrightToggle();
    private boolean enabled = true;
    private boolean isActive = false;
    private double originalGamma = 0.5;
    private KeyBinding fullbrightKey;
    
    private FullbrightToggle() {}
    
    public static FullbrightToggle getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Fullbright Toggle";
    }
    
    @Override
    public void initialize() {
        System.out.println("🌙 [Nebula] Fullbright Toggle inicializado!");
        if (!NebulaConfig.enableFullbrightToggle) return;
        
        fullbrightKey = new KeyBinding(
            "key.nebula.fullbright",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.category.nebula"
        );
        
        System.out.println("🌙 [Nebula] ✅ Fullbright ativado! Pressione B para alternar");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableFullbrightToggle) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;
        
        if (fullbrightKey.wasPressed()) {
            isActive = !isActive;
            if (isActive) {
                originalGamma = client.options.getGamma().getValue();
                client.options.getGamma().setValue(10.0);
                System.out.println("🌙 [Nebula] ✅ Night Vision ativada!");
            } else {
                client.options.getGamma().setValue(originalGamma);
                System.out.println("🌙 [Nebula] ❌ Night Vision desativada!");
            }
        }
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    public void shutdown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.options != null) {
            client.options.getGamma().setValue(originalGamma);
        }
        System.out.println("🌙 [Nebula] Fullbright Toggle desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableFullbrightToggle;
    }
}
