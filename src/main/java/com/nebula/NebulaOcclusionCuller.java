package com.nebula;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

public class NebulaOcclusionCuller {

    private Vec3d cameraPos;
    private final double maxDistanceSq;
    private final boolean enabled;
    private final boolean verticalCulling;

    public NebulaOcclusionCuller(Camera camera) {
        this.cameraPos = camera.getPos();
        
        NebulaConfig config = NebulaConfig.get();
        this.enabled = config.enableBlockCulling;
        this.verticalCulling = config.enableVerticalCulling;

        double viewDist = config.blockCullingDistance * 16.0;
        this.maxDistanceSq = viewDist * viewDist;
    }

    public boolean isSectionVisible(ChunkSectionPos sectionPos) {
        if (!enabled) return true;

        double centerX = sectionPos.getMinX() + 8.0;
        double centerY = sectionPos.getMinY() + 8.0;
        double centerZ = sectionPos.getMinZ() + 8.0;

        double dx = centerX - cameraPos.x;
        double dy = centerY - cameraPos.y;
        double dz = centerZ - cameraPos.z;

        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > maxDistanceSq) {
            return false;
        }

        // Culling vertical
        if (verticalCulling && Math.abs(dy) > 96 && distSq > 2304) {
            return false;
        }

        return true;
    }

    public void updateFrustum(Camera camera) {
        this.cameraPos = camera.getPos();
    }
}
