package com.nebula.profiles;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;

public class AutoProfileManager {

    private static PerformanceProfile currentProfile = PerformanceProfile.BALANCED;
    private static boolean initialized = false;

    public static void detectAndApply() {
        if (!NebulaConfig.enableAutoProfile) return;

        int cores = Runtime.getRuntime().availableProcessors();
        long maxMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        // Detecção simples e eficaz
        if (cores <= 4 || maxMemoryMB <= 4096) {
            currentProfile = PerformanceProfile.LOW_END;
        } else if (cores <= 8 || maxMemoryMB <= 8192) {
            currentProfile = PerformanceProfile.BALANCED;
        } else {
            currentProfile = PerformanceProfile.QUALITY;
        }

        applyProfile(currentProfile);
        initialized = true;

        System.out.println("[Nebula] Auto-Profile detectado: " + currentProfile.getDisplayName());
        System.out.println("[Nebula] CPU Cores: " + cores + " | RAM: " + maxMemoryMB + "MB");
    }

    public static void applyProfile(PerformanceProfile profile) {
        currentProfile = profile;

        switch (profile) {
            case LOW_END -> {
                NebulaConfig.enableEntityCulling = true;
                NebulaConfig.enableAngleCulling = true;
                NebulaConfig.enableAggressiveCulling = true;
                NebulaConfig.entityRenderDistance = 16;
                NebulaConfig.enableBlockCulling = true;
                NebulaConfig.blockCullingDistance = 10;
                NebulaConfig.enableParticleLimit = true;
                NebulaConfig.maxParticles = 150;
                NebulaConfig.enableDynamicResolution = true;
                NebulaConfig.minResolutionPercent = 50;
            }
            case BALANCED -> {
                NebulaConfig.enableEntityCulling = true;
                NebulaConfig.enableAngleCulling = true;
                NebulaConfig.enableAggressiveCulling = false;
                NebulaConfig.entityRenderDistance = 28;
                NebulaConfig.enableBlockCulling = true;
                NebulaConfig.blockCullingDistance = 14;
                NebulaConfig.enableParticleLimit = true;
                NebulaConfig.maxParticles = 350;
                NebulaConfig.enableDynamicResolution = true;
                NebulaConfig.minResolutionPercent = 70;
            }
            case QUALITY -> {
                NebulaConfig.enableEntityCulling = true;
                NebulaConfig.enableAngleCulling = false;
                NebulaConfig.enableAggressiveCulling = false;
                NebulaConfig.entityRenderDistance = 40;
                NebulaConfig.enableBlockCulling = false;
                NebulaConfig.enableParticleLimit = false;
                NebulaConfig.maxParticles = 800;
                NebulaConfig.enableDynamicResolution = false;
                NebulaConfig.minResolutionPercent = 100;
            }
        }

        NebulaConfig.saveConfig();
    }

    public static PerformanceProfile getCurrentProfile() {
        return currentProfile;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
