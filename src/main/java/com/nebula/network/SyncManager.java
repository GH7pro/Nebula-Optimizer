package com.nebula.network;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SyncManager {
    private static final SyncManager INSTANCE = new SyncManager();
    private List<BlockPos> blocksToSync = new ArrayList<>();
    private int syncCounter = 0;
    
    private SyncManager() {}
    
    public static SyncManager getInstance() {
        return INSTANCE;
    }
    
    public void tick() {
        if (!NebulaConfig.get().enableAntiGhostBlock) return;
        
        syncCounter++;
        
        // A cada 5 segundos, verifica sincronização
        if (syncCounter % 100 == 0) {
            forceSync();
        }
    }
    
    public void forceSync() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        // Força a sincronização de blocos pendentes
        for (BlockPos pos : blocksToSync) {
            if (client.world.getBlockState(pos).isAir()) {
                blocksToSync.remove(pos);
                AntiGhostBlock.getInstance().confirmBlockBroken(pos);
            }
        }
        
        if (NebulaConfig.get().debugMode) {
            System.out.println("🔄 [Nebula] Sincronização forçada! Blocos pendentes: " + blocksToSync.size());
        }
    }
    
    public void addBlockToSync(BlockPos pos) {
        if (!blocksToSync.contains(pos)) {
            blocksToSync.add(pos);
        }
    }
    
    public void removeBlockFromSync(BlockPos pos) {
        blocksToSync.remove(pos);
    }
}
