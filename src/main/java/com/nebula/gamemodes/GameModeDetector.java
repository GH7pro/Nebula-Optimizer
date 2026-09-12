package com.nebula.gamemodes;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GameModeDetector {
    private static final GameModeDetector INSTANCE = new GameModeDetector();
    private GameMode currentMode = GameMode.EXPLORING;
    private int tickCounter = 0;
    private Vec3d lastPosition = Vec3d.ZERO;
    private int stationaryTicks = 0;
    
    public enum GameMode {
        COMBAT("⚔️ Combate"),
        BUILDING("🏗️ Construção"),
        EXPLORING("🗺️ Explorando"),
        MINING("⛏️ Minerando"),
        AFK("💤 Parado"),
        UNKNOWN("❓ Desconhecido");
        
        private final String displayName;
        
        GameMode(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private GameModeDetector() {}
    
    public static GameModeDetector getInstance() {
        return INSTANCE;
    }
    
    public void tick() {
        if (!NebulaConfig.enableGameModeDetection) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        
        tickCounter++;
        if (tickCounter % 20 != 0) return; // A cada 1 segundo
        
        PlayerEntity player = client.player;
        Vec3d currentPos = player.getPos();
        
        // ===== 1. DETECTA SE ESTÁ PARADO =====
        if (currentPos.distanceTo(lastPosition) < 0.1) {
            stationaryTicks++;
        } else {
            stationaryTicks = 0;
        }
        
        lastPosition = currentPos;
        
        // ===== 2. DETECTA COMBATE =====
        boolean inCombat = detectCombat(player);
        
        // ===== 3. DETECTA CONSTRUÇÃO =====
        boolean isBuilding = detectBuilding(client);
        
        // ===== 4. DETECTA MINERAÇÃO =====
        boolean isMining = detectMining(client);
        
        // ===== 5. DETERMINA O MODO =====
        GameMode newMode = currentMode;
        
        if (stationaryTicks > 300) { // 15 segundos parado
            newMode = GameMode.AFK;
        } else if (inCombat) {
            newMode = GameMode.COMBAT;
        } else if (isBuilding) {
            newMode = GameMode.BUILDING;
        } else if (isMining) {
            newMode = GameMode.MINING;
        } else if (stationaryTicks < 100) {
            newMode = GameMode.EXPLORING;
        }
        
        if (newMode != currentMode) {
            currentMode = newMode;
            onModeChange();
        }
    }
    
    private boolean detectCombat(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return false;
        
        for (Entity entity : client.world.getEntities()) {
            if (entity == player) continue;
            if (entity instanceof MobEntity) {
                double distance = player.distanceTo(entity);
                if (distance < 10) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean detectBuilding(MinecraftClient client) {
        if (client.crosshairTarget != null && 
            client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
            if (client.interactionManager != null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean detectMining(MinecraftClient client) {
        if (client.crosshairTarget != null && 
            client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            if (client.player != null && client.player.getMainHandStack() != null) {
                String itemName = client.player.getMainHandStack().getItem().toString().toLowerCase();
                if (itemName.contains("pickaxe") || 
                    itemName.contains("axe") || 
                    itemName.contains("shovel") ||
                    itemName.contains("sword")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void onModeChange() {
        System.out.println("🔄 [Nebula] Modo de jogo: " + currentMode.getDisplayName());
    }
    
    public GameMode getCurrentMode() {
        return currentMode;
    }
}
