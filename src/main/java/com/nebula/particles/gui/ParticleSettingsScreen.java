package com.nebula.particles.gui;

import com.nebula.particles.ParticleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;

public class ParticleSettingsScreen extends Screen {
    private final Screen parent;
    private int scrollOffset = 0;
    private int selectedIndex = -1;
    private ParticleManager manager;
    
    private static final int HEADER_HEIGHT = 50;
    private static final int ITEM_HEIGHT = 30;
    private static final int ITEM_SPACING = 2;
    private static final int MAX_VISIBLE = 12;
    private static final int FOOTER_HEIGHT = 50;
    
    public ParticleSettingsScreen(Screen parent) {
        super(Text.literal("Particle Settings"));
        this.parent = parent;
        this.manager = ParticleManager.getInstance();
        this.manager.loadSettings();
    }
    
    @Override
    protected void init() {
        super.init();
        
        int buttonY = height - 25;
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("← Back"),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(this.parent);
                }
            }
        ).dimensions(10, buttonY, 80, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("❌ Disable All"),
            button -> {
                manager.disableAll();
            }
        ).dimensions(width - 270, buttonY, 100, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("✅ Enable All"),
            button -> {
                manager.resetAll();
            }
        ).dimensions(width - 160, buttonY, 70, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Apply"),
            button -> {
                manager.saveSettings();
                System.out.println("💀 [Nebula] Configurações de partículas aplicadas!");
            }
        ).dimensions(width - 80, buttonY, 70, 20).build());
        
        this.addDrawableChild(new TextWidget(
            20, 12, 300, 22,
            Text.literal("💀 Particle Settings").formatted(Formatting.GOLD, Formatting.BOLD),
            textRenderer
        ));
        
        this.addDrawableChild(new TextWidget(
            20, 34, 300, 16,
            Text.literal("Select which particles to remove").formatted(Formatting.GRAY),
            textRenderer
        ));
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, new Color(25, 25, 40, 255).getRGB());
        context.fill(0, 0, width, HEADER_HEIGHT, new Color(15, 15, 30, 255).getRGB());
        context.fill(0, HEADER_HEIGHT - 1, width, HEADER_HEIGHT, new Color(255, 180, 0, 60).getRGB());
        
        String[] particleTypes = ParticleManager.PARTICLE_TYPES;
        int y = HEADER_HEIGHT + 10;
        int start = scrollOffset;
        int end = Math.min(start + MAX_VISIBLE, particleTypes.length);
        
        for (int i = start; i < end; i++) {
            String type = particleTypes[i];
            boolean enabled = manager.isParticleEnabled(type);
            boolean hover = mouseX > 20 && mouseX < width - 20 && 
                           mouseY > y && mouseY < y + ITEM_HEIGHT;
            boolean selected = (i == selectedIndex);
            
            if (selected) {
                context.fill(20, y, width - 20, y + ITEM_HEIGHT, new Color(255, 180, 0, 40).getRGB());
            } else if (hover) {
                context.fill(20, y, width - 20, y + ITEM_HEIGHT, new Color(255, 255, 255, 10).getRGB());
            }
            
            context.drawBorder(20, y, width - 40, ITEM_HEIGHT, new Color(60, 60, 80, 80).getRGB());
            
            String displayName = formatParticleName(type);
            context.drawText(textRenderer, displayName, 35, y + 9, 0xDDDDDD, false);
            
            String status = enabled ? "✅ ON" : "❌ OFF";
            int color = enabled ? 0x55FF55 : 0xFF5555;
            context.drawText(textRenderer, status, width - 60, y + 9, color, false);
            
            y += ITEM_HEIGHT + ITEM_SPACING;
        }
        
        if (ParticleManager.PARTICLE_TYPES.length > MAX_VISIBLE) {
            context.drawText(textRenderer, 
                "▼ " + (ParticleManager.PARTICLE_TYPES.length - MAX_VISIBLE) + " more (scroll)", 
                20, y + 10, 
                0x555555, false);
        }
        
        int footerY = height - FOOTER_HEIGHT;
        context.fill(0, footerY, width, height, new Color(15, 15, 30, 255).getRGB());
        context.fill(0, footerY - 1, width, footerY, new Color(255, 180, 0, 20).getRGB());
        
        int total = ParticleManager.PARTICLE_TYPES.length;
        int enabledCount = 0;
        for (String type : ParticleManager.PARTICLE_TYPES) {
            if (manager.isParticleEnabled(type)) enabledCount++;
        }
        
        context.drawText(textRenderer, 
            "📊 " + enabledCount + "/" + total + " partículas ativas", 
            20, footerY + 16, 
            0x888888, false);
        context.drawText(textRenderer, 
            "💡 Click to toggle | Scroll to see more", 
            20, footerY + 34, 
            0x444444, false);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private String formatParticleName(String type) {
        String[] parts = type.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }
        return sb.toString();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int y = HEADER_HEIGHT + 10;
        int start = scrollOffset;
        int end = Math.min(start + MAX_VISIBLE, ParticleManager.PARTICLE_TYPES.length);
        
        for (int i = start; i < end; i++) {
            if (mouseX > 20 && mouseX < width - 20 && 
                mouseY > y && mouseY < y + ITEM_HEIGHT) {
                selectedIndex = i;
                String type = ParticleManager.PARTICLE_TYPES[i];
                boolean current = manager.isParticleEnabled(type);
                manager.setParticleEnabled(type, !current);
                return true;
            }
            y += ITEM_HEIGHT + ITEM_SPACING;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int maxScroll = Math.max(0, ParticleManager.PARTICLE_TYPES.length - MAX_VISIBLE);
        scrollOffset = (int) Math.max(0, Math.min(scrollOffset - amount, maxScroll));
        return true;
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
