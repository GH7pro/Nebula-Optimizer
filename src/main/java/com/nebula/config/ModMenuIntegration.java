package com.nebula.config;

import com.nebula.gui.NebulaSettingsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // Abre a tela de configurações gerais do Nebula em vez da de vídeo
            return new NebulaSettingsScreen(parent);
        };
    }
}
