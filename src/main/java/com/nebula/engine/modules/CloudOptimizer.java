package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;

/**
 * CloudOptimizer
 * AVISO: Mudar o CloudRenderMode dinamicamente no PojavLauncher/FCL causa
 * um NullPointerException no VertexBuffer das nuvens quando a distância de
 * renderização muda. Este módulo foi desativado para evitar crashes no celular.
 */
public class CloudOptimizer implements NebulaModule {
    private static CloudOptimizer instance;

    public static CloudOptimizer getInstance() {
        if (instance == null) {
            instance = new CloudOptimizer();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Cloud Optimizer";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Cloud Optimizer desativado para evitar crash de VertexBuffer no mobile.");
    }

    @Override
    public void tick() {
        // Desativado: Mudar nuvens dinamicamente quebra o VertexBuffer no mobile.
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean isEnabled() {
        // Retorna falso para o ModuleManager pular este módulo e economizar processamento.
        return false;
    }
}
