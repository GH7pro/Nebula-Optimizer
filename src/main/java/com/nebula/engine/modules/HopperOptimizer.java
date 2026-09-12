package com.nebula.engine.modules;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;

/**
 * HopperOptimizer
 * Cancela a renderização dos itens que estão passando por dentro dos funis (hoppers)
 * quando o AdaptiveOptimizer está em nível alto, salvando o FPS em fazendas automáticas.
 */
public class HopperOptimizer implements NebulaModule {
    private static HopperOptimizer instance;

    public static HopperOptimizer getInstance() {
        if (instance == null) {
            instance = new HopperOptimizer();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Hopper Optimizer";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Hopper Optimizer inicializado.");
    }

    @Override
    public void tick() {
        // A lógica de ticks do funi não precisa rodar no cliente, 
        // o Mixin cuida de cortar a renderização direto na fonte.
    }

    // Método estático rápido para o Mixin chamar
    public static boolean shouldSkipRender() {
        if (!NebulaConfig.enableNebula) return false;
        AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
        return optimizer != null && (optimizer.isInPanicMode() || optimizer.getCurrentOptimizationLevel() >= 2);
    }

    @Override
    public void shutdown() {
        System.out.println("[Nebula] Hopper Optimizer desligado.");
    }

    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula;
    }
}
