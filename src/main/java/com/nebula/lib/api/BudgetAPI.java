package com.nebula.lib.api;

/**
 * Orçamento de entidades renderizadas por FRAME (não por tick). Mesmo que
 * uma entidade passe em todos os outros critérios de culling, se o frame já
 * "gastou" o orçamento ela é cortada também — evita que um pico de mobs
 * juntos (spawn de zumbis, por exemplo) derrube o FPS de uma vez.
 */
public final class BudgetAPI {

    private BudgetAPI() {}

    private static int renderedThisFrame = 0;

    public static void beginFrame() {
        renderedThisFrame = 0;
    }

    public static boolean tryConsume() {
        if (renderedThisFrame >= getMaxEntitiesPerFrame()) {
            return false;
        }
        renderedThisFrame++;
        return true;
    }

    public static int getRenderedThisFrame() {
        return renderedThisFrame;
    }

    /** Escala com a densidade adaptativa: menos orçamento quando o FPS já tá ruim. */
    public static int getMaxEntitiesPerFrame() {
        double density = PerformanceAPI.getDensityMultiplier();
        int base = 200;
        return Math.max(20, (int) (base * density));
    }
}
