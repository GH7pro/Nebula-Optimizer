package com.nebula.lib.api;

import net.minecraft.client.MinecraftClient;

/**
 * Acompanha FPS de verdade (média móvel) e deriva uma "densidade" adaptativa
 * — um multiplicador entre 0.4 e 1.0 que o CullingAPI usa pra encolher a
 * distância de renderização quando o FPS cai, e voltar ao normal quando
 * melhora. Sem histeria: só reage a médias, não a picos de 1 frame.
 */
public final class PerformanceAPI {

    private PerformanceAPI() {}

    private static final int HISTORY_SIZE = 60; // ~3s a 20 ticks/s, ou alguns segundos de frames
    private static final double[] fpsHistory = new double[HISTORY_SIZE];
    private static int historyIndex = 0;
    private static int historyCount = 0;

    private static int entityCount = 0;
    private static double densityMultiplier = 1.0;

    public static void onFrame() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        double fps = client.getCurrentFps();
        fpsHistory[historyIndex] = fps;
        historyIndex = (historyIndex + 1) % HISTORY_SIZE;
        if (historyCount < HISTORY_SIZE) historyCount++;
    }

    public static double getCurrentFps() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null ? client.getCurrentFps() : 0;
    }

    public static double getAverageFps() {
        if (historyCount == 0) return getCurrentFps();
        double sum = 0;
        for (int i = 0; i < historyCount; i++) sum += fpsHistory[i];
        return sum / historyCount;
    }

    public static void setEntityCount(int count) {
        entityCount = count;
    }

    public static int getEntityCount() {
        return entityCount;
    }

    /**
     * Ajusta a densidade com base na média de FPS. Alvo é
     * NebulaConfig.targetFps; abaixo disso a densidade encolhe aos poucos
     * (nunca de uma vez, pra não "piscar" a distância de render).
     */
    public static void updateAdaptiveDensity() {
        double avg = getAverageFps();
        double target = com.nebula.NebulaConfig.targetFps;
        if (target <= 0) target = 60;

        double desired;
        if (avg < target * 0.7) {
            desired = 0.4;
        } else if (avg < target * 0.9) {
            desired = 0.7;
        } else {
            desired = 1.0;
        }

        // Suaviza a transição (10% do caminho por chamada) pra não oscilar.
        densityMultiplier += (desired - densityMultiplier) * 0.1;
    }

    public static double getDensityMultiplier() {
        return densityMultiplier;
    }

    public static String getDensityStatus() {
        if (densityMultiplier >= 0.95) return "FULL";
        if (densityMultiplier >= 0.65) return "REDUCED";
        return "MINIMAL";
    }
}
