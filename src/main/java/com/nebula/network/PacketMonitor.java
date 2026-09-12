package com.nebula.network;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.Queue;

public class PacketMonitor {
    private static final PacketMonitor INSTANCE = new PacketMonitor();
    private Queue<Long> pingHistory = new LinkedList<>();
    private long lastPing = 0;
    private int packetLoss = 0;
    private int totalPackets = 0;
    private long lastPacketTime = 0;
    
    private static final int HISTORY_SIZE = 20;
    private static final long PACKET_TIMEOUT = 5000; // 5 segundos sem pacote = perda
    
    private PacketMonitor() {}
    
    public static PacketMonitor getInstance() {
        return INSTANCE;
    }
    
    public void recordPing(long ping) {
        pingHistory.add(ping);
        if (pingHistory.size() > HISTORY_SIZE) {
            pingHistory.poll();
        }
        lastPing = ping;
    }
    
    public void recordPacketSent() {
        totalPackets++;
        lastPacketTime = System.currentTimeMillis();
    }
    
    public void recordPacketReceived() {
        totalPackets++;
        lastPacketTime = System.currentTimeMillis();
    }
    
    public void recordPacketLoss() {
        packetLoss++;
        totalPackets++;
    }
    
    public double getAveragePing() {
        if (pingHistory.isEmpty()) return 0;
        return pingHistory.stream().mapToLong(Long::longValue).average().orElse(0);
    }
    
    public long getLastPing() {
        return lastPing;
    }
    
    public int getPacketLossPercent() {
        if (totalPackets == 0) return 0;
        return (packetLoss * 100) / totalPackets;
    }
    
    public boolean isConnectionStable() {
        // Verifica se há perda de pacotes ou se está sem receber dados
        long timeSinceLastPacket = System.currentTimeMillis() - lastPacketTime;
        return packetLoss < 10 && timeSinceLastPacket < PACKET_TIMEOUT;
    }
    
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        // Verifica se o jogador está tendo problemas de conexão
        if (!isConnectionStable() && NebulaConfig.get().debugMode) {
            System.out.println("📡 [Nebula] ⚠️ Conexão instável detectada!");
            System.out.println("📡 [Nebula] Perda de pacotes: " + getPacketLossPercent() + "%");
            System.out.println("📡 [Nebula] Ping médio: " + (int)getAveragePing() + "ms");
        }
    }
}
