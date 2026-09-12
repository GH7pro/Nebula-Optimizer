package com.nebula.gui;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * NebulaHudOverlay
 * Desenha um indicador na tela mostrando o nível de otimização do Nebula.
 * Só aparece quando o mod está atuando ativamente (Nível 1+ ou Modo Pânico).
 */
public class NebulaHudOverlay {

    public static void render(DrawContext context) {
        if (!NebulaConfig.enableNebula) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.hudHidden) return;

        AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
        if (optimizer == null) return;

        int level = optimizer.getCurrentOptimizationLevel();
        boolean panic = optimizer.isInPanicMode();

        // Se não está em pânico e o nível é 0, não desenha nada na tela (jogo rodando bem)
        if (!panic && level == 0) return;

        String text;
        int color;

        if (panic) {
            // Efeito de "piscar" rápido no modo pânico
            long time = System.currentTimeMillis() / 250; // Muda a cada 0.25 segundos
            if (time % 2 == 0) {
                text = "🚨 NEBULA: PÂNICO 🚨";
                color = 0xFF5555; // Vermelho claro
            } else {
                text = "   NEBULA: PÂNICO   ";
                color = 0xFFFFFF; // Branco
            }
        } else {
            text = "⚡ NEBULA: NÍVEL " + level;
            color = 0xFFAA00; // Amarelo/Laranja
        }

        // Posição: Canto superior esquerdo, um pouco abaixo da hotbar de FPS do vanilla
        int x = 10;
        int y = 10;

        // Desenha o texto com sombra para não sumir no fundo claro
        context.drawTextWithShadow(client.textRenderer, Text.literal(text), x, y, color);
    }
}
