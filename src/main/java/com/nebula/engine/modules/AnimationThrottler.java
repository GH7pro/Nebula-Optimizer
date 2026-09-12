package com.nebula.engine.modules;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;

/**
 * AnimationThrottler
 * Congela as animações de mobs (bater de asas, balançar braços) quando eles
 * estão longe do jogador ou quando o mod está em nível alto de otimização.
 * Economiza muita CPU do celular.
 */
public class AnimationThrottler implements NebulaModule {
    private static AnimationThrottler instance;

    public static AnimationThrottler getInstance() {
        if (instance == null) {
            instance = new AnimationThrottler();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Animation Throttler";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Animation Throttler inicializado.");
    }

    @Override
    public void tick() {
        // A lógica real de congelar é feita direto no Mixin, aqui não precisa de tick.
    }

    /**
     * Este método é chamado pelo Mixin para saber se a entidade deve ser animada.
     * @param distance A distância da entidade até o jogador.
     * @return true se a animação deve ser cortada, false se deve rodar normal.
     */
    public static boolean shouldFreezeAnimation(float distance) {
        if (!NebulaConfig.enableNebula) return false;

        AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
        int level = optimizer != null ? optimizer.getCurrentOptimizationLevel() : 0;
        boolean panic = optimizer != null && optimizer.isInPanicMode();

        // Se estiver em Modo Pânico ou Nível 3+, congela animações a partir de 10 blocos
        if (panic || level >= 3) {
            return distance > 10.0f;
        } 
        // Se estiver no Nível 2, congela a partir de 20 blocos
        else if (level == 2) {
            return distance > 20.0f;
        }

        return false;
    }

    @Override
    public void shutdown() {
        System.out.println("[Nebula] Animation Throttler desligado.");
    }

    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula;
    }
}
