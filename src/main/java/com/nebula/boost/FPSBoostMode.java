package com.nebula.boost;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.PerformanceMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FPSBoostMode implements NebulaModule {
    private static final FPSBoostMode INSTANCE = new FPSBoostMode();
    private boolean enabled = true;
    private boolean isBoosted = false;
    private boolean autoBoost = false;
    private int tickCounter = 0;
    private KeyBinding boostKey;
    
    private static final int AUTO_BOOST_THRESHOLD = 20;
    
    private FPSBoostMode() {}
    
    public static FPSBoostMode getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "FPS Boost Mode";
    }
    
    @Override
    public void initialize() {
        System.out.println("⚡ [Nebula] FPS Boost Mode inicializado!");
        if (!NebulaConfig.enableFPSBoost) return;
        
        boostKey = new KeyBinding(
            "key.nebula.boost",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            "key.category.nebula"
        );
        
        System.out.println("⚡ [Nebula] ✅ Pressione F para ativar/desativar o Boost");
        System.out.println("⚡ [Nebula] ✅ Boost automático em FPS < " + AUTO_BOOST_THRESHOLD);
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableFPSBoost) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        tickCounter++;
        
        if (boostKey.wasPressed()) {
            if (isBoosted) {
                deactivate();
            } else {
                activate();
            }
        }
        
        if (NebulaConfig.enableAutoBoost) {
            if (tickCounter % 20 != 0) return;
            
            double fps = PerformanceMonitor.getInstance().getCurrentFPS();
            if (fps < AUTO_BOOST_THRESHOLD && !isBoosted) {
                autoBoost = true;
                activate();
            } else if (fps > AUTO_BOOST_THRESHOLD + 10 && isBoosted && autoBoost) {
                autoBoost = false;
                deactivate();
            }
        }
    }
    
    public void activate() {
        if (isBoosted) return;
        isBoosted = true;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        System.out.println("⚡ [Nebula] 🔥 FPS BOOST ATIVADO!" + (autoBoost ? " (automático)" : ""));
        
        // OTIMIZAÇÕES VISUAIS (Sem mexer na View Distance para não recarregar chunks)
        client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
        client.options.getParticles().setValue(ParticlesMode.MINIMAL);
        client.options.getEntityShadows().setValue(false);
        client.options.getMaxFps().setValue(60);
        client.options.getBobView().setValue(false);
        client.options.getAo().setValue(false);
        client.options.getCloudRenderMode().setValue(net.minecraft.client.option.CloudRenderMode.OFF);
        
        System.out.println("⚡ [Nebula] 📊 FPS: 60 | Gráficos: FAST | Nuvens: OFF");
    }
    
    public void deactivate() {
        if (!isBoosted) return;
        isBoosted = false;
        autoBoost = false;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        System.out.println("⚡ [Nebula] 🔄 FPS BOOST desativado!");
        
        // RESTAURA CONFIGURAÇÕES VISUAIS
        client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
        client.options.getParticles().setValue(ParticlesMode.ALL);
        client.options.getEntityShadows().setValue(true);
        client.options.getMaxFps().setValue(120);
        client.options.getBobView().setValue(true);
        client.options.getAo().setValue(true);
        client.options.getCloudRenderMode().setValue(net.minecraft.client.option.CloudRenderMode.FANCY);
    }
    
    public void toggle() {
        if (isBoosted) {
            deactivate();
        } else {
            activate();
        }
    }
    
    public boolean isBoosted() {
        return isBoosted;
    }
    
    public boolean isAutoBoost() {
        return autoBoost;
    }
    
    @Override
    public void shutdown() {
        if (isBoosted) deactivate();
        System.out.println("⚡ [Nebula] FPS Boost Mode desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableFPSBoost;
    }
}
