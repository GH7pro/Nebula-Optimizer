package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;

/**
 * ThermalCuller
 * Previne o superaquecimento do celular. Se o jogador ficar parado por mais
 * de 3 segundos, o mod baixa o FPS para 15 e avisa o Mixin para congelar
 * entidades distantes. Isso dá um "respiro" na CPU/GPU, esfriando o aparelho.
 */
public class ThermalCuller implements NebulaModule {
    private static ThermalCuller instance;

    public static ThermalCuller getInstance() {
        if (instance == null) {
            instance = new ThermalCuller();
        }
        return instance;
    }

    private int idleTicks = 0;
    private boolean isThrottling = false;
    private int originalMaxFps = 60;

    // Variável estática que o nosso Mixin vai checar
    public static boolean shouldFreezeEntities = false;

    @Override
    public String getName() {
        return "Thermal Culler";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Thermal Culler inicializado.");
    }

    @Override
    public void tick() {
        if (!isEnabled()) {
            // Se desligou no menu, restaura tudo
            if (isThrottling) {
                restoreFps();
            }
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        // Verifica se o jogador está parado ou com o inventário aberto
        boolean isIdle = client.player.horizontalSpeed < 0.1 && client.currentScreen == null;
        
        if (isIdle) {
            idleTicks++;
        } else {
            idleTicks = 0;
        }

        // Se ficou parado por 3 segundos (60 ticks), ativa a economia térmica
        if (idleTicks > 60 && !isThrottling) {
            activateThrottle(client);
        } 
        // Se voltou a se mover, desliga a economia
        else if (idleTicks == 0 && isThrottling) {
            restoreFps();
        }
    }

    private void activateThrottle(MinecraftClient client) {
        isThrottling = true;
        shouldFreezeEntities = true; // O Mixin vai ler isso e congelar os mobs
        
        try {
            originalMaxFps = client.options.getMaxFps().getValue();
        } catch (Exception e) {
            originalMaxFps = 60;
        }

        if (NebulaConfig.debugMode) {
            System.out.println("[Nebula] 🧊 Cortador Térmico: Jogador parado. Reduzindo FPS para esfriar o celular.");
        }

        // Baixa o FPS drasticamente. A GPU quase para de trabalhar, esfriando o aparelho.
        client.options.getMaxFps().setValue(15);
    }

    private void restoreFps() {
        isThrottling = false;
        shouldFreezeEntities = false; // Libera os mobs de volta

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        if (NebulaConfig.debugMode) {
            System.out.println("[Nebula] 🔥 Cortador Térmico: Jogador voltou a se mover. Restaurando FPS.");
        }

        client.options.getMaxFps().setValue(originalMaxFps);
    }

    @Override
    public void shutdown() {
        if (isThrottling) {
            restoreFps();
        }
    }

    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableThermalProtection;
    }
}
