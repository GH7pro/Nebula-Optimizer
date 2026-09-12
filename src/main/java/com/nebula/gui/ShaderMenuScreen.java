package com.nebula.gui;

import com.nebula.shaders.ShaderManager;
import com.nebula.shaders.generator.ShaderPreset;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.List;

/**
 * Lista os presets de shader REAIS do Nebula (pós-processamento de verdade,
 * via o mesmo sistema que o vanilla usa pra Náusea/efeitos de status) e
 * deixa escolher um.
 *
 * Antes esta tela tinha duas abas (uma escaneando .zip/.jar soltos numa
 * pasta shaderpacks/ que nunca fazia nada de verdade, tipo Iris — e outra
 * com os presets, que geravam o GLSL certinho mas nunca chegavam a ser
 * aplicados). Simplificado pra uma coisa só: os presets, que agora
 * realmente mudam a tela.
 */
public class ShaderMenuScreen extends Screen {
    private final Screen parent;
    private final ShaderManager manager;

    private static final int CONTENT_START_Y = 60;
    private static final int ITEM_HEIGHT = 30;
    private static final int MAX_VISIBLE = 8;

    private int scrollOffset = 0;

    // Mesmo sistema unificado de toque do menu principal: só decide entre
    // "toque rápido" (seleciona) e "arrasto" (rola a lista) depois que o
    // ponteiro se move de verdade — assim dá pra arrastar mesmo em cima de
    // um item da lista, não só em espaço vazio.
    private boolean pointerDown = false;
    private boolean dragCommitted = false;
    private double pointerStartY;
    private double dragAccum = 0;

    public ShaderMenuScreen(Screen parent) {
        super(Text.literal("Nebula Shaders"));
        this.parent = parent;
        this.manager = ShaderManager.getInstance();
        if (manager.getPresets().isEmpty()) {
            manager.initialize();
        }
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Voltar"),
            b -> this.client.setScreen(parent)
        ).dimensions(width / 2 - 60, height - 28, 120, 20).build());
    }

    private int totalOptions() {
        return manager.getPresets().size() + 1; // +1 pro "Default (sem efeito)"
    }

    private int maxScroll() {
        return Math.max(0, totalOptions() - MAX_VISIBLE);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, new Color(18, 18, 28, 255).getRGB());
        context.fill(0, 0, width, 45, new Color(12, 12, 20, 255).getRGB());

        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("🎨 Shaders do Nebula").formatted(Formatting.GOLD), width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Efeitos de pós-processamento reais — muda a tela de verdade").formatted(Formatting.GRAY),
            width / 2, 26, 0x888888);

        if (manager.didLastLoadFail()) {
            context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("⚠ Falha ao carregar o último shader — voltou pro padrão").formatted(Formatting.RED),
                width / 2, 45, 0xFF5555);
        }

        List<ShaderPreset> presets = manager.getPresets();
        int total = totalOptions();
        if (scrollOffset > maxScroll()) scrollOffset = maxScroll();

        int y = CONTENT_START_Y;
        int end = Math.min(scrollOffset + MAX_VISIBLE, total);

        for (int i = scrollOffset; i < end; i++) {
            boolean isActive = i == manager.getActiveIndex();
            boolean hovered = mouseX > 20 && mouseX < width - 20 && mouseY > y && mouseY < y + ITEM_HEIGHT - 4;

            context.fill(20, y, width - 20, y + ITEM_HEIGHT - 4,
                isActive ? new Color(80, 200, 120, 60).getRGB()
                          : (hovered ? new Color(255, 255, 255, 15).getRGB() : new Color(255, 255, 255, 6).getRGB()));

            String name = i == 0 ? "Default (sem efeito)" : presets.get(i - 1).getName();
            String desc = i == 0 ? "Desliga qualquer shader" : presets.get(i - 1).getDescription();

            context.drawText(textRenderer, (isActive ? "▶ " : "  ") + name, 30, y + 6,
                isActive ? 0x55FF55 : 0xEEEEEE, false);
            context.drawText(textRenderer, desc, 30, y + 17, 0x888888, false);

            y += ITEM_HEIGHT;
        }

        if (total > MAX_VISIBLE) {
            context.drawText(textRenderer, "📌 " + (scrollOffset + 1) + "-" + end + " / " + total,
                20, CONTENT_START_Y + (MAX_VISIBLE * ITEM_HEIGHT) + 6, 0x555555, false);
        }

        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("💡 Toque num preset pra aplicar na hora").formatted(Formatting.DARK_GRAY),
            width / 2, height - 42, 0x555555);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        pointerDown = mouseX > 20 && mouseX < width - 20 && mouseY > CONTENT_START_Y
                && mouseY < CONTENT_START_Y + MAX_VISIBLE * ITEM_HEIGHT;
        dragCommitted = false;
        pointerStartY = mouseY;
        return pointerDown;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (pointerDown) {
            double totalDy = mouseY - pointerStartY;
            if (!dragCommitted && Math.abs(totalDy) > 4) {
                dragCommitted = true;
                dragAccum = 0;
            }
            if (dragCommitted) {
                int max = maxScroll();
                dragAccum += deltaY;
                while (dragAccum <= -ITEM_HEIGHT) {
                    scrollOffset = Math.min(max, scrollOffset + 1);
                    dragAccum += ITEM_HEIGHT;
                }
                while (dragAccum >= ITEM_HEIGHT) {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                    dragAccum -= ITEM_HEIGHT;
                }
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (pointerDown && !dragCommitted) {
            // Foi um toque rápido, não um arrasto — seleciona o preset.
            int idx = scrollOffset + (int) ((mouseY - CONTENT_START_Y) / ITEM_HEIGHT);
            if (idx >= 0 && idx < totalOptions() && button == 0) {
                manager.applyByIndex(idx);
            }
        }
        pointerDown = false;
        dragCommitted = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int max = maxScroll();
        if (max <= 0) return false;
        scrollOffset = (int) Math.max(0, Math.min(max, scrollOffset - amount));
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
