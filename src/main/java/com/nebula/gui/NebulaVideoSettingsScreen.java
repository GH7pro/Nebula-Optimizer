package com.nebula.gui;

import com.nebula.NebulaConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Layout inspirado no menu de vídeo do Sodium (busca no topo, checkboxes
 * quadrados, linhas divisórias entre grupos) — mas com a identidade visual
 * do Nebula e, em vez de categorias de OUTROS mods (tipo "MoreCulling"),
 * as categorias são vanilla + Nebula misturadas, igual o layout anterior.
 */
public class NebulaVideoSettingsScreen extends Screen {

    private final Screen parent;
    private final GameOptions options;

    private int selectedCategory = 0;
    private int scrollOffset = 0;
    private String searchQuery = "";
    private TextFieldWidget searchField;
    private ButtonWidget doneButton, resetButton, applyButton, shadersButton, closeButton, themeButton;

    // Compartilhados pelo novo sistema unificado de arraste abaixo.
    private int dragAnchorValue;
    private double dragScrollAccum = 0;

    // Scroll da SIDEBAR (categorias) — antes não existia, então categorias
    // além do que cabia na tela ficavam inacessíveis, escondidas atrás da
    // fileira de botões.
    private int sidebarScrollOffset = 0;

    // Estado unificado de "toque": grava onde o dedo/mouse tocou e só
    // decide o que fazer (tocar = clique, arrastar vertical = rolar,
    // arrastar horizontal = ajustar número) depois que o ponteiro se move
    // o suficiente. Sem isso, arrastar em cima de uma linha de opção
    // (em vez de num espaço vazio) não rolava nada.
    private static final int DRAG_NONE = 0, DRAG_SCROLL = 1, DRAG_NUMERIC = 2;
    private int dragCommitted = DRAG_NONE;
    private boolean pointerIsSidebar = false;
    private int pointerCategoryIndex = -1;
    private Setting pointerSetting = null;
    private double pointerStartX, pointerStartY;
    private int pointerButton = 0;

    private final List<Category> categories = new ArrayList<>();

    private static final int TOPBAR_HEIGHT = 34;
    private static final int HEADER_HEIGHT = TOPBAR_HEIGHT; // mantém o nome usado nos cálculos de layout
    private static final int SIDEBAR_WIDTH = 150;
    private static final int BOTTOM_HEIGHT = 58;
    private static final int ROW_HEIGHT = 26;

    // Cores (dependem do tema — ver applyTheme())
    private int BG, HEADER_BG, SIDEBAR_BG, ACCENT, TEXT, MUTED, ON, OFF, DISABLED;

            private void applyTheme() {
        int theme = NebulaConfig.uiTheme;
        if (theme == 1) {
            // LIGHT (Claro limpo)
            BG = new Color(245, 245, 247, 255).getRGB();
            HEADER_BG = new Color(235, 235, 238, 255).getRGB();
            SIDEBAR_BG = new Color(240, 240, 243, 255).getRGB();
            ACCENT = 0xFF007ACC; // Azul Microsoft
            TEXT = 0xFF1F2430;
            MUTED = 0xFF6B7280;
            ON = 0xFF10B981; // Verde esmeralda
            OFF = 0xFF9CA3AF;
            DISABLED = 0xFFD1D5DB;
        } else {
            // DARK (Estilo Sodium Deep Space - Padrão)
            BG = new Color(20, 20, 23, 255).getRGB();
            HEADER_BG = new Color(15, 15, 18, 255).getRGB();
            SIDEBAR_BG = new Color(18, 18, 21, 255).getRGB();
            ACCENT = 0xFF00CEC9; // Ciano Nebula
            TEXT = 0xFFE8E8EE;
            MUTED = 0xFF8A8A9A;
            ON = 0xFF2ECC71; // Verde brilhante
            OFF = 0xFF6C6C7C;
            DISABLED = 0xFF4A4A55;
        }
    }

    private static String themeName(int theme) {
        return switch (theme) {
            case 1 -> "Light";
            default -> "Dark";
        };
    }

    private static class Category {
        final String name;
        final String icon;
        final List<Setting> settings = new ArrayList<>();

        Category(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }
    }

    private static class Setting {
        final String name;
        final String description;
        final SettingType type;
        final Supplier<String> display;
        final Runnable onClick;
        final Runnable onLeft;
        final Runnable onRight;
        String categoryLabel; // preenchido só durante busca (mostra de qual categoria veio)

        // Desenha uma linha fina acima desta linha, separando "grupos" de
        // opções relacionadas (como no Sodium).
        boolean groupBreak = false;

        // Settings do Nebula que só têm efeito se "Enable Nebula" (o
        // interruptor mestre) estiver ligado. Quando desligado, aparecem
        // acinzentadas — pra deixar claro que não estão fazendo nada agora,
        // em vez de fingir que estão ativas.
        boolean nebulaGated = false;

        // Só preenchido para settings numéricas (Render Distance etc.) —
        // permite ARRASTAR pra ajustar rápido / clicar em qualquer ponto do
        // slider, igual o vanilla. O staging (só aplicar de vez no Apply/
        // Done) é feito nas fábricas (toggle/cycleGeneric/numericSlider/
        // nebulaToggle) via commitActions/resetActions, não aqui.
        final java.util.function.IntSupplier dragGet;
        final java.util.function.IntConsumer dragSet;
        final int dragMin;
        final int dragMax;

        enum SettingType { TOGGLE, CYCLE, ACTION, SLIDER }

        Setting(String name, String description, SettingType type,
                Supplier<String> display, Runnable onClick, Runnable onLeft, Runnable onRight) {
            this(name, description, type, display, onClick, onLeft, onRight, null, null, 0, 0);
        }

        Setting(String name, String description, SettingType type,
                Supplier<String> display, Runnable onClick, Runnable onLeft, Runnable onRight,
                java.util.function.IntSupplier dragGet, java.util.function.IntConsumer dragSet,
                int dragMin, int dragMax) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.display = display;
            this.onClick = onClick;
            this.onLeft = onLeft;
            this.onRight = onRight;
            this.dragGet = dragGet;
            this.dragSet = dragSet;
            this.dragMin = dragMin;
            this.dragMax = dragMax;
        }

        boolean isDraggable() {
            return dragGet != null && dragSet != null;
        }

        Setting group() {
            this.groupBreak = true;
            return this;
        }
    }

    public NebulaVideoSettingsScreen(Screen parent, GameOptions options) {
        super(Text.literal("Video Settings"));
        this.parent = parent;
        this.options = options;
        buildCategories();
    }

    private void buildCategories() {
        categories.clear();

        // ===== 1. VANILLA VIDEO =====
        Category video = new Category("Video", "🎥");
        video.settings.add(numericSlider(
            "Render Distance",
            "Distância de renderização dos chunks",
            () -> options.getViewDistance().getValue(),
            v -> {
                options.getViewDistance().setValue(v);
                // Sem isso, os módulos de performance (Sprint Optimizer,
                // Battery Saver, Memory Manager) restauravam pra um valor
                // antigo/travado depois de qualquer pico temporário deles.
                NebulaConfig.userRenderDistance = v;
            },
            2, 32, 1, 12, v -> v + " chunks"
        ));
        video.settings.add(numericSlider(
            "Simulation Distance",
            "Distância de simulação do mundo",
            () -> options.getSimulationDistance().getValue(),
            v -> options.getSimulationDistance().setValue(v),
            5, 32, 1, 8, v -> v + " chunks"
        ));
        video.settings.add(cycleGeneric(
            "Graphics",
            "Qualidade gráfica (Fast / Fancy)",
            () -> options.getGraphicsMode().getValue(),
            v -> options.getGraphicsMode().setValue(v),
            GraphicsMode.FANCY,
            m -> m == GraphicsMode.FAST ? GraphicsMode.FANCY : GraphicsMode.FAST,
            m -> m == GraphicsMode.FAST ? GraphicsMode.FANCY : GraphicsMode.FAST,
            Object::toString
        ).group());
        video.settings.add(cycleGeneric(
            "Particles",
            "Quantidade de partículas",
            () -> options.getParticles().getValue(),
            v -> options.getParticles().setValue(v),
            ParticlesMode.ALL,
            m -> m == ParticlesMode.MINIMAL ? ParticlesMode.DECREASED
                    : m == ParticlesMode.DECREASED ? ParticlesMode.ALL : ParticlesMode.MINIMAL,
            m -> m == ParticlesMode.ALL ? ParticlesMode.DECREASED
                    : m == ParticlesMode.DECREASED ? ParticlesMode.MINIMAL : ParticlesMode.ALL,
            Object::toString
        ));
        video.settings.add(cycleGeneric(
            "Clouds",
            "Renderização das nuvens",
            () -> options.getCloudRenderMode().getValue(),
            v -> options.getCloudRenderMode().setValue(v),
            CloudRenderMode.FANCY,
            m -> m == CloudRenderMode.OFF ? CloudRenderMode.FAST
                    : m == CloudRenderMode.FAST ? CloudRenderMode.FANCY : CloudRenderMode.OFF,
            m -> m == CloudRenderMode.FANCY ? CloudRenderMode.FAST
                    : m == CloudRenderMode.FAST ? CloudRenderMode.OFF : CloudRenderMode.FANCY,
            Object::toString
        ));
        video.settings.add(toggle(
            "Smooth Lighting",
            "Iluminação suave",
            () -> options.getAo().getValue(),
            v -> options.getAo().setValue(v),
            true
        ).group());
        video.settings.add(toggle(
            "Entity Shadows",
            "Sombras das entidades",
            () -> options.getEntityShadows().getValue(),
            v -> options.getEntityShadows().setValue(v),
            true
        ));
        video.settings.add(toggle(
            "VSync",
            "Sincronização vertical",
            () -> options.getEnableVsync().getValue(),
            v -> options.getEnableVsync().setValue(v),
            true
        ));
        video.settings.add(numericSlider(
            "Max FPS",
            "Limite de FPS",
            () -> options.getMaxFps().getValue(),
            v -> options.getMaxFps().setValue(v),
            20, 260, 10, 60,
            v -> v >= 260 ? "Unlimited" : v + " FPS"
        ));
        video.settings.add(toggle(
            "View Bobbing",
            "Balanço da câmera ao andar",
            () -> options.getBobView().getValue(),
            v -> options.getBobView().setValue(v),
            true
        ).group());
        categories.add(video);

        // ===== 2. NEBULA PERFORMANCE =====
        Category perf = new Category("Performance", "⚡");
        perf.settings.add(gated(nebulaToggle("Entity Culling", "Corta entidades fora da visão",
            () -> NebulaConfig.enableEntityCulling, v -> NebulaConfig.enableEntityCulling = v)));
        perf.settings.add(gated(nebulaToggle("Angle Culling", "Corta entidades atrás da câmera",
            () -> NebulaConfig.enableAngleCulling, v -> NebulaConfig.enableAngleCulling = v)));
        perf.settings.add(gated(nebulaToggle("Aggressive Culling", "Culling mais agressivo",
            () -> NebulaConfig.enableAggressiveCulling, v -> NebulaConfig.enableAggressiveCulling = v)));
        perf.settings.add(gated(nebulaToggle("Block Culling", "Corta seções de blocos distantes",
            () -> NebulaConfig.enableBlockCulling, v -> NebulaConfig.enableBlockCulling = v)).group());
        perf.settings.add(gated(nebulaToggle("Particle Limit", "Limita quantidade de partículas",
            () -> NebulaConfig.enableParticleLimit, v -> NebulaConfig.enableParticleLimit = v)).group());
        perf.settings.add(gated(nebulaToggle("Far Entity Simplifier", "Simplifica entidades longe",
            () -> NebulaConfig.enableFarEntitySimplifier, v -> NebulaConfig.enableFarEntitySimplifier = v)).group());
        perf.settings.add(gated(nebulaToggle("Entity Cleanup", "Limpa entidades desnecessárias",
            () -> NebulaConfig.enableEntityCleanup, v -> NebulaConfig.enableEntityCleanup = v)));
        categories.add(perf);

        // ===== 3. NEBULA VISUAL =====
        Category visual = new Category("Visual", "🎨");
        visual.settings.add(gated(nebulaToggle("Reduce Bobbing", "Remove balanço da câmera",
            () -> NebulaConfig.enableReduceShakeBobbing, v -> NebulaConfig.enableReduceShakeBobbing = v)));
        visual.settings.add(gated(nebulaToggle("Disable Pumpkin Overlay", "Remove overlay da abóbora",
            () -> NebulaConfig.enablePumpkinOverlayDisabler, v -> NebulaConfig.enablePumpkinOverlayDisabler = v)));
        visual.settings.add(gated(nebulaToggle("Fire Overlay Opacity", "Deixa fogo mais transparente",
            () -> NebulaConfig.enableFireOverlayOpacity, v -> NebulaConfig.enableFireOverlayOpacity = v)));
        visual.settings.add(gated(nebulaToggle("Disable Vignette", "Remove vinheta da tela",
            () -> NebulaConfig.enableVignetteDisabler, v -> NebulaConfig.enableVignetteDisabler = v)));
        visual.settings.add(gated(nebulaToggle("Zoom Feature", "Zoom com tecla C",
            () -> NebulaConfig.enableZoomFeature, v -> NebulaConfig.enableZoomFeature = v)).group());
        visual.settings.add(gated(nebulaToggle("Fullbright Toggle", "Fullbright com tecla B",
            () -> NebulaConfig.enableFullbrightToggle, v -> NebulaConfig.enableFullbrightToggle = v)));
        visual.settings.add(gated(nebulaToggle("Coordinates Display", "Mostra coordenadas na tela",
            () -> NebulaConfig.enableCoordinatesDisplay, v -> NebulaConfig.enableCoordinatesDisplay = v)));
        categories.add(visual);
        visual.settings.add(gated(nebulaToggle("Optimized Clouds", "Forca nuvens 2D (Fast) para salvar GPU",
            () -> NebulaConfig.enableOptimizedClouds, v -> NebulaConfig.enableOptimizedClouds = v)).group());

        // ===== 4. NEBULA ADVANCED =====
        Category advanced = new Category("Advanced", "🔧");
        advanced.settings.add(nebulaToggle("Enable Nebula", "Liga/desliga TODAS as otimizações do Nebula de uma vez",
            () -> NebulaConfig.enableNebula, v -> NebulaConfig.enableNebula = v));
        advanced.settings.add(gated(nebulaToggle("Auto Profile", "Detecta PC e aplica preset",
            () -> NebulaConfig.enableAutoProfile, v -> NebulaConfig.enableAutoProfile = v)).group());
        categories.add(advanced);

        // ===== CULLING & LOD =====
        Category cullinglod = new Category("Culling & LOD", "🎯");
        cullinglod.settings.add(gated(nebulaToggle("Adaptive Entity LOD", "Otimização: Adaptive Entity LOD",
            () -> NebulaConfig.enableAdaptiveEntityLOD, v -> NebulaConfig.enableAdaptiveEntityLOD = v)).group());
        cullinglod.settings.add(gated(nebulaToggle("Block Entity Culling", "Otimização: Block Entity Culling",
            () -> NebulaConfig.enableBlockEntityCulling, v -> NebulaConfig.enableBlockEntityCulling = v)));
        cullinglod.settings.add(gated(nebulaToggle("Entity LOD", "Otimização: Entity LOD",
            () -> NebulaConfig.enableEntityLOD, v -> NebulaConfig.enableEntityLOD = v)));
        cullinglod.settings.add(gated(nebulaToggle("Selective Culling", "Otimização: Selective Culling",
            () -> NebulaConfig.enableSelectiveCulling, v -> NebulaConfig.enableSelectiveCulling = v)));
        cullinglod.settings.add(gated(nebulaToggle("Smart Cleanup", "Otimização: Smart Cleanup",
            () -> NebulaConfig.enableSmartCleanup, v -> NebulaConfig.enableSmartCleanup = v)));
        cullinglod.settings.add(gated(nebulaToggle("Smart Cleanup Advanced", "Otimização: Smart Cleanup Advanced",
            () -> NebulaConfig.enableSmartCleanupAdvanced, v -> NebulaConfig.enableSmartCleanupAdvanced = v)));
        cullinglod.settings.add(gated(nebulaToggle("Vertical Culling", "Otimização: Vertical Culling",
            () -> NebulaConfig.enableVerticalCulling, v -> NebulaConfig.enableVerticalCulling = v)));
        categories.add(cullinglod);

        // ===== AI & PREDICTION =====
        Category aiprediction = new Category("AI & Prediction", "🧠");
        aiprediction.settings.add(gated(nebulaToggle("FPS History", "Otimização: FPS History",
            () -> NebulaConfig.enableFPSHistory, v -> NebulaConfig.enableFPSHistory = v)));
        aiprediction.settings.add(gated(nebulaToggle("FPS Predictor", "Otimização: FPS Predictor",
            () -> NebulaConfig.enableFPSPredictor, v -> NebulaConfig.enableFPSPredictor = v)));
        aiprediction.settings.add(gated(nebulaToggle("Frame Prediction", "Otimização: Frame Prediction",
            () -> NebulaConfig.enableFramePrediction, v -> NebulaConfig.enableFramePrediction = v)));
        aiprediction.settings.add(gated(nebulaToggle("Performance Analytics", "Otimização: Performance Analytics",
            () -> NebulaConfig.enablePerformanceAnalytics, v -> NebulaConfig.enablePerformanceAnalytics = v)));
        aiprediction.settings.add(gated(nebulaToggle("Performance Graph", "Otimização: Performance Graph",
            () -> NebulaConfig.enablePerformanceGraph, v -> NebulaConfig.enablePerformanceGraph = v)));
        aiprediction.settings.add(gated(nebulaToggle("Performance Prediction", "Otimização: Performance Prediction",
            () -> NebulaConfig.enablePerformancePrediction, v -> NebulaConfig.enablePerformancePrediction = v)));
        aiprediction.settings.add(gated(nebulaToggle("Performance Score", "Otimização: Performance Score",
            () -> NebulaConfig.enablePerformanceScore, v -> NebulaConfig.enablePerformanceScore = v)));
        aiprediction.settings.add(gated(nebulaToggle("Smart AI Assistant", "Otimização: Smart AI Assistant",
            () -> NebulaConfig.enableSmartAIAssistant, v -> NebulaConfig.enableSmartAIAssistant = v)));
        aiprediction.settings.add(gated(nebulaToggle("Smart Entity AI", "Otimização: Smart Entity AI",
            () -> NebulaConfig.enableSmartEntityAI, v -> NebulaConfig.enableSmartEntityAI = v)));
        aiprediction.settings.add(gated(nebulaToggle("World Analyzer", "Otimização: World Analyzer",
            () -> NebulaConfig.enableWorldAnalyzer, v -> NebulaConfig.enableWorldAnalyzer = v)));
        categories.add(aiprediction);

        // ===== MOBILE & BATTERY =====
        Category mobilebatter = new Category("Mobile & Battery", "🔋");
        mobilebatter.settings.add(gated(nebulaToggle("AFK Detection", "Otimização: AFK Detection",
            () -> NebulaConfig.enableAFKDetection, v -> NebulaConfig.enableAFKDetection = v)).group());
        mobilebatter.settings.add(gated(nebulaToggle("Adaptive FPS", "Otimização: Adaptive FPS",
            () -> NebulaConfig.enableAdaptiveFPS, v -> NebulaConfig.enableAdaptiveFPS = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Adaptive GUI Scale", "Otimização: Adaptive GUI Scale",
            () -> NebulaConfig.enableAdaptiveGUIScale, v -> NebulaConfig.enableAdaptiveGUIScale = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Battery Saver", "Otimização: Battery Saver",
            () -> NebulaConfig.enableBatterySaver, v -> NebulaConfig.enableBatterySaver = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Mobile Power Saving", "Otimização: Mobile Power Saving",
            () -> NebulaConfig.enableMobilePowerSaving, v -> NebulaConfig.enableMobilePowerSaving = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Mobile Touch Controls", "Otimização: Mobile Touch Controls",
            () -> NebulaConfig.enableMobileTouchControls, v -> NebulaConfig.enableMobileTouchControls = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Screen Dimmer", "Otimização: Screen Dimmer",
            () -> NebulaConfig.enableScreenDimmer, v -> NebulaConfig.enableScreenDimmer = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Smart Reboot", "Otimização: Smart Reboot",
            () -> NebulaConfig.enableSmartReboot, v -> NebulaConfig.enableSmartReboot = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Smart Sleep", "Otimização: Smart Sleep",
            () -> NebulaConfig.enableSmartSleep, v -> NebulaConfig.enableSmartSleep = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Smart Ventilation", "Otimização: Smart Ventilation",
            () -> NebulaConfig.enableSmartVentilation, v -> NebulaConfig.enableSmartVentilation = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Thermal Monitor", "Otimização: Thermal Monitor",
            () -> NebulaConfig.enableThermalMonitor, v -> NebulaConfig.enableThermalMonitor = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Thermal Protection", "Otimização: Thermal Protection",
            () -> NebulaConfig.enableThermalProtection, v -> NebulaConfig.enableThermalProtection = v)));
        mobilebatter.settings.add(gated(nebulaToggle("Touch Optimization", "Otimização: Touch Optimization",
            () -> NebulaConfig.enableTouchOptimization, v -> NebulaConfig.enableTouchOptimization = v)));
        categories.add(mobilebatter);

        // ===== AUTOMATION =====
        Category automation = new Category("Automation", "🤖");
        automation.settings.add(gated(nebulaToggle("Auto Boost", "Otimização: Auto Boost",
            () -> NebulaConfig.enableAutoBoost, v -> NebulaConfig.enableAutoBoost = v)).group());
        automation.settings.add(gated(nebulaToggle("Auto Optimizer", "Otimização: Auto Optimizer",
            () -> NebulaConfig.enableAutoOptimizer, v -> NebulaConfig.enableAutoOptimizer = v)));
        automation.settings.add(gated(nebulaToggle("Auto Target", "Otimização: Auto Target",
            () -> NebulaConfig.enableAutoTarget, v -> NebulaConfig.enableAutoTarget = v)));
        automation.settings.add(gated(nebulaToggle("Auto Tuner", "Otimização: Auto Tuner",
            () -> NebulaConfig.enableAutoTuner, v -> NebulaConfig.enableAutoTuner = v)));
        automation.settings.add(gated(nebulaToggle("Combat Mode", "Otimização: Combat Mode",
            () -> NebulaConfig.enableCombatMode, v -> NebulaConfig.enableCombatMode = v)));
        automation.settings.add(gated(nebulaToggle("Day Night Optimization", "Otimização: Day Night Optimization",
            () -> NebulaConfig.enableDayNightOptimization, v -> NebulaConfig.enableDayNightOptimization = v)));
        automation.settings.add(gated(nebulaToggle("Dimension Optimizer", "Otimização: Dimension Optimizer",
            () -> NebulaConfig.enableDimensionOptimizer, v -> NebulaConfig.enableDimensionOptimizer = v)));
        automation.settings.add(gated(nebulaToggle("Dynamic Music", "Otimização: Dynamic Music",
            () -> NebulaConfig.enableDynamicMusic, v -> NebulaConfig.enableDynamicMusic = v)));
        automation.settings.add(gated(nebulaToggle("FPS Boost", "Otimização: FPS Boost",
            () -> NebulaConfig.enableFPSBoost, v -> NebulaConfig.enableFPSBoost = v)));
        automation.settings.add(gated(nebulaToggle("Game Mode Detection", "Otimização: Game Mode Detection",
            () -> NebulaConfig.enableGameModeDetection, v -> NebulaConfig.enableGameModeDetection = v)));
        automation.settings.add(gated(nebulaToggle("Game Mode Profiles", "Otimização: Game Mode Profiles",
            () -> NebulaConfig.enableGameModeProfiles, v -> NebulaConfig.enableGameModeProfiles = v)));

        // ===== WORLD & NETWORK =====
        Category worldnetwork = new Category("World & Network", "🌐");
        worldnetwork.settings.add(gated(nebulaToggle("Anti Ghost Block", "Otimização: Anti Ghost Block",
            () -> NebulaConfig.enableAntiGhostBlock, v -> NebulaConfig.enableAntiGhostBlock = v)).group());
        worldnetwork.settings.add(gated(nebulaToggle("Chunk Loader Optimizer", "Otimização: Chunk Loader Optimizer",
            () -> NebulaConfig.enableChunkLoaderOptimizer, v -> NebulaConfig.enableChunkLoaderOptimizer = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Dynamic Chunk Loading", "Otimização: Dynamic Chunk Loading",
            () -> NebulaConfig.enableDynamicChunkLoading, v -> NebulaConfig.enableDynamicChunkLoading = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Faster Block Breaking", "Otimização: Faster Block Breaking",
            () -> NebulaConfig.enableFasterBlockBreaking, v -> NebulaConfig.enableFasterBlockBreaking = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Launch Acceleration", "Otimização: Launch Acceleration",
            () -> NebulaConfig.enableLaunchAcceleration, v -> NebulaConfig.enableLaunchAcceleration = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Light Updates Optimizer", "Otimização: Light Updates Optimizer",
            () -> NebulaConfig.enableLightUpdatesOptimizer, v -> NebulaConfig.enableLightUpdatesOptimizer = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Lighting Optimization", "Otimização: Lighting Optimization",
            () -> NebulaConfig.enableLightingOptimization, v -> NebulaConfig.enableLightingOptimization = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Mod Compatibility", "Otimização: Mod Compatibility",
            () -> NebulaConfig.enableModCompatibility, v -> NebulaConfig.enableModCompatibility = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Network Optimization", "Otimização: Network Optimization",
            () -> NebulaConfig.enableNetworkOptimization, v -> NebulaConfig.enableNetworkOptimization = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Reduced Collisions", "Otimização: Reduced Collisions",
            () -> NebulaConfig.enableReducedCollisions, v -> NebulaConfig.enableReducedCollisions = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Smart Chunk Loading", "Otimização: Smart Chunk Loading",
            () -> NebulaConfig.enableSmartChunkLoading, v -> NebulaConfig.enableSmartChunkLoading = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Smooth World Loading", "Otimização: Smooth World Loading",
            () -> NebulaConfig.enableSmoothWorldLoading, v -> NebulaConfig.enableSmoothWorldLoading = v)));
        worldnetwork.settings.add(gated(nebulaToggle("Water Optimization", "Otimização: Water Optimization",
            () -> NebulaConfig.enableWaterOptimization, v -> NebulaConfig.enableWaterOptimization = v)));
        categories.add(worldnetwork);

        // ===== RENDERING & MEMORY =====
        Category renderingmem = new Category("Rendering & Memory", "🧩");
        renderingmem.settings.add(gated(nebulaToggle("Audio Optimization", "Otimização: Audio Optimization",
            () -> NebulaConfig.enableAudioOptimization, v -> NebulaConfig.enableAudioOptimization = v)).group());
        renderingmem.settings.add(gated(nebulaToggle("Dynamic Resolution", "Otimização: Dynamic Resolution",
            () -> NebulaConfig.enableDynamicResolution, v -> NebulaConfig.enableDynamicResolution = v)));
        renderingmem.settings.add(gated(nebulaToggle("Dynamic Shaders", "Otimização: Dynamic Shaders",
            () -> NebulaConfig.enableDynamicShaders, v -> NebulaConfig.enableDynamicShaders = v)));
        renderingmem.settings.add(gated(nebulaToggle("Garbage Collection", "Otimização: Garbage Collection",
            () -> NebulaConfig.enableGarbageCollection, v -> NebulaConfig.enableGarbageCollection = v)));
        renderingmem.settings.add(gated(nebulaToggle("Intelligent Ticking", "Otimização: Intelligent Ticking",
            () -> NebulaConfig.enableIntelligentTicking, v -> NebulaConfig.enableIntelligentTicking = v)));
        renderingmem.settings.add(gated(nebulaToggle("Logic Optimization", "Otimização: Logic Optimization",
            () -> NebulaConfig.enableLogicOptimization, v -> NebulaConfig.enableLogicOptimization = v)));
        renderingmem.settings.add(gated(nebulaToggle("Memory Management", "Otimização: Memory Management",
            () -> NebulaConfig.enableMemoryManagement, v -> NebulaConfig.enableMemoryManagement = v)));
        renderingmem.settings.add(gated(nebulaToggle("Night Vision Optimizer", "Otimização: Night Vision Optimizer",
            () -> NebulaConfig.enableNightVisionOptimizer, v -> NebulaConfig.enableNightVisionOptimizer = v)));
        renderingmem.settings.add(gated(nebulaToggle("Particle Killer", "Otimização: Particle Killer",
            () -> NebulaConfig.enableParticleKiller, v -> NebulaConfig.enableParticleKiller = v)));
        renderingmem.settings.add(gated(nebulaToggle("State Based Rendering", "Otimização: State Based Rendering",
            () -> NebulaConfig.enableStateBasedRendering, v -> NebulaConfig.enableStateBasedRendering = v)));
        renderingmem.settings.add(gated(nebulaToggle("State Compression", "Otimização: State Compression",
            () -> NebulaConfig.enableStateCompression, v -> NebulaConfig.enableStateCompression = v)));
        renderingmem.settings.add(gated(nebulaToggle("Texture Streaming", "Otimização: Texture Streaming",
            () -> NebulaConfig.enableTextureStreaming, v -> NebulaConfig.enableTextureStreaming = v)));
        renderingmem.settings.add(gated(nebulaToggle("Texture Upscaling", "Otimização: Texture Upscaling",
            () -> NebulaConfig.enableTextureUpscaling, v -> NebulaConfig.enableTextureUpscaling = v)));
        renderingmem.settings.add(gated(nebulaToggle("Thread Pool", "Otimização: Thread Pool",
            () -> NebulaConfig.enableThreadPool, v -> NebulaConfig.enableThreadPool = v)));
        categories.add(renderingmem);

                // ===== NEBULA EXTRA =====
        Category extra = new Category("Nebula Extra", "✨");
        extra.settings.add(nebulaToggle("Remove All Particles", "Cancela o spawn de todas as particulas",
            () -> NebulaConfig.enableRemoveAllParticles, v -> NebulaConfig.enableRemoveAllParticles = v));
        extra.settings.add(nebulaToggle("Remove Water Particles", "Cancela bolhas e gotas de agua",
            () -> NebulaConfig.enableRemoveWaterParticles, v -> NebulaConfig.enableRemoveWaterParticles = v));
        extra.settings.add(nebulaToggle("Remove Explosion Particles", "Cancela fumaca de tnt e explosões",
            () -> NebulaConfig.enableRemoveExplosionParticles, v -> NebulaConfig.enableRemoveExplosionParticles = v));
        extra.settings.add(nebulaToggle("Remove Entity Animations", "Congela todas as animacoes de mobs",
            () -> NebulaConfig.enableRemoveEntityAnimations, v -> NebulaConfig.enableRemoveEntityAnimations = v));
        categories.add(extra);
        extra.settings.add(nebulaToggle("No Weather", "Remove a chuva e neve para salvar FPS",
            () -> NebulaConfig.enableNoWeather, v -> NebulaConfig.enableNoWeather = v));
        extra.settings.add(nebulaToggle("No Distance Fog", "Remove a neblina do final dos chunks",
            () -> NebulaConfig.enableNoDistanceFog, v -> NebulaConfig.enableNoDistanceFog = v));
        extra.settings.add(nebulaToggle("Fullbright (Lanterna)", "Visao noturna total (Aperte G para ligar/desligar)",
            () -> NebulaConfig.enableFullbright, v -> NebulaConfig.enableFullbright = v));
        // extra.settings.add(nebulaToggle("Toggle Sneak (PC)", "Agachar com um clique (Apenas PC)",
        //     () -> NebulaConfig.enableToggleSneak, v -> NebulaConfig.enableToggleSneak = v));

    }

    /**
     * Sistema de "staging": nada muda de verdade até clicar em Apply/Done.
     * Cada fábrica de Setting registra aqui como confirmar (commitActions)
     * e como voltar ao padrão (resetActions) o valor pendente dela.
     */
    private final List<Runnable> commitActions = new ArrayList<>();
    private final List<Runnable> resetActions = new ArrayList<>();
    private boolean hasPendingChanges = false;

    private Setting toggle(String name, String desc, java.util.function.BooleanSupplier getter,
                            Consumer<Boolean> setter, boolean defaultValue) {
        boolean[] staged = new boolean[1];
        boolean[] hasStaged = new boolean[1];

        java.util.function.BooleanSupplier effective = () -> hasStaged[0] ? staged[0] : getter.getAsBoolean();
        Supplier<String> display = () -> effective.getAsBoolean() ? "ON" : "OFF";
        Runnable onClick = () -> {
            staged[0] = !effective.getAsBoolean();
            hasStaged[0] = true;
            hasPendingChanges = true;
        };

        commitActions.add(() -> {
            if (hasStaged[0]) {
                setter.accept(staged[0]);
                hasStaged[0] = false;
            }
        });
        resetActions.add(() -> {
            staged[0] = defaultValue;
            hasStaged[0] = true;
            hasPendingChanges = true;
        });

        return new Setting(name, desc, Setting.SettingType.TOGGLE, display, onClick, onClick, onClick);
    }

    /** Versão genérica de CYCLE pra qualquer enum (Graphics, Particles, Clouds...), também com staging. */
    private <T> Setting cycleGeneric(String name, String desc, Supplier<T> getter, Consumer<T> setter,
                                      T defaultValue, java.util.function.UnaryOperator<T> prev,
                                      java.util.function.UnaryOperator<T> next,
                                      java.util.function.Function<T, String> formatter) {
        Object[] staged = new Object[1];

        @SuppressWarnings("unchecked")
        Supplier<T> effective = () -> staged[0] != null ? (T) staged[0] : getter.get();
        Supplier<String> display = () -> formatter.apply(effective.get());

        Runnable onLeft = () -> {
            staged[0] = prev.apply(effective.get());
            hasPendingChanges = true;
        };
        Runnable onRight = () -> {
            staged[0] = next.apply(effective.get());
            hasPendingChanges = true;
        };

        commitActions.add(() -> {
            if (staged[0] != null) {
                @SuppressWarnings("unchecked")
                T v = (T) staged[0];
                setter.accept(v);
                staged[0] = null;
            }
        });
        resetActions.add(() -> {
            staged[0] = defaultValue;
            hasPendingChanges = true;
        });

        return new Setting(name, desc, Setting.SettingType.CYCLE, display, onRight, onLeft, onRight);
    }

    /**
     * Slider numérico de verdade (estilo vanilla): barra do tamanho da
     * linha inteira, arrasta ou clica em qualquer ponto pra pular direto
     * pro valor. Também com staging — só vale de verdade no Apply/Done.
     */
    private Setting numericSlider(String name, String desc, java.util.function.IntSupplier getter,
                                   java.util.function.IntConsumer setter, int min, int max, int step,
                                   int defaultValue, java.util.function.IntFunction<String> formatter) {
        int[] staged = new int[]{Integer.MIN_VALUE};

        java.util.function.IntSupplier effective = () -> staged[0] != Integer.MIN_VALUE ? staged[0] : getter.getAsInt();
        Supplier<String> display = () -> formatter.apply(effective.getAsInt());

        Runnable stepDown = () -> {
            staged[0] = Math.max(min, effective.getAsInt() - step);
            hasPendingChanges = true;
        };
        Runnable stepUp = () -> {
            staged[0] = Math.min(max, effective.getAsInt() + step);
            hasPendingChanges = true;
        };
        java.util.function.IntConsumer dragSet = (v) -> {
            staged[0] = Math.max(min, Math.min(max, v));
            hasPendingChanges = true;
        };

        commitActions.add(() -> {
            if (staged[0] != Integer.MIN_VALUE) {
                setter.accept(staged[0]);
                staged[0] = Integer.MIN_VALUE;
            }
        });
        resetActions.add(() -> {
            staged[0] = defaultValue;
            hasPendingChanges = true;
        });

        return new Setting(name, desc, Setting.SettingType.SLIDER, display, stepUp, stepDown, stepUp,
                effective, dragSet, min, max);
    }

    private Setting nebulaToggle(String name, String desc, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        boolean[] staged = new boolean[1];
        boolean[] hasStaged = new boolean[1];
        boolean defaultValue = getter.get();

        Supplier<Boolean> effective = () -> hasStaged[0] ? staged[0] : getter.get();
        Supplier<String> display = () -> effective.get() ? "§aON" : "§cOFF";
        Runnable onClick = () -> {
            staged[0] = !effective.get();
            hasStaged[0] = true;
            hasPendingChanges = true;
        };

        commitActions.add(() -> {
            if (hasStaged[0]) {
                setter.accept(staged[0]);
                hasStaged[0] = false;
            }
        });
        resetActions.add(() -> {
            staged[0] = defaultValue;
            hasStaged[0] = true;
            hasPendingChanges = true;
        });

        return new Setting(name, desc, Setting.SettingType.TOGGLE, display, onClick, onClick, onClick);
    }

    /** Aplica de vez todas as mudanças pendentes (chamado por Apply e Done). */
    private void applyPendingChanges() {
        for (Runnable action : commitActions) {
            action.run();
        }
        options.write();
        NebulaConfig.saveConfig();
        hasPendingChanges = false;
    }

    private Setting gated(Setting s) {
        s.nebulaGated = true;
        return s;
    }

    /** Lista de fato visível agora: filtrada (busca, em todas categorias) ou a categoria atual. */
    private List<Setting> visibleSettings() {
        if (searchQuery.isEmpty()) {
            return categories.get(selectedCategory).settings;
        }

        List<Setting> results = new ArrayList<>();
        for (Category cat : categories) {
            for (Setting s : cat.settings) {
                if (s.name.toLowerCase().contains(searchQuery)) {
                    s.categoryLabel = cat.name;
                    results.add(s);
                }
            }
        }
        return results;
    }

    private boolean inSearchMode() {
        return !searchQuery.isEmpty();
    }

    private int sidebarMaxVisible() {
        int available = (height - BOTTOM_HEIGHT) - (HEADER_HEIGHT + 8);
        return Math.max(1, available / 30);
    }

        protected void init() {
        super.init();

        int centerX = width / 2;
        int rowBottomY = height - 24; // Done | Reset All | Apply
        int rowTopY = height - 48;    // Shaders...

        int w1 = 80;
        int gap = 6;
        int groupWidth = w1 * 3 + gap * 2;
        int groupStart = centerX - groupWidth / 2;

        // 1. Cria os botões PRIMEIRO
        doneButton = ButtonWidget.builder(Text.literal("Done"), b -> {
            applyPendingChanges();
            this.client.setScreen(parent);
        }).dimensions(groupStart, rowBottomY, w1, 20).build();

        resetButton = ButtonWidget.builder(Text.literal("Reset All"), b -> resetAllSettings())
            .dimensions(groupStart + w1 + gap, rowBottomY, w1, 20).build();

        applyButton = ButtonWidget.builder(Text.literal("Apply"), b -> applyPendingChanges())
            .dimensions(groupStart + (w1 + gap) * 2, rowBottomY, w1, 20).build();

        shadersButton = ButtonWidget.builder(Text.literal("Shaders..."), b -> {
            if (this.client != null) {
                this.client.setScreen(new ShaderMenuScreen(this));
            }
        }).dimensions(width - 200, 7, 90, 20).build();

        closeButton = ButtonWidget.builder(Text.literal("✕"), b -> {
            applyPendingChanges();
            this.client.setScreen(parent);
        }).dimensions(width - 30, 7, 22, 20).build();

        themeButton = ButtonWidget.builder(
            Text.literal("🎨"),
            b -> {
                NebulaConfig.uiTheme = (NebulaConfig.uiTheme + 1) % 2;
                NebulaConfig.saveConfig();
            }
        ).dimensions(8, rowTopY, 24, 20).tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
            Text.literal("Tema: " + themeName(NebulaConfig.uiTheme) + " (clique pra alternar)")
        )).build();

        // 2. Adiciona os botões na tela DEPOIS de criados
        this.addDrawableChild(doneButton);
        this.addDrawableChild(resetButton);
        this.addDrawableChild(applyButton);
        this.addDrawableChild(shadersButton);
        this.addDrawableChild(closeButton);
        this.addDrawableChild(themeButton);

        // Barra de busca
        searchField = new TextFieldWidget(textRenderer, 8, 7, width - 300, 20, Text.literal("Search"));
        searchField.setMaxLength(64);
        searchField.setSuggestion("🔍 Search options...");
        searchField.setChangedListener(s -> {
            searchQuery = s.toLowerCase();
            scrollOffset = 0;
        });
        this.addDrawableChild(searchField);
    }

    private void resetAllSettings() {
        // Cada setting já registrou seu próprio valor padrão (via
        // toggle/cycleGeneric/numericSlider/nebulaToggle) — só precisa
        // "encostar" nesses valores. Ainda precisa de Apply/Done pra valer
        // de verdade, igual qualquer outra mudança.
        for (Runnable action : resetActions) {
            action.run();
        }
        System.out.println("[Nebula] Valores padrão prontos — clique em Apply ou Done pra confirmar.");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        applyTheme();

        // Fundo
        context.fill(0, 0, width, height, BG);
        context.fill(0, 0, width, HEADER_HEIGHT, HEADER_BG);
        if (!inSearchMode()) {
            context.fill(0, HEADER_HEIGHT, SIDEBAR_WIDTH, height - BOTTOM_HEIGHT, SIDEBAR_BG);
        }
        context.fill(0, height - BOTTOM_HEIGHT, width, height, HEADER_BG);

        int contentStartX = inSearchMode() ? 8 : SIDEBAR_WIDTH + 10;

        // Sidebar (escondida durante a busca, igual ao Sodium)
        if (!inSearchMode()) {
            int sidebarMax = sidebarMaxVisible();
            int sidebarMaxScroll = Math.max(0, categories.size() - sidebarMax);
            if (sidebarScrollOffset > sidebarMaxScroll) sidebarScrollOffset = sidebarMaxScroll;

            int y = HEADER_HEIGHT + 8;
            int end = Math.min(sidebarScrollOffset + sidebarMax, categories.size());
            for (int i = sidebarScrollOffset; i < end; i++) {
                Category cat = categories.get(i);
                boolean selected = i == selectedCategory;
                boolean hovered = mouseX < SIDEBAR_WIDTH && mouseY > y && mouseY < y + 26;

                if (selected) {
                    context.fill(0, y, 3, y + 26, ACCENT);
                    context.fill(3, y, SIDEBAR_WIDTH, y + 26, new Color(255, 170, 0, 20).getRGB());
                } else if (hovered) {
                    context.fill(0, y, SIDEBAR_WIDTH, y + 26, new Color(255, 255, 255, 10).getRGB());
                }

                context.drawText(textRenderer, cat.icon + " " + cat.name, 12, y + 8, selected ? ACCENT : MUTED, false);
                y += 30;
            }

            // Indicador de que tem mais categoria pra baixo/cima (rola com scroll ou arrastando)
            if (sidebarScrollOffset > 0) {
                context.drawText(textRenderer, "▲", SIDEBAR_WIDTH - 16, HEADER_HEIGHT - 2, MUTED, false);
            }
            if (sidebarScrollOffset < sidebarMaxScroll) {
                context.drawText(textRenderer, "▼ mais", 10, height - BOTTOM_HEIGHT - 10, MUTED, false);
            }
        }

        // Settings
        List<Setting> visible = visibleSettings();
        int listTop = HEADER_HEIGHT + 10;
        int listBottom = height - BOTTOM_HEIGHT - 8;
        int maxVisible = Math.max(1, (listBottom - listTop) / ROW_HEIGHT);

        if (scrollOffset > Math.max(0, visible.size() - maxVisible)) {
            scrollOffset = Math.max(0, visible.size() - maxVisible);
        }

        if (visible.isEmpty() && inSearchMode()) {
            context.drawText(textRenderer, "Nenhuma opção encontrada para \"" + searchQuery + "\"",
                contentStartX, listTop + 4, MUTED, false);
        }

        int rowY = listTop;
        for (int i = scrollOffset; i < Math.min(scrollOffset + maxVisible, visible.size()); i++) {
            Setting s = visible.get(i);
            boolean disabled = s.nebulaGated && !NebulaConfig.enableNebula;
            boolean hovered = !disabled && mouseX > contentStartX && mouseX < width - 10
                    && mouseY > rowY && mouseY < rowY + ROW_HEIGHT - 2;

            if (s.groupBreak && i > scrollOffset) {
                context.fill(contentStartX, rowY, width - 10, rowY + 1, new Color(255, 255, 255, 18).getRGB());
                rowY += 5;
            }

            if (s.type == Setting.SettingType.SLIDER) {
                // Barra do tamanho da linha inteira, estilo botão do vanilla
                // (arrasta ou clica em qualquer ponto pra pular pro valor).
                                int barX = contentStartX - 4;
                int barW = (width - 10) - barX;
                int barY = rowY;
                int barH = ROW_HEIGHT - 4;

                int val = s.dragGet.getAsInt();
                float progress = (s.dragMax > s.dragMin)
                        ? (val - s.dragMin) / (float) (s.dragMax - s.dragMin) : 0f;
                progress = Math.max(0f, Math.min(1f, progress));
                int filledW = (int) (barW * progress);

                // Trilho da barra (fundo)
                context.fill(barX, barY + (barH/2) - 3, barX + barW, barY + (barH/2) + 3, new Color(255, 255, 255, 20).getRGB());
                
                // Parte preenchida (Accent)
                context.fill(barX, barY + (barH/2) - 3, barX + filledW, barY + (barH/2) + 3, disabled ? DISABLED : ACCENT);
                
                // "Knob" (Bolinha branca no meio da barra preenchida) - Estilo Sodium
                if (!disabled) {
                    int knobX = barX + filledW - 2;
                    context.fill(knobX, barY + (barH/2) - 5, knobX + 4, barY + (barH/2) + 5, new Color(255, 255, 255, 230).getRGB());
                }

                if (hovered && !disabled) {
                    context.fill(barX, barY, barX + barW, barY + barH, new Color(255, 255, 255, 8).getRGB());
                }

                String text = s.name + ": " + s.display.get();
                int tw = textRenderer.getWidth(text);
                context.drawText(textRenderer, text, barX + (barW - tw) / 2, barY + (barH - 8) / 2,
                        disabled ? DISABLED : TEXT, false);

                rowY += ROW_HEIGHT;
                continue;
            }

            if (hovered) {
                context.fill(contentStartX - 4, rowY, width - 10, rowY + ROW_HEIGHT - 2, new Color(255, 255, 255, 12).getRGB());
            }

            String label = s.categoryLabel != null ? "§8[" + s.categoryLabel + "]§r " + s.name : s.name;
            context.drawText(textRenderer, label, contentStartX, rowY + 8, disabled ? DISABLED : TEXT, false);

            if (s.type == Setting.SettingType.TOGGLE) {
                boolean value = s.display.get().contains("ON");
                drawCheckbox(context, width - 30, rowY + 6, value, disabled);
            } else {
                String value = s.display.get();
                int vw = textRenderer.getWidth(value);
                context.drawText(textRenderer, value, width - 20 - vw, rowY + 8, disabled ? DISABLED : MUTED, false);
            }

            rowY += ROW_HEIGHT;
        }

        // Descrição da opção sob o mouse
        int idx = scrollOffset + (mouseY - listTop) / ROW_HEIGHT;
        if (mouseX > contentStartX && idx >= 0 && idx < visible.size() && mouseY > listTop && mouseY < listBottom) {
            context.drawText(textRenderer, visible.get(idx).description, contentStartX, height - 38, MUTED, false);
        } else if (inSearchMode()) {
            context.drawText(textRenderer, "🔍 Buscando em todas as categorias — " + visible.size() + " resultado(s)",
                contentStartX, height - 38, MUTED, false);
        }

                super.render(context, mouseX, mouseY, delta);

        // Desenha o visual customizado por cima dos botões padrão do Minecraft
        drawCustomButton(context, doneButton, mouseX, mouseY);
        drawCustomButton(context, resetButton, mouseX, mouseY);
        drawCustomButton(context, applyButton, mouseX, mouseY);
        drawCustomButton(context, shadersButton, mouseX, mouseY);
        drawCustomButton(context, closeButton, mouseX, mouseY);
        drawCustomButton(context, themeButton, mouseX, mouseY);
    }

        /** Checkbox quadrado moderno e limpo (estilo Sodium) */
    private void drawCheckbox(DrawContext context, int x, int y, boolean checked, boolean disabled) {
        int size = 14;
        int borderColor = disabled ? DISABLED : (checked ? ACCENT : MUTED);
        
        // Fundo levemente escurecido
        context.fill(x, y, x + size, y + size, new Color(0, 0, 0, 80).getRGB());
        
        // Borda de 1px
        context.fill(x, y, x + size, y + 1, borderColor);
        context.fill(x, y + size - 1, x + size, y + size, borderColor);
        context.fill(x, y, x + 1, y + size, borderColor);
        context.fill(x + size - 1, y, x + size, y + size, borderColor);

        // Preenchimento interno quando ligado
        if (checked) {
            // Deixa um respiro de 3px para parecer um checkbox moderno e não um bloco sólido
            context.fill(x + 3, y + 3, x + size - 3, y + size - 3, disabled ? DISABLED : ACCENT);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Botões de verdade (Done/Reset/Apply/Shaders/✕/Tema/busca) sempre primeiro.
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        dragCommitted = DRAG_NONE;
        pointerStartX = mouseX;
        pointerStartY = mouseY;
        pointerButton = button;
        pointerSetting = null;
        pointerCategoryIndex = -1;
        pointerIsSidebar = false;

        // Sidebar (não existe durante a busca)
        if (!inSearchMode() && mouseX < SIDEBAR_WIDTH && mouseY > HEADER_HEIGHT && mouseY < height - BOTTOM_HEIGHT) {
            pointerIsSidebar = true;
            int idx = sidebarScrollOffset + (int) ((mouseY - HEADER_HEIGHT - 8) / 30);
            if (idx >= 0 && idx < categories.size()) {
                pointerCategoryIndex = idx;
            }
            return true;
        }

        int contentStartX = inSearchMode() ? 8 : SIDEBAR_WIDTH + 10;

        if (mouseX > contentStartX) {
            List<Setting> visible = visibleSettings();
            int listTop = HEADER_HEIGHT + 10;
            int listBottom = height - BOTTOM_HEIGHT - 8;

            if (mouseY > listTop && mouseY < listBottom) {
                int idx = scrollOffset + (int) ((mouseY - listTop) / ROW_HEIGHT);
                if (idx >= 0 && idx < visible.size()) {
                    pointerSetting = visible.get(idx);
                    boolean disabled = pointerSetting.nebulaGated && !NebulaConfig.enableNebula;

                    if (!disabled && pointerSetting.type == Setting.SettingType.SLIDER) {
                        // Estilo vanilla: clicar em qualquer ponto da barra
                        // já pula pro valor daquele ponto, e o arraste
                        // começa imediatamente (sem esperar o dedo se mover).
                        int barX = contentStartX - 4;
                        int barW = (width - 10) - barX;
                        double progress = (mouseX - barX) / (double) barW;
                        progress = Math.max(0.0, Math.min(1.0, progress));
                        int newValue = pointerSetting.dragMin
                                + (int) Math.round(progress * (pointerSetting.dragMax - pointerSetting.dragMin));
                        pointerSetting.dragSet.accept(newValue);
                        dragCommitted = DRAG_NUMERIC;
                        dragAnchorValue = newValue;
                    } else if (pointerSetting.isDraggable()) {
                        dragAnchorValue = pointerSetting.dragGet.getAsInt();
                    }
                }
                // Sempre retorna true aqui (mesmo se não caiu em nenhuma
                // opção específica, tipo um espacinho entre grupos) — assim
                // dá pra começar um arrasto de rolagem de qualquer ponto da
                // lista, não só onde tem uma opção.
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double totalDx = mouseX - pointerStartX;
        double totalDy = mouseY - pointerStartY;

        if (dragCommitted == DRAG_NONE) {
            if (pointerSetting != null && pointerSetting.isDraggable()
                    && Math.abs(totalDx) > 4 && Math.abs(totalDx) > Math.abs(totalDy)) {
                dragCommitted = DRAG_NUMERIC;
            } else if (Math.abs(totalDy) > 4 && Math.abs(totalDy) > Math.abs(totalDx)) {
                // Qualquer arrasto vertical de tamanho razoável vira rolagem
                // — funciona em cima de uma opção, de um espaço vazio, ou
                // da sidebar.
                dragCommitted = DRAG_SCROLL;
                dragScrollAccum = 0;
            }
        }

        if (dragCommitted == DRAG_NUMERIC && pointerSetting != null && pointerSetting.isDraggable()) {
            if (pointerSetting.type == Setting.SettingType.SLIDER) {
                // Mesma matemática de posição absoluta do clique inicial —
                // arrastar continua acompanhando o dedo/cursor na barra
                // inteira, não um incremento relativo pequeno.
                int contentStartX = inSearchMode() ? 8 : SIDEBAR_WIDTH + 10;
                int barX = contentStartX - 4;
                int barW = (width - 10) - barX;
                double progress = (mouseX - barX) / (double) barW;
                progress = Math.max(0.0, Math.min(1.0, progress));
                int newValue = pointerSetting.dragMin
                        + (int) Math.round(progress * (pointerSetting.dragMax - pointerSetting.dragMin));
                pointerSetting.dragSet.accept(newValue);
            } else {
                int delta = (int) (totalDx / 4);
                int newValue = Math.max(pointerSetting.dragMin, Math.min(pointerSetting.dragMax, dragAnchorValue + delta));
                pointerSetting.dragSet.accept(newValue);
            }
            return true;
        }

        if (dragCommitted == DRAG_SCROLL) {
            if (pointerIsSidebar) {
                int sidebarMax = sidebarMaxVisible();
                int sidebarMaxScroll = Math.max(0, categories.size() - sidebarMax);
                dragScrollAccum += deltaY;
                while (dragScrollAccum <= -30) {
                    sidebarScrollOffset = Math.min(sidebarMaxScroll, sidebarScrollOffset + 1);
                    dragScrollAccum += 30;
                }
                while (dragScrollAccum >= 30) {
                    sidebarScrollOffset = Math.max(0, sidebarScrollOffset - 1);
                    dragScrollAccum -= 30;
                }
            } else {
                List<Setting> visible = visibleSettings();
                int maxVisible = Math.max(1, (height - HEADER_HEIGHT - BOTTOM_HEIGHT - 18) / ROW_HEIGHT);
                int maxScroll = Math.max(0, visible.size() - maxVisible);

                dragScrollAccum += deltaY;
                while (dragScrollAccum <= -ROW_HEIGHT) {
                    scrollOffset = Math.min(maxScroll, scrollOffset + 1);
                    dragScrollAccum += ROW_HEIGHT;
                }
                while (dragScrollAccum >= ROW_HEIGHT) {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                    dragScrollAccum -= ROW_HEIGHT;
                }
            }
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

        @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Se nunca "comprometeu" com rolagem nem ajuste numérico, foi um
        // toque rápido — trata como clique normal.
        if (dragCommitted == DRAG_NONE) {
            if (pointerIsSidebar && pointerCategoryIndex >= 0) {
                selectedCategory = pointerCategoryIndex;
                scrollOffset = 0;
            } else if (pointerSetting != null) {
                boolean disabled = pointerSetting.nebulaGated && !NebulaConfig.enableNebula;
                if (!disabled) {
                    if (pointerButton == 0) pointerSetting.onClick.run();
                    else if (pointerButton == 1) pointerSetting.onLeft.run();
                }
            }
        }

        dragCommitted = DRAG_NONE;
        pointerSetting = null;
        pointerCategoryIndex = -1;
        
        // FIX: Não chamamos super.mouseReleased() para evitar NullPointer do motor gráfico do celular.
        // Apenas avisamos a tela que o arraste parou.
        this.setDragging(false);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!inSearchMode() && mouseX < SIDEBAR_WIDTH) {
            int sidebarMax = sidebarMaxVisible();
            int sidebarMaxScroll = Math.max(0, categories.size() - sidebarMax);
            sidebarScrollOffset = (int) Math.max(0, Math.min(sidebarMaxScroll, sidebarScrollOffset - amount));
            return true;
        }

        List<Setting> visible = visibleSettings();
        int maxVisible = Math.max(1, (height - HEADER_HEIGHT - BOTTOM_HEIGHT - 18) / ROW_HEIGHT);
        int maxScroll = Math.max(0, visible.size() - maxVisible);
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - amount));
        return true;
    }

    @Override
    public void close() {
        applyPendingChanges();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

        /** Desenha um fundo moderno e limpo sobre os botões padrão do Minecraft */
    private void drawCustomButton(DrawContext context, ButtonWidget button, int mouseX, int mouseY) {
        if (button == null || !button.visible) return;
        
        boolean hovered = mouseX >= button.getX() && mouseY >= button.getY() 
                       && mouseX < button.getX() + button.getWidth() 
                       && mouseY < button.getY() + button.getHeight();
        boolean active = button.active;

        // Cores baseadas no tema
        int bgColor = active ? (hovered ? new Color(45, 45, 50, 255).getRGB() : new Color(30, 30, 35, 255).getRGB()) : new Color(20, 20, 23, 255).getRGB();
        int borderColor = active ? (hovered ? ACCENT : MUTED) : DISABLED;
        int textColor = active ? (hovered ? ACCENT : TEXT) : DISABLED;

        // Fundo
        context.fill(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), bgColor);
        
        // Bordas de 1px (Estilo Sodium)
        context.fill(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + 1, borderColor);
        context.fill(button.getX(), button.getY() + button.getHeight() - 1, button.getX() + button.getWidth(), button.getY() + button.getHeight(), borderColor);
        context.fill(button.getX(), button.getY(), button.getX() + 1, button.getY() + button.getHeight(), borderColor);
        context.fill(button.getX() + button.getWidth() - 1, button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), borderColor);

        // Texto centralizado
        String text = button.getMessage().getString();
        int textWidth = textRenderer.getWidth(text);
        context.drawText(textRenderer, text, button.getX() + (button.getWidth() - textWidth) / 2, button.getY() + (button.getHeight() - 8) / 2 + 1, textColor, false);
    }
}
