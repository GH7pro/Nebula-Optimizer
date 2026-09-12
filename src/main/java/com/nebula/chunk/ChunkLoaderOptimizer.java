package com.nebula.chunk;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChunkLoaderOptimizer implements NebulaModule {
    private static final ChunkLoaderOptimizer INSTANCE = new ChunkLoaderOptimizer();
    private boolean enabled = true;
    private int tickCounter = 0;
    private List<BlockPos> pendingChunks = new ArrayList<>();
    private BlockPos lastPlayerPos = null;
    private Vec3d lastVelocity = Vec3d.ZERO;
    
    private static final int CHECK_INTERVAL = 10;
    private static final int PRELOAD_DISTANCE = 32;
    
    private ChunkLoaderOptimizer() {}
    
    public static ChunkLoaderOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Chunk Loader Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("🗺️ [Nebula] Chunk Loader Optimizer inicializado!");
        if (!NebulaConfig.enableChunkLoaderOptimizer) return;
        
        System.out.println("🗺️ [Nebula] ✅ Carregamento de chunks otimizado!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableChunkLoaderOptimizer) return;
        
        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        // ===== 1. CARREGAMENTO PREDITIVO =====
        predictiveLoading(client);
        
        // ===== 2. PRIORIDADE DE CHUNKS =====
        prioritizeChunks(client);
        
        // ===== 3. CARREGAMENTO ASSÍNCRONO =====
        asyncChunkLoading(client);
    }
    
    private void predictiveLoading(MinecraftClient client) {
        BlockPos playerPos = client.player.getBlockPos();
        Vec3d velocity = client.player.getVelocity();
        
        // Detecta direção do movimento
        if (velocity.length() > 0.1) {
            // Calcula chunks na direção do movimento
            int dx = (int) (velocity.x * 2);
            int dz = (int) (velocity.z * 2);
            
            int chunkX = playerPos.getX() >> 4;
            int chunkZ = playerPos.getZ() >> 4;
            
            // Pré-carrega chunks na direção do movimento
            for (int i = 1; i <= 4; i++) {
                int cx = chunkX + dx * i;
                int cz = chunkZ + dz * i;
                
                if (!client.world.isChunkLoaded(cx, cz)) {
                    // Inicia carregamento assíncrono
                    final int finalCx = cx;
                    final int finalCz = cz;
                    CompletableFuture.runAsync(() -> {
                        try {
                            client.world.getChunkManager().getChunk(finalCx, finalCz);
                        } catch (Exception e) {}
                    });
                }
            }
            
            if (NebulaConfig.debugMode && tickCounter % 100 == 0) {
                System.out.println("🗺️ [Nebula] Pré-carregando chunks na direção: " + velocity);
            }
        }
    }
    
    private void prioritizeChunks(MinecraftClient client) {
        // Prioriza chunks que o jogador está olhando
        // (já implementado no DynamicChunkLoader)
    }
    
    private void asyncChunkLoading(MinecraftClient client) {
        // Carrega chunks em background para evitar engasgos
        if (pendingChunks.isEmpty()) return;
        
        // Processa chunks pendentes
        List<BlockPos> toProcess = new ArrayList<>(pendingChunks);
        pendingChunks.clear();
        
        for (BlockPos pos : toProcess) {
            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            
            if (!client.world.isChunkLoaded(cx, cz)) {
                try {
                    client.world.getChunkManager().getChunk(cx, cz);
                } catch (Exception e) {}
            }
        }
    }
    
    public void requestChunkLoad(BlockPos pos) {
        if (!pendingChunks.contains(pos)) {
            pendingChunks.add(pos);
        }
    }
    
    @Override
    public void shutdown() {
        pendingChunks.clear();
        System.out.println("🗺️ [Nebula] Chunk Loader Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableChunkLoaderOptimizer;
    }
}
