package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.Set;

public class DynamicChunkLoader implements NebulaModule {
    private static final DynamicChunkLoader INSTANCE = new DynamicChunkLoader();
    private boolean enabled = true;
    
    // Armazena chunks que estão "prioritários"
    private Set<BlockPos> priorityChunks = new HashSet<>();
    private BlockPos lastPlayerPos = null;
    private Vec3d lastLookDirection = Vec3d.ZERO;
    private int tickCounter = 0;
    
    // Configurações
    private static final int PRIORITY_RADIUS = 8;      // Chunks prioritários na frente
    private static final int NORMAL_RADIUS = 4;        // Chunks normais ao redor
    private static final int BEHIND_RADIUS = 2;        // Chunks atrás (menos importantes)
    private static final int CHECK_INTERVAL = 20;      // Verifica a cada 1 segundo
    
    private DynamicChunkLoader() {}
    
    public static DynamicChunkLoader getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Dynamic Chunk Loading";
    }
    
    @Override
    public void initialize() {
        System.out.println("🗺️ [Nebula] Dynamic Chunk Loader inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableDynamicChunkLoading) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;
        
        ClientPlayerEntity player = client.player;
        BlockPos playerPos = player.getBlockPos();
        Vec3d lookDirection = player.getRotationVector();
        
        // Verifica se o jogador se moveu ou mudou de direção
        boolean moved = !playerPos.equals(lastPlayerPos);
        boolean turned = !lookDirection.equals(lastLookDirection);
        
        if (moved || turned) {
            updatePriorityChunks(client, playerPos, lookDirection);
            lastPlayerPos = playerPos;
            lastLookDirection = lookDirection;
        }
    }
    
    private void updatePriorityChunks(MinecraftClient client, BlockPos playerPos, Vec3d lookDirection) {
        priorityChunks.clear();
        
        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;
        
        // ===== 1. CALCULA DIREÇÃO DO OLHAR =====
        // Normaliza a direção
        Vec3d normalized = lookDirection.normalize();
        double lookX = normalized.x;
        double lookZ = normalized.z;
        
        // ===== 2. PRIORIDADE: CHUNKS NA DIREÇÃO DO OLHAR =====
        for (int dx = -PRIORITY_RADIUS; dx <= PRIORITY_RADIUS; dx++) {
            for (int dz = -PRIORITY_RADIUS; dz <= PRIORITY_RADIUS; dz++) {
                // Calcula a distância do centro
                double distance = Math.sqrt(dx*dx + dz*dz);
                if (distance > PRIORITY_RADIUS) continue;
                
                // Calcula o ângulo entre a direção do olhar e o chunk
                double angle = Math.atan2(dz, dx);
                double lookAngle = Math.atan2(lookZ, lookX);
                double angleDiff = Math.abs(angle - lookAngle);
                
                // Normaliza o ângulo
                if (angleDiff > Math.PI) angleDiff = 2 * Math.PI - angleDiff;
                
                // ===== 3. CHUNKS NA FRENTE =====
                if (angleDiff < Math.PI / 3) { // 60 graus na frente
                    int chunkX = playerChunkX + dx;
                    int chunkZ = playerChunkZ + dz;
                    BlockPos chunkPos = new BlockPos(chunkX, 0, chunkZ);
                    priorityChunks.add(chunkPos);
                }
            }
        }
        
        // ===== 4. CHUNKS ATRÁS (DESPRIORIZAR) =====
        for (int dx = -BEHIND_RADIUS; dx <= BEHIND_RADIUS; dx++) {
            for (int dz = -BEHIND_RADIUS; dz <= BEHIND_RADIUS; dz++) {
                // Direção oposta ao olhar
                double angle = Math.atan2(dz, dx);
                double lookAngle = Math.atan2(-lookZ, -lookX);
                double angleDiff = Math.abs(angle - lookAngle);
                
                if (angleDiff > Math.PI) angleDiff = 2 * Math.PI - angleDiff;
                
                // Chunks atrás do jogador
                if (angleDiff < Math.PI / 4) {
                    int chunkX = playerChunkX + dx;
                    int chunkZ = playerChunkZ + dz;
                    BlockPos chunkPos = new BlockPos(chunkX, 0, chunkZ);
                    // Remove da prioridade (será descarregado mais rápido)
                    priorityChunks.remove(chunkPos);
                }
            }
        }
        
        // ===== 5. LOG PARA DEBUG =====
        if (NebulaConfig.debugMode && tickCounter % 100 == 0) {
            System.out.println("🗺️ [Nebula] Chunks prioritários: " + priorityChunks.size());
        }
    }
    
    public boolean isChunkPriority(BlockPos chunkPos) {
        return priorityChunks.contains(chunkPos);
    }
    
    public boolean shouldLoadChunk(int chunkX, int chunkZ) {
        BlockPos pos = new BlockPos(chunkX, 0, chunkZ);
        return priorityChunks.contains(pos);
    }
    
    @Override
    public void shutdown() {
        priorityChunks.clear();
        System.out.println("🗺️ [Nebula] Dynamic Chunk Loader desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableDynamicChunkLoading;
    }
}
