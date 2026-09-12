package com.nebula;

import com.nebula.command.NebulaCommand;
import net.fabricmc.api.ModInitializer;

public class Nebula implements ModInitializer {
    
    @Override
    public void onInitialize() {
        System.out.println("[Nebula] Mod inicializando...");
        
        // ===== REGISTRA O COMANDO /nebula =====
        NebulaCommand.register();
        System.out.println("[Nebula] 💻 Comando /nebula registrado!");
        
        System.out.println("[Nebula] ✅ Nebula Mod inicializado!");
        System.out.println("[Nebula] 💡 Use /nebula para abrir as configurações!");
        System.out.println("[Nebula] 💡 Ou clique no ícone do ModMenu!");
    }
}
