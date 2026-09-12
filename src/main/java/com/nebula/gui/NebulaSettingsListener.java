package com.nebula.gui;

import com.nebula.NebulaConfig;

public class NebulaSettingsListener {
    private static NebulaConfig config = NebulaConfig.get();
    
    public static void onSettingChange(String key, Object value) {
        System.out.println("[Nebula] Setting changed: " + key + " = " + value);
        
        // Aqui você pode adicionar lógica para quando uma configuração muda
        switch (key) {
            case "enableEntityCulling":
                // Atualiza o módulo
                break;
            case "enableDynamicResolution":
                // Ativa/Desativa resolução dinâmica
                break;
            case "enableBatterySaver":
                // Ativa/Desativa economia de bateria
                break;
        }
    }
}
