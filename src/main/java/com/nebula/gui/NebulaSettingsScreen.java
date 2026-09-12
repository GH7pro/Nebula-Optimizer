package com.nebula.gui;

import com.nebula.NebulaConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class NebulaSettingsScreen extends Screen {
    private final Screen parent;
    private int selectedCategory = 0;
    private List<Category> categories = new ArrayList<>();
    
    private static final int HEADER_HEIGHT = 40;
    private static final int SIDEBAR_WIDTH = 180;
    private static final int SETTINGS_START_X = SIDEBAR_WIDTH + 20;
    private static final int SETTINGS_START_Y = HEADER_HEIGHT + 20;
    private static final int SETTING_SPACING = 30;
    
    private static class Category {
        String name;
        String icon;
        List<Setting> settings;
        
        Category(String name, String icon) {
            this.name = name;
            this.icon = icon;
            this.settings = new ArrayList<>();
        }
    }
    
    private static class Setting {
        String name;
        String description;
        String key;
        SettingType type;
        Object value;
        Object defaultValue;
        Object min;
        Object max;
        
        enum SettingType {
            TOGGLE, SLIDER, SELECT
        }
    }
    
    public NebulaSettingsScreen(Screen parent) {
        super(Text.literal("Nebula Settings"));
        this.parent = parent;
        initCategories();
        initSettings();
    }
    
    private void initCategories() {
        categories.add(new Category("Entity", "👾"));
        categories.add(new Category("Blocks", "🧱"));
        categories.add(new Category("Particles", "✨"));
        categories.add(new Category("Resolution", "📐"));
        categories.add(new Category("Textures", "🖼️"));
        categories.add(new Category("Ticking", "⏱️"));
        categories.add(new Category("Chunk", "🔮"));
        categories.add(new Category("Memory", "💾"));
        categories.add(new Category("Shaders", "🎨"));
        categories.add(new Category("Battery", "🔋"));
        categories.add(new Category("Advanced", "⚡"));
        categories.add(new Category("General", "⚙️"));
    }
    
    private void initSettings() {
        NebulaConfig config = NebulaConfig.get();
        
        addSetting(0, "Entity Culling", "Oculta entidades fora do campo de visão", 
                   "enableEntityCulling", config.enableEntityCulling);
        addSetting(0, "Angle Culling", "Oculta entidades baseado no ângulo da câmera", 
                   "enableAngleCulling", config.enableAngleCulling);
        addSetting(0, "Aggressive Culling", "Culling mais agressivo (pode causar pop-in)", 
                   "enableAggressiveCulling", config.enableAggressiveCulling);
        
        addSetting(1, "Block Culling", "Oculta blocos fora do campo de visão", 
                   "enableBlockCulling", config.enableBlockCulling);
        addSetting(1, "Vertical Culling", "Oculta blocos verticalmente", 
                   "enableVerticalCulling", config.enableVerticalCulling);
        
        addSetting(2, "Particle Limit", "Limita o número de partículas", 
                   "enableParticleLimit", config.enableParticleLimit);
        
        addSetting(3, "Dynamic Resolution", "Ajusta resolução automaticamente", 
                   "enableDynamicResolution", config.enableDynamicResolution);
        
        addSetting(4, "Texture Streaming", "Carrega texturas apenas quando necessário", 
                   "enableTextureStreaming", config.enableTextureStreaming);
        
        addSetting(5, "Intelligent Ticking", "Reduz ticks de entidades distantes", 
                   "enableIntelligentTicking", config.enableIntelligentTicking);
        
        addSetting(6, "Smart Chunk Loading", "Pré-carrega chunks preditivamente", 
                   "enableSmartChunkLoading", config.enableSmartChunkLoading);
        
        addSetting(7, "Memory Management", "Gerencia RAM automaticamente", 
                   "enableMemoryManagement", config.enableMemoryManagement);
        
        addSetting(8, "Dynamic Shaders", "Ajusta qualidade de shaders dinamicamente", 
                   "enableDynamicShaders", config.enableDynamicShaders);
        
        addSetting(9, "Battery Saver", "Economiza bateria em dispositivos móveis", 
                   "enableBatterySaver", config.enableBatterySaver);
        
        addSetting(10, "Frame Prediction", "Previsão de quadros com IA", 
                   "enableFramePrediction", config.enableFramePrediction);
        addSetting(10, "Thermal Protection", "Proteção contra superaquecimento", 
                   "enableThermalProtection", config.enableThermalProtection);
        addSetting(10, "Smart Sleep", "Modo sleep quando jogador está parado", 
                   "enableSmartSleep", config.enableSmartSleep);
        
        addSetting(11, "Debug Mode", "Mostra informações de debug", 
                   "debugMode", config.debugMode);
        addSetting(11, "Show FPS", "Mostra FPS no console", 
                   "showFps", config.showFps);
    }
    
    private void addSetting(int categoryIndex, String name, String description, 
                           String key, Object currentValue) {
        Setting setting = new Setting();
        setting.name = name;
        setting.description = description;
        setting.key = key;
        setting.type = Setting.SettingType.TOGGLE;
        setting.value = currentValue;
        categories.get(categoryIndex).settings.add(setting);
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("← Back"),
            button -> this.client.setScreen(parent)
        ).dimensions(10, height - 30, 80, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("↺ Reset All"),
            button -> {}
        ).dimensions(width - 180, height - 30, 80, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("✓ Apply"),
            button -> {}
        ).dimensions(width - 90, height - 30, 80, 20).build());
        
        this.addDrawableChild(new TextWidget(
            width / 2 - 100, 10, 200, 20,
            Text.literal("⚡ Nebula Optimizer").formatted(Formatting.GOLD, Formatting.BOLD),
            textRenderer
        ));
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, new Color(20, 20, 30, 240).getRGB());
        context.fill(0, 0, width, HEADER_HEIGHT, new Color(40, 40, 60, 200).getRGB());
        
        context.drawText(textRenderer, "⚡ Nebula Optimizer", 20, 14, 0xFFAA00, false);
        context.drawText(textRenderer, "v1.0.0", 20, 28, 0x888888, false);
        
        context.fill(0, HEADER_HEIGHT, SIDEBAR_WIDTH, height, new Color(30, 30, 45, 200).getRGB());
        
        int y = HEADER_HEIGHT + 10;
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            int color = (selectedCategory == i) ? 0xFFAA00 : 0x888888;
            
            if (selectedCategory == i) {
                context.fill(0, y, 3, y + 28, 0xFFAA00);
            }
            
            String display = cat.icon + " " + cat.name;
            context.drawText(textRenderer, display, 15, y + 8, color, false);
            y += 32;
        }
        
        int settingY = SETTINGS_START_Y;
        Category currentCat = categories.get(selectedCategory);
        
        for (Setting setting : currentCat.settings) {
            context.drawText(textRenderer, setting.name, SETTINGS_START_X, settingY, 0xFFFFFF, false);
            
            String valueStr = setting.value.toString();
            if (setting.value instanceof Boolean) {
                valueStr = (Boolean) setting.value ? "✅ ON" : "❌ OFF";
            }
            int valueColor = (setting.value instanceof Boolean && (Boolean) setting.value) ? 
                             0x55FF55 : 0xFF5555;
            context.drawText(textRenderer, valueStr, width - 120, settingY, valueColor, false);
            
            settingY += SETTING_SPACING;
        }
        
        context.drawText(textRenderer, "💡 Passe o mouse sobre uma configuração para ver a descrição", 
                         SETTINGS_START_X, height - 40, 0x888888, false);
        context.drawText(textRenderer, "🔧 " + currentCat.settings.size() + " configurações", 
                         SETTINGS_START_X, height - 25, 0x888888, false);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX > 0 && mouseX < SIDEBAR_WIDTH && mouseY > HEADER_HEIGHT) {
            int index = (int)((mouseY - HEADER_HEIGHT) / 32);
            if (index >= 0 && index < categories.size()) {
                selectedCategory = index;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
