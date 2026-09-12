package com.nebula;

import com.nebula.lib.api.BudgetAPI;
import com.nebula.lib.api.CullingAPI;
import com.nebula.lib.api.FocusAPI;
import com.nebula.lib.api.ImportanceAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class NebulaEntityCuller {

    private final Vec3d cameraPos;
    private final Vec3d lookVec;
    private final double renderDistanceSq;

    public NebulaEntityCuller(Camera camera) {
        this.cameraPos = camera.getPos();
        this.lookVec = new Vec3d(
            camera.getHorizontalPlane().get(0),
            0,
            camera.getHorizontalPlane().get(2)
        ).normalize();

        // Distância base da CullingAPI + multiplicador do Focus Mode
        double base = CullingAPI.getEntityRenderDistance() * 16.0;
        double focusMul = FocusAPI.getFocusDistanceMultiplier();
        double adjusted = base * focusMul;
        this.renderDistanceSq = adjusted * adjusted;
    }

    public boolean shouldRenderEntity(Entity entity) {
        if (!CullingAPI.isEntityCullingEnabled()) return true;
        if (entity == null || !entity.isAlive()) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return true;

        // Nunca corta o jogador local
        if (entity instanceof PlayerEntity && entity == client.player) {
            return true;
        }

        // Budget: se já renderizou demais neste frame, corta
        if (!BudgetAPI.tryConsume()) {
            return false;
        }

        double distSq = entity.squaredDistanceTo(cameraPos);
        double distance = Math.sqrt(distSq);

        // Direção da entidade
        Vec3d toEntity = entity.getPos().subtract(cameraPos).normalize();
        double dot = lookVec.dotProduct(toEntity);

        // Focus Mode: ângulo mais restrito quando parado
        if (FocusAPI.isFocused() && dot < FocusAPI.getFocusAngleThreshold()) {
            // Ainda assim não corta CRITICAL
            ImportanceAPI.Priority p = classify(entity, distance);
            if (p != ImportanceAPI.Priority.CRITICAL) {
                return false;
            }
        }

        // Classifica importância
        ImportanceAPI.Priority priority = classify(entity, distance);

        // Culling por importância
        if (ImportanceAPI.shouldCull(priority, distSq, dot)) {
            return false;
        }

        // Fallback: distância pura
        if (distSq > renderDistanceSq && priority != ImportanceAPI.Priority.CRITICAL) {
            return false;
        }

        // Agressivo (só em LOW/NORMAL)
        if (CullingAPI.isAggressiveCullingEnabled()
                && distSq > 1600
                && priority != ImportanceAPI.Priority.CRITICAL
                && priority != ImportanceAPI.Priority.HIGH) {
            return false;
        }

        return true;
    }

    private ImportanceAPI.Priority classify(Entity entity, double distance) {
        boolean isPlayer = entity instanceof PlayerEntity;
        boolean hostile = entity instanceof HostileEntity;
        String type = entity.getType().toString().toLowerCase();
        return ImportanceAPI.classify(type, distance, hostile, isPlayer);
    }
}
