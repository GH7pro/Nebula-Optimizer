package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * SmoothWorldLoading
 * Evita o lag de entrar em um mundo. Quando o jogador entra, a distância de
 * renderização é reduzida para 2 chunks e vai aumentando 1 chunk por segundo
 * até chegar no valor que o jogador escolheu nas configurações.
 */
public class SmoothWorldLoading implements NebulaModule {
    private static SmoothWorldLoading instance;

    private boolean isBoosting = false;
    private int targetRenderDistance = 12;
    private int currentLoadingDistance = 2;
    private int tickCounter = 0;

    public static SmoothWorldLoading getInstance() {
        if (instance == null) {
            instance = new SmoothWorldLoading();
        }
        return instance;
    }

    @Override
    public String getName() {
        return "Smooth World Loading";
    }

        @Override
    public void initialize() {
        System.out.println("[Nebula] Smooth World Loading inicializado.");

        // Registra um evento que dispara EXATAMENTE quando o jogador entra num mundo
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!isEnabled()) return;

            // client.execute garante que o código rode na Thread Principal do Jogo,
            // evitando que a câmera, FOV e interface distorçam ao entrar no mundo!
            client.execute(() -> {
                // Salva a distância que o jogador realmente quer usar
                targetRenderDistance = client.options.getViewDistance().getValue();
                
                // Se a distância alvo for muito pequena (ex: 2), não precisamos fazer nada
                if (targetRenderDistance <= 2) return;

                // Baixa a distância para 2 chunks instantaneamente para evitar o lag inicial
                currentLoadingDistance = 2;
//                 client.options.getViewDistance().setValue(currentLoadingDistance);
                
                isBoosting = true;
                tickCounter = 0;

                if (NebulaConfig.debugMode) {
                    System.out.println("[Nebula] 🚀 Mundo entrando! Smooth Loading ativado. Meta: " + targetRenderDistance + " chunks.");
                }
            });
        });
    }

    @Override
    public void tick() {
        if (!isEnabled() || !isBoosting) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;

        tickCounter++;

        // A cada 1 segundo (20 ticks), aumenta 1 chunk na distância
        if (tickCounter % 20 == 0) {
            if (currentLoadingDistance < targetRenderDistance) {
                currentLoadingDistance++;
//                 client.options.getViewDistance().setValue(currentLoadingDistance);
                
                if (NebulaConfig.debugMode) {
                    System.out.println("[Nebula] Smooth Loading: Subiu para " + currentLoadingDistance + " chunks.");
                }
            } else {
                // Chegou na distância alvo, desliga o boost
                isBoosting = false;
                if (NebulaConfig.debugMode) {
                    System.out.println("[Nebula] ✅ Mundo carregado suavemente!");
                }
            }
        }
    }

    @Override
    public void shutdown() {
        System.out.println("[Nebula] Smooth World Loading desligado.");
    }

    @Override
    public boolean isEnabled() {
        return NebulaConfig.enableNebula && NebulaConfig.enableSmoothWorldLoading;
    }
}
