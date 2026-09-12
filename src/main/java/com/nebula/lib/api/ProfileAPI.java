package com.nebula.lib.api;

import com.nebula.NebulaConfig;

/**
 * Detecta o hardware (via CPU/RAM, sem nada específico de plataforma — mais
 * seguro que ler arquivos de sistema) e aplica um perfil de distância de
 * render/culling coerente. Roda uma vez ao iniciar o jogo.
 */
public final class ProfileAPI {

    private ProfileAPI() {}

    public enum Profile {
        LOW("Low-End", 12, 8),
        MEDIUM("Balanced", 24, 12),
        HIGH("High-End", 32, 16);

        private final String displayName;
        private final int entityRenderDistance;
        private final int blockCullingDistance;

        Profile(String displayName, int entityRenderDistance, int blockCullingDistance) {
            this.displayName = displayName;
            this.entityRenderDistance = entityRenderDistance;
            this.blockCullingDistance = blockCullingDistance;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static Profile currentProfile = Profile.MEDIUM;

    public static void detectAndApply() {
        int cores = Runtime.getRuntime().availableProcessors();
        long maxHeapMb = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        if (cores <= 2 || maxHeapMb < 1500) {
            currentProfile = Profile.LOW;
        } else if (cores >= 8 && maxHeapMb >= 4000) {
            currentProfile = Profile.HIGH;
        } else {
            currentProfile = Profile.MEDIUM;
        }

        // Só aplica como PONTO DE PARTIDA — se o jogador já mexeu manualmente
        // no menu antes, NebulaConfig.userRenderDistance como referência.
        NebulaConfig.entityRenderDistance = currentProfile.entityRenderDistance;
        NebulaConfig.blockCullingDistance = currentProfile.blockCullingDistance;

        System.out.println("[Nebula] 🖥️ Perfil detectado: " + currentProfile.getDisplayName()
                + " (" + cores + " núcleos, " + maxHeapMb + "MB heap)");
    }

    public static Profile getCurrentProfile() {
        return currentProfile;
    }
}
