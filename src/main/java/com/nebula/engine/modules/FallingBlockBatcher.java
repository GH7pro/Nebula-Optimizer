package com.nebula.engine.modules;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

/**
 * FallingBlockBatcher
 * Otimiza a renderização de blocos caindo (areia, cascalho, bigornas).
 * Quando o AdaptiveOptimizer está em nível alto, simplifica a renderização
 * para evitar travamentos quando muitos blocos caem ao mesmo tempo.
 */
public class FallingBlockBatcher implements NebulaModule {
    private static FallingBlockBatcher instance;

    public static FallingBlockBatcher getInstance() {
        if (instance == null) {
            instance = new FallingBlockBatcher();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Falling Block Batcher";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Falling Block Batcher inicializado.");
    }

    @Override
    public void tick() {
        if (!isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null) return;

        AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
        
        // Se o mod estiver em Nível 3+ ou no Modo Pânico, ativamos a otimização de blocos caindo
        if (optimizer != null && (optimizer.isInPanicMode() || optimizer.getCurrentOptimizationLevel() >= 3)) {
            // No futuro, um Mixin vai ler essa variável para cancelar a renderização 3D 
            // dos blocos caindo e desenhar apenas um retângulo simples (ou ignorá-los).
            // Por enquanto, isso prepara a lógica no motor do Nebula.
        }
    }

    @Override
    public void shutdown() {
        System.out.println("[Nebula] Falling Block Batcher desligado.");
    }

    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula;
    }
}
