package com.nebula.culling;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class AdaptiveEntityDensity {

    private static int lastEntityCount = 0;
    private static float currentCullingMultiplier = 1.0f;
    private static int tickCounter = 0;

    public static void tick() {
        if (!NebulaConfig.enableEntityCulling) return;

        tickCounter++;
        if (tickCounter % 40 != 0) return; // Atualiza a cada 2 segundos

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        int entityCount = 0;
        for (Entity ignored : client.world.getEntities()) {
            entityCount++;
        }

        lastEntityCount = entityCount;

        // Lógica adaptativa
        if (entityCount > 180) {
            currentCullingMultiplier = 0.55f; // Culling bem agressivo
        } else if (entityCount > 100) {
            currentCullingMultiplier = 0.75f;
        } else if (entityCount > 50) {
            currentCullingMultiplier = 0.90f;
        } else {
            currentCullingMultiplier = 1.15f; // Relaxa o culling
        }
    }

    public static double getAdjustedRenderDistance() {
        return NebulaConfig.entityRenderDistance * 16.0 * currentCullingMultiplier;
    }

    public static float getCullingMultiplier() {
        return currentCullingMultiplier;
    }

    public static int getLastEntityCount() {
        return lastEntityCount;
    }

    public static String getStatus() {
        if (currentCullingMultiplier < 0.7f) return "§cHigh Density";
        if (currentCullingMultiplier < 0.95f) return "§eMedium Density";
        return "§aLow Density";
    }
}
