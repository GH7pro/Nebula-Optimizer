package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

public class BatterySaver implements NebulaModule {
    private static final BatterySaver INSTANCE = new BatterySaver();
    private boolean isBatterySaverActive = false;
    private int tickCounter = 0;
    
    // NOVO: Salva o FPS original do jogador para não forçar 60 se ele jogava em 120
    private int originalMaxFps = 60;
    private int originalRenderDistance = 12;
    
    private BatterySaver() {}
    
    public static BatterySaver getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Battery Saver";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔋 [Nebula] Battery Saver inicializado!");
    }
    
        @Override
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        tickCounter++;
        
        // Verifica a cada 5 segundos (100 ticks)
        if (tickCounter % 100 != 0) return;
        
        // LÓGICA CORRIGIDA: Liga se estiver ativado na config, desliga se estiver desativado!
        if (NebulaConfig.enableBatterySaver && !isBatterySaverActive) {
            activateBatterySaver(client);
        } else if (!NebulaConfig.enableBatterySaver && isBatterySaverActive) {
            deactivateBatterySaver(client);
        }
        
        // BÔNUS: Se o jogador estiver parado (AFK), baixar o FPS para economizar bateria!
        if (isBatterySaverActive && client.player.age % 100 == 0) {
            if (client.player.horizontalSpeed < 0.1) {
                client.options.getMaxFps().setValue(15);
            } else {
                client.options.getMaxFps().setValue(30);
            }
        }
    }
    
        private void activateBatterySaver(MinecraftClient client) {
        isBatterySaverActive = true;
        
        // Salva as configurações originais do jogador antes de mudar
        try {
            originalMaxFps = client.options.getMaxFps().getValue();
            originalRenderDistance = client.options.getViewDistance().getValue();
        } catch (Exception e) {
            originalMaxFps = 60;
            originalRenderDistance = 12;
        }
        
        System.out.println("🔋 [Nebula] ⚠️ MODO ECO ATIVADO! Economizando bateria.");
        
        try {
            client.options.getMaxFps().setValue(30); // Limite suave para economizar
//             client.options.getViewDistance().setValue(6);
            // REMOVIDO: client.options.getGraphicsMode().setValue(GraphicsMode.FAST); -> Causava crash no mobile
            client.options.getParticles().setValue(ParticlesMode.MINIMAL);
            client.options.getEntityShadows().setValue(false);
        } catch (Exception e) {
            System.err.println("🔋 [Nebula] Erro ao ativar economia: " + e.getMessage());
        }
    }
    
        private void deactivateBatterySaver(MinecraftClient client) {
        isBatterySaverActive = false;
        System.out.println("🔋 [Nebula] ✅ Modo Eco desativado. Restaurando config.");
        
        try {
            client.options.getMaxFps().setValue(originalMaxFps);
//             client.options.getViewDistance().setValue(originalRenderDistance);
            // REMOVIDO: client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
            client.options.getParticles().setValue(ParticlesMode.ALL);
            client.options.getEntityShadows().setValue(true);
        } catch (Exception e) {
            System.err.println("🔋 [Nebula] Erro ao restaurar: " + e.getMessage());
        }
    }
    
    public boolean isBatterySaverActive() {
        return isBatterySaverActive;
    }
    
    @Override
    public void shutdown() {
        // Ao fechar o jogo, garante que restaura tudo
        if (isBatterySaverActive) {
            deactivateBatterySaver(MinecraftClient.getInstance());
        }
    }
    
    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableBatterySaver;
    }
}
