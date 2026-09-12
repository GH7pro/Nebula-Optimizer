package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.Set;

public class SmartChunkLoader implements NebulaModule {
    private static final SmartChunkLoader INSTANCE = new SmartChunkLoader();
    private Vec3d lastPosition = null;
    private Vec3d velocity = Vec3d.ZERO;
    private Direction lastDirection = Direction.NORTH;
    private Set<Chunk> preloadedChunks = new HashSet<>();
    private boolean enabled = true;
    
    private static final int PREDICT_DISTANCE = 8;
    private static final int MAX_PRELOAD = 16;
    
    private SmartChunkLoader() {}
    
    public static SmartChunkLoader getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Smart Chunk Loading";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔮 [Nebula] Smart Chunk Loader inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        Vec3d currentPos = client.player.getPos();
        
        if (lastPosition == null) {
            lastPosition = currentPos;
            return;
        }
        
        // Calcula velocidade e direção
        velocity = currentPos.subtract(lastPosition);
        lastPosition = currentPos;
        
        if (velocity.length() > 0.1) {
            Direction direction = Direction.fromVector((int)velocity.x, (int)velocity.y, (int)velocity.z);
            if (direction != null) {
                lastDirection = direction;
                predictAndPreload(client);
            }
        }
    }
    
    private void predictAndPreload(MinecraftClient client) {
        Vec3d playerPos = client.player.getPos();
        int chunkX = (int)(playerPos.x / 16);
        int chunkZ = (int)(playerPos.z / 16);
        
        // Calcula chunks na direção do movimento
        int dx = lastDirection.getOffsetX() * PREDICT_DISTANCE;
        int dz = lastDirection.getOffsetZ() * PREDICT_DISTANCE;
        
        int targetX = chunkX + dx;
        int targetZ = chunkZ + dz;
        
        // Pré-carrega chunks ao longo do caminho
        for (int i = 0; i < PREDICT_DISTANCE; i++) {
            int cx = chunkX + (dx / PREDICT_DISTANCE) * i;
            int cz = chunkZ + (dz / PREDICT_DISTANCE) * i;
            
            if (!client.world.isChunkLoaded(cx, cz)) {
                // Inicia carregamento assíncrono
                client.world.getChunkManager().getChunk(cx, cz);
                
                if (NebulaConfig.get().debugMode) {
                    System.out.println("🔮 [Nebula] Pré-carregando chunk: [" + cx + ", " + cz + "]");
                }
            }
        }
    }
    
    @Override
    public void shutdown() {
        preloadedChunks.clear();
        System.out.println("🔮 [Nebula] Smart Chunk Loader desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableSmartChunkLoading;
    }
}
