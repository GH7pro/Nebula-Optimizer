package com.nebula;

import com.nebula.lib.api.BudgetAPI;
import com.nebula.lib.api.CullingAPI;
import com.nebula.lib.api.FocusAPI;
import com.nebula.lib.api.PerformanceAPI;
import com.nebula.lib.api.ProfileAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class NebulaDebug {

    public static int culledEntities = 0;
    public static int totalEntities = 0;
    public static int visibleSections = 0;

    public static void render(DrawContext context) {
        if (!NebulaConfig.debugMode) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        TextRenderer tr = client.textRenderer;
        int y = 30;
        int x = 10;

        context.drawText(tr, "§6§l[Nebula Debug]", x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, "§7Profile: §e" + ProfileAPI.getCurrentProfile().getDisplayName(), x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, String.format("§7FPS: §a%.1f §8| §7Avg: §a%.1f",
                PerformanceAPI.getCurrentFps(), PerformanceAPI.getAverageFps()), x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, "§7Entities: §f" + totalEntities + " §8| §7Culled: §c" + culledEntities, x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, "§7Density: §b" + PerformanceAPI.getDensityStatus() +
                " §8(" + String.format("%.2f", CullingAPI.getDensityMultiplier()) + ")", x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, "§7Focus: " + (FocusAPI.isFocused() ? "§aON" : "§cOFF") +
                " §8| §7Budget: §f" + BudgetAPI.getRenderedThisFrame() + "/" + BudgetAPI.getMaxEntitiesPerFrame(), x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, "§7Culling: " + onOff(CullingAPI.isEntityCullingEnabled()) +
                " §8| §7Angle: " + onOff(CullingAPI.isAngleCullingEnabled()) +
                " §8| §7Aggro: " + onOff(CullingAPI.isAggressiveCullingEnabled()), x, y, 0xFFFFFF, true);
        y += 12;

        context.drawText(tr, "§7Block Culling: " + onOff(CullingAPI.isBlockCullingEnabled()) +
                " §8| §7Particle Killer: " + onOff(NebulaConfig.enableParticleKiller), x, y, 0xFFFFFF, true);
    }

    private static String onOff(boolean v) {
        return v ? "§aON" : "§cOFF";
    }

    public static void resetCounters() {
        culledEntities = 0;
        totalEntities = 0;
        visibleSections = 0;
    }
}
