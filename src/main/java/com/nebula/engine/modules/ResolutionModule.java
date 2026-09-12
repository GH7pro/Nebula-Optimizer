package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

/**
 * ResolutionModule
 * AVISO: Redimensionar o Framebuffer principal do Minecraft dinamicamente
 * causa distorção de FOV e borra a interface. Este módulo agora está desativado
 * por segurança. A otimização de FPS é feita pelo AdaptiveOptimizer.
 */
public class ResolutionModule implements NebulaModule {
    private static final ResolutionModule INSTANCE = new ResolutionModule();
    
    private ResolutionModule() {}
    
    public static ResolutionModule getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Dynamic Resolution Scaling";
    }
    
    @Override
    public void initialize() {
        System.out.println("📐 [Nebula] Dynamic Resolution Module desativado para evitar distorção de tela.");
    }
    
    @Override
    public void tick() {
        // Desativado: Redimensionar o framebuffer quebra a matriz de projeção do Minecraft.
    }
    
    @Override
    public void shutdown() {
    }
    
    @Override
    public boolean isEnabled() {
        // Retorna falso para o ModuleManager pular este módulo e economizar processamento.
        return false;
    }
}
