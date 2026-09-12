package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

public class SelectiveCulling implements NebulaModule {
    private static final SelectiveCulling INSTANCE = new SelectiveCulling();
    private boolean enabled = true;
    
    private SelectiveCulling() {}
    
    public static SelectiveCulling getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Selective Render Culling";
    }
    
    @Override
    public void initialize() {
        System.out.println("🧩 [Nebula] Selective Culling inicializado!");
    }
    
    @Override
    public void tick() {
        // O trabalho real seria feito nos Mixins de renderização
    }
    
    public boolean shouldRender(Entity entity, Vec3d cameraDirection) {
        if (!enabled) return true;
        if (!NebulaConfig.get().enableSelectiveCulling) return true;
        
        Vec3d entityPos = entity.getPos();
        Vec3d cameraPos = MinecraftClient.getInstance().player.getPos();
        
        Vec3d toEntity = entityPos.subtract(cameraPos).normalize();
        double angle = Math.acos(cameraDirection.dotProduct(toEntity));
        double angleDegrees = Math.toDegrees(angle);
        
        // Foco central (45 graus): renderiza completo
        if (angleDegrees < 45) {
            return true;
        }
        
        // Periferia (45-90 graus): renderiza com qualidade reduzida
        if (angleDegrees < 90) {
            return true; // Reduz qualidade
        }
        
        // Fora do campo de visão: não renderiza
        return false;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🧩 [Nebula] Selective Culling desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableSelectiveCulling;
    }
}
