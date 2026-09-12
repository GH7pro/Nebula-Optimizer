package com.nebula.network;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AntiGhostBlock implements NebulaModule {
    private static final AntiGhostBlock INSTANCE = new AntiGhostBlock();
    private boolean enabled = true;
    
    // Armazena blocos que estão "pendentes" de confirmação do servidor
    private Map<BlockPos, Long> pendingBlocks = new ConcurrentHashMap<>();
    private Map<BlockPos, Integer> retryCount = new ConcurrentHashMap<>();
    
    // Tempo máximo para esperar confirmação (em ms)
    private static final long TIMEOUT_MS = 3000;
    private static final int MAX_RETRIES = 5;
    
    private AntiGhostBlock() {}
    
    public static AntiGhostBlock getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Anti-Ghost Block";
    }
    
    @Override
    public void initialize() {
        System.out.println("👻 [Nebula] Anti-Ghost Block inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        if (!NebulaConfig.get().enableAntiGhostBlock) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        long currentTime = System.currentTimeMillis();
        
        // Verifica blocos pendentes
        for (Map.Entry<BlockPos, Long> entry : pendingBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            long timestamp = entry.getValue();
            
            // Se passou do tempo limite, reenvia a quebra
            if (currentTime - timestamp > TIMEOUT_MS) {
                retryBreakBlock(client, pos);
            }
        }
        
        // Limpa blocos que já foram processados
        cleanupProcessedBlocks(client);
    }
    
    public void markBlockForBreaking(BlockPos pos) {
        if (!enabled) return;
        
        // Adiciona o bloco à lista de pendentes
        pendingBlocks.put(pos, System.currentTimeMillis());
        retryCount.put(pos, 0);
        
        if (NebulaConfig.get().debugMode) {
            System.out.println("👻 [Nebula] Bloco marcado para quebra: " + pos);
        }
    }
    
    public void confirmBlockBroken(BlockPos pos) {
        if (!enabled) return;
        
        // Bloco foi confirmado pelo servidor
        pendingBlocks.remove(pos);
        retryCount.remove(pos);
        
        if (NebulaConfig.get().debugMode) {
            System.out.println("✅ [Nebula] Bloco confirmado: " + pos);
        }
    }
    
    private void retryBreakBlock(MinecraftClient client, BlockPos pos) {
        int retries = retryCount.getOrDefault(pos, 0);
        
        if (retries >= MAX_RETRIES) {
            // Desiste após muitas tentativas
            pendingBlocks.remove(pos);
            retryCount.remove(pos);
            
            if (NebulaConfig.get().debugMode) {
                System.out.println("❌ [Nebula] Desistindo do bloco: " + pos + " (muitas tentativas)");
            }
            return;
        }
        
        // Reenvia a ação de quebrar
        if (client.interactionManager != null && client.player != null) {
            retryCount.put(pos, retries + 1);
            pendingBlocks.put(pos, System.currentTimeMillis());
            
            // Tenta quebrar novamente
            client.interactionManager.breakBlock(pos);
            
            if (NebulaConfig.get().debugMode) {
                System.out.println("🔄 [Nebula] Re-tentando quebrar bloco: " + pos + " (tentativa " + (retries + 1) + ")");
            }
        }
    }
    
    private void cleanupProcessedBlocks(MinecraftClient client) {
        World world = client.world;
        if (world == null) return;
        
        // Remove blocos que já foram quebrados
        for (BlockPos pos : pendingBlocks.keySet()) {
            if (world.getBlockState(pos).isAir()) {
                confirmBlockBroken(pos);
            }
        }
    }
    
    public boolean isBlockPending(BlockPos pos) {
        return pendingBlocks.containsKey(pos);
    }
    
    public int getPendingCount() {
        return pendingBlocks.size();
    }
    
    public void reset() {
        pendingBlocks.clear();
        retryCount.clear();
    }
    
    @Override
    public void shutdown() {
        reset();
        System.out.println("👻 [Nebula] Anti-Ghost Block desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableAntiGhostBlock;
    }
}
