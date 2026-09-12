package com.nebula.engine.modules;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;

/**
 * DroppedItemOptimizer
 * Cancela a renderização de itens caídos no chão quando o jogo está sofrendo
 * com lag (Nível 3+ ou Modo Pânico). A fazenda continua funcionando, os itens
 * continuam lá, mas não são mais desenhados na tela para salvar o FPS.
 */
public class DroppedItemOptimizer implements NebulaModule {
    private static DroppedItemOptimizer instance;

    public static DroppedItemOptimizer getInstance() {
        if (instance == null) {
            instance = new DroppedItemOptimizer();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Dropped Item Optimizer";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Dropped Item Optimizer inicializado.");
    }

    @Override
    public void tick() {
        // O trabalho pesado é feito direto no Mixin, não precisamos de tick aqui.
    }

    public static boolean shouldSkipRender() {
        if (!NebulaConfig.enableNebula) return false;
        AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
        return optimizer != null && (optimizer.isInPanicMode() || optimizer.getCurrentOptimizationLevel() >= 3);
    }

    @Override
    public void shutdown() {
        System.out.println("[Nebula] Dropped Item Optimizer desligado.");
    }

    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula;
    }
}
