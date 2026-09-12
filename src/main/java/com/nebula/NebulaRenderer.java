package com.nebula;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkSectionPos;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NebulaRenderer {

    private final Long2ObjectOpenHashMap<NebulaChunkSection> sections = new Long2ObjectOpenHashMap<>();
    
    public final ExecutorService buildPool = Executors.newFixedThreadPool(
        Math.max(1, Runtime.getRuntime().availableProcessors() - 2),
        r -> {
            Thread t = new Thread(r, "Nebula-Builder");
            t.setDaemon(true);
            t.setPriority(Thread.NORM_PRIORITY - 1);
            return t;
        }
    );

    private NebulaOcclusionCuller blockCuller;
    private NebulaEntityCuller entityCuller;
    private int tickCounter = 0;

    public void update() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        tickCounter++;
        Camera camera = client.gameRenderer.getCamera();

        if (blockCuller == null || tickCounter % 4 == 0) {
            blockCuller = new NebulaOcclusionCuller(camera);
        } else {
            blockCuller.updateFrustum(camera);
        }

        if (entityCuller == null || tickCounter % 2 == 0) {
            entityCuller = new NebulaEntityCuller(camera);
        }

        // Reset counters
        NebulaDebug.resetCounters();

        // Entity Culling + contagem
        if (NebulaConfig.get().enableEntityCulling) {
            for (Entity entity : client.world.getEntities()) {
                NebulaDebug.totalEntities++;
                if (!entityCuller.shouldRenderEntity(entity)) {
                    NebulaDebug.culledEntities++;
                }
            }
        }
    }

    public void clear() {
        sections.forEach((k, section) -> section.delete());
        sections.clear();
    }

    /**
     * Sem isso, o thread pool de build de chunks (buildPool) nunca era
     * desligado, e cada saída de mundo/jogo vazava threads.
     */
    public void shutdown() {
        clear();
        buildPool.shutdown();
        try {
            if (!buildPool.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                buildPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            buildPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
