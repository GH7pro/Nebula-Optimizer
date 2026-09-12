package com.nebula.lib.api;

import com.nebula.NebulaConfig;

/**
 * Ponto único de leitura das configurações de culling. Existia como import
 * em vários arquivos (NebulaEntityCuller, NebulaDebug, o mixin de block
 * entity) mas a classe em si nunca tinha sido criada — por isso o projeto
 * não compilava. Aqui é só um wrapper fino sobre o NebulaConfig, mas real:
 * cada método reflete o campo correspondente de verdade.
 */
public final class CullingAPI {

    private CullingAPI() {}

    public static boolean isEntityCullingEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableEntityCulling;
    }

    public static boolean isAngleCullingEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableAngleCulling;
    }

    public static boolean isAggressiveCullingEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableAggressiveCulling;
    }

    public static boolean isBlockCullingEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableBlockCulling;
    }

    /** Em chunks (o chamador multiplica por 16 pra virar blocos). */
    public static double getEntityRenderDistance() {
        return NebulaConfig.entityRenderDistance;
    }

    /** Em chunks (o chamador multiplica por 16 pra virar blocos). */
    public static double getBlockCullingDistance() {
        return NebulaConfig.blockCullingDistance;
    }

    /**
     * Multiplicador de densidade vindo do PerformanceAPI: quando o FPS cai,
     * a densidade some, encolhendo a distância efetiva de culling pra
     * aliviar o frame sem precisar mudar a config manualmente.
     */
    public static double getDensityMultiplier() {
        return PerformanceAPI.getDensityMultiplier();
    }
}
