package com.nebula;

import java.io.*;
import java.util.Properties;

public class NebulaConfig {
    private static final String CONFIG_FILE = "config/nebula.properties";
    private static Properties properties = new Properties();
    private static boolean loaded = false;
    
    // ============================================================
    // CONFIGURAÇÕES EXISTENTES
    // ============================================================
    public static boolean enableNebula = true;
    public static boolean enableEntityCulling = true;
    public static boolean enableAngleCulling = true;
    public static int entityRenderDistance = 24;
    public static boolean enableAggressiveCulling = true;
    
    public static boolean enableBlockCulling = true;
    public static int blockCullingDistance = 12;
    public static boolean enableVerticalCulling = true;
    
    public static boolean enableParticleLimit = true;
    public static int maxParticles = 200;
    
    public static boolean enableDynamicResolution = true;
    public static int minResolutionPercent = 60;
    public static int maxResolutionPercent = 100;
    public static int targetFps = 60;
    
    public static boolean enableTextureStreaming = true;
    public static int textureStreamingRadius = 30;
    
    public static boolean enableIntelligentTicking = true;
    public static int entityTickReductionDistance = 30;
    
    public static boolean enableSmartChunkLoading = true;
    public static boolean enableMemoryManagement = true;
    public static boolean enableDynamicShaders = true;
    public static boolean enableGarbageCollection = true;
    public static boolean enableThreadPool = true;
    public static boolean enableEntityLOD = true;
    public static boolean enableAdaptiveEntityLOD = true;
    public static boolean enableDayNightOptimization = false;
    public static boolean enableAudioOptimization = true;
    public static boolean enableAutoTuner = true;
    public static boolean enableFramePrediction = true;
    public static boolean enableTextureUpscaling = true;
    public static boolean enableThermalProtection = true;
    public static boolean enableResourcePreloading = true;
    public static boolean enableSelectiveCulling = true;
    public static boolean enableStateBasedRendering = true;
    public static boolean enableSmartSleep = true;
    public static boolean enableBottleneckDetection = true;
    public static boolean enableStateCompression = true;
    public static boolean enableDimensionOptimizer = true;
    public static boolean enableBatterySaver = true;
    public static boolean enableAntiGhostBlock = true;
    public static boolean enableSmartEntityAI = true;
    public static boolean enableWaterOptimization = true;
    public static boolean enableDynamicChunkLoading = true;
    public static boolean enableSmartCleanup = true;
    public static boolean enableNightVisionOptimizer = true;
    public static boolean enableSprintOptimization = true;
    public static boolean enablePerformanceAnalytics = true;
    public static boolean enableAutoProfile = true;
    public static boolean enableGameModeDetection = true;
    public static boolean enableThermalMonitor = true;
    public static boolean enableSmartVentilation = true;
    public static boolean enableAFKDetection = true;
    public static boolean enableSmartReboot = true;
    public static boolean enableAdaptiveGUIScale = true;
    public static boolean enableTouchOptimization = true;
    public static boolean enableDynamicMusic = true;
    public static boolean enableSmartWeather = true;
    public static boolean enableCombatMode = true;
    public static boolean enableScreenDimmer = true;
    public static boolean enableOptimizedClouds = true;
    public static boolean enableAutoTarget = false;
    // ============================================================
    // NEBULA EXTRA OPTIONS
    // ============================================================
    public static boolean enableRemoveAllParticles = false;
    public static boolean enableRemoveWaterParticles = false;
    public static boolean enableRemoveExplosionParticles = false;
    public static boolean enableRemoveEntityAnimations = false;
    public static boolean enableNoWeather = false;
    public static boolean enableNoDistanceFog = true;
    public static boolean enableFullbright = false;
    // public static boolean enableToggleSneak = false; // Descomentar quando for jogar no PC

    // ===== PERFORMANCE PREDICTION =====
    public static boolean enablePerformancePrediction = true;
    
    // ============================================================
    // PARTICLE KILLER
    // ============================================================
    public static boolean enableParticleKiller = false;
    
    // ============================================================
    // SMOOTH FPS
    // ============================================================
    public static boolean enableSmoothFPS = true;
    public static boolean enableAdaptiveFPS = true;
    
    // ============================================================
    // MEMORY OPTIMIZER
    // ============================================================
    
    // ============================================================
    // CHUNK LOADER OPTIMIZER
    // ============================================================
    public static boolean enableChunkLoaderOptimizer = true;
    
    // ============================================================
    // LAUNCH ACCELERATOR
    // ============================================================
    public static boolean enableLaunchAcceleration = true;
    
    // ============================================================
    // LIGHTING OPTIMIZER
    // ============================================================
    public static boolean enableLightingOptimization = true;
    
    // ============================================================
    // LOGIC OPTIMIZER
    // ============================================================
    public static boolean enableLogicOptimization = true;
    
    // ============================================================
    // NETWORK OPTIMIZER
    // ============================================================
    public static boolean enableNetworkOptimization = true;
    
    // ============================================================
    // ZOOM FEATURE
    // ============================================================
    public static boolean enableZoomFeature = true;
    
    // ============================================================
    // FULLBRIGHT TOGGLE
    // ============================================================
    public static boolean enableFullbrightToggle = true;
    
    // ============================================================
    // COORDINATES DISPLAY
    // ============================================================
    public static boolean enableCoordinatesDisplay = true;
    
    // ============================================================
    // FASTER BLOCK BREAKING
    // ============================================================
    public static boolean enableFasterBlockBreaking = true;
    
    // ============================================================
    // REDUCE SHAKE BOBBING
    // ============================================================
    public static boolean enableReduceShakeBobbing = true;
    
    // ============================================================
    // PUMPKIN OVERLAY DISABLER
    // ============================================================
    public static boolean enablePumpkinOverlayDisabler = true;
    
    // ============================================================
    // FIRE OVERLAY OPACITY
    // ============================================================
    public static boolean enableFireOverlayOpacity = true;
    
    // ============================================================
    // VIGNETTE DISABLER
    // ============================================================
    public static boolean enableVignetteDisabler = true;
    
    // ============================================================
    // SMOOTH WORLD LOADING
    // ============================================================
    public static boolean enableSmoothWorldLoading = true;
    
    // ============================================================
    // LIGHT UPDATES OPTIMIZER
    // ============================================================
    public static boolean enableLightUpdatesOptimizer = true;
    
    // ============================================================
    // REDUCED COLLISIONS
    // ============================================================
    public static boolean enableReducedCollisions = true;
    
    // ============================================================
    // ENTITY CLEANUP
    // ============================================================
    public static boolean enableEntityCleanup = true;
    
    // ============================================================
    // FAR ENTITY SIMPLIFIER
    // ============================================================
    public static boolean enableFarEntitySimplifier = true;
    
    // ============================================================
    // MOD COMPATIBILITY
    // ============================================================
    public static boolean enableModCompatibility = true;
    
    // ============================================================
    // USER RENDER DISTANCE
    // ============================================================
    public static int userRenderDistance = 12;
    
    // ============================================================
    // BLOCK ENTITY CULLING
    // ============================================================
    public static boolean enableBlockEntityCulling = true;
    
    // ============================================================
    // GAME MODE PROFILES
    // ============================================================
    public static boolean enableGameModeProfiles = true;
    
    // ============================================================
    // AUTO OPTIMIZER
    // ============================================================
    public static boolean enableAutoOptimizer = true;
    
    // ============================================================
    // FPS BOOST
    // ============================================================
    public static boolean enableFPSBoost = true;
    public static boolean enableAutoBoost = true;
    
    // ============================================================
    // SMART CLEANUP ADVANCED
    // ============================================================
    public static boolean enableSmartCleanupAdvanced = true;
    
    // ============================================================
    // MOBILE TOUCH CONTROLS
    // ============================================================
    public static boolean enableMobileTouchControls = true;
    public static boolean enableMobilePowerSaving = true;
    
    // ============================================================
    // AI PREDICTOR
    // ============================================================
    
    // ============================================================
    // PERFORMANCE GRAPH
    // ============================================================
    public static boolean enablePerformanceGraph = true;
    
    // ============================================================
    // PERFORMANCE SCORE
    // ============================================================
    public static boolean enablePerformanceScore = true;
    
    // ============================================================
    // FPS HISTORY
    // ============================================================
    public static boolean enableFPSHistory = true;
    
    // ============================================================
    // SMART AI ASSISTANT
    // ============================================================
    public static boolean enableSmartAIAssistant = true;
    
    // ============================================================
    // WORLD ANALYZER
    // ============================================================
    public static boolean enableWorldAnalyzer = true;
    
    // ============================================================
    // FPS PREDICTOR
    // ============================================================
    public static boolean enableFPSPredictor = true;
    
    // ============================================================
    // UI THEME (0=Dark, 1=Normal, 2=Light)
    // ============================================================
    public static int uiTheme = 0;
    
    // ============================================================
    // CATEGORIA GENERAL
    // ============================================================
    public static boolean debugMode = false;
    public static boolean showFps = true;
    public static boolean showPerformanceStats = false;
    
    public static NebulaConfig get() {
        return new NebulaConfig();
    }
    
    public static void init() {
        if (loaded) return;
        loadConfig();
        loaded = true;
    }
    
    private static void loadConfig() {
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) {
                saveConfig();
                return;
            }
            
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();
            
            enableNebula = getBoolean("enableNebula", true);
            enableEntityCulling = getBoolean("enableEntityCulling", true);
            enableAngleCulling = getBoolean("enableAngleCulling", true);
            entityRenderDistance = getInt("entityRenderDistance", 24);
            enableAggressiveCulling = getBoolean("enableAggressiveCulling", true);
            
            enableBlockCulling = getBoolean("enableBlockCulling", true);
            blockCullingDistance = getInt("blockCullingDistance", 12);
            enableVerticalCulling = getBoolean("enableVerticalCulling", true);
            
            enableParticleLimit = getBoolean("enableParticleLimit", true);
            maxParticles = getInt("maxParticles", 200);
            
            enableDynamicResolution = getBoolean("enableDynamicResolution", true);
            minResolutionPercent = getInt("minResolutionPercent", 60);
            maxResolutionPercent = getInt("maxResolutionPercent", 100);
            targetFps = getInt("targetFps", 60);
            
            enableTextureStreaming = getBoolean("enableTextureStreaming", true);
            textureStreamingRadius = getInt("textureStreamingRadius", 30);
            
            enableIntelligentTicking = getBoolean("enableIntelligentTicking", true);
            entityTickReductionDistance = getInt("entityTickReductionDistance", 30);
            
            enableSmartChunkLoading = getBoolean("enableSmartChunkLoading", true);
            enableMemoryManagement = getBoolean("enableMemoryManagement", true);
            enableDynamicShaders = getBoolean("enableDynamicShaders", true);
            enableGarbageCollection = getBoolean("enableGarbageCollection", true);
            enableThreadPool = getBoolean("enableThreadPool", true);
            enableEntityLOD = getBoolean("enableEntityLOD", true);
            enableAdaptiveEntityLOD = getBoolean("enableAdaptiveEntityLOD", true);
            enableDayNightOptimization = getBoolean("enableDayNightOptimization", true);
            enableAudioOptimization = getBoolean("enableAudioOptimization", true);
            enableAutoTuner = getBoolean("enableAutoTuner", true);
            enableFramePrediction = getBoolean("enableFramePrediction", true);
            enableTextureUpscaling = getBoolean("enableTextureUpscaling", true);
            enableThermalProtection = getBoolean("enableThermalProtection", true);
            enableResourcePreloading = getBoolean("enableResourcePreloading", true);
            enableSelectiveCulling = getBoolean("enableSelectiveCulling", true);
            enableStateBasedRendering = getBoolean("enableStateBasedRendering", true);
            enableSmartSleep = getBoolean("enableSmartSleep", true);
            enableBottleneckDetection = getBoolean("enableBottleneckDetection", true);
            enableStateCompression = getBoolean("enableStateCompression", true);
            enableDimensionOptimizer = getBoolean("enableDimensionOptimizer", true);
            enableBatterySaver = getBoolean("enableBatterySaver", true);
            enableAntiGhostBlock = getBoolean("enableAntiGhostBlock", true);
            enableSmartEntityAI = getBoolean("enableSmartEntityAI", true);
            enableWaterOptimization = getBoolean("enableWaterOptimization", true);
            enableDynamicChunkLoading = getBoolean("enableDynamicChunkLoading", true);
            enableSmartCleanup = getBoolean("enableSmartCleanup", true);
            enableNightVisionOptimizer = getBoolean("enableNightVisionOptimizer", true);
            enableSprintOptimization = getBoolean("enableSprintOptimization", true);
            enablePerformanceAnalytics = getBoolean("enablePerformanceAnalytics", true);
            enableAutoProfile = getBoolean("enableAutoProfile", true);
            enableGameModeDetection = getBoolean("enableGameModeDetection", true);
            enableThermalMonitor = getBoolean("enableThermalMonitor", true);
            enableSmartVentilation = getBoolean("enableSmartVentilation", true);
            enableAFKDetection = getBoolean("enableAFKDetection", true);
            enableSmartReboot = getBoolean("enableSmartReboot", true);
            enableAdaptiveGUIScale = getBoolean("enableAdaptiveGUIScale", true);
            enableTouchOptimization = getBoolean("enableTouchOptimization", true);
            enableDynamicMusic = getBoolean("enableDynamicMusic", true);
            enableSmartWeather = getBoolean("enableSmartWeather", true);
            enableCombatMode = getBoolean("enableCombatMode", true);
            enableScreenDimmer = getBoolean("enableScreenDimmer", true);
            enableAutoTarget = getBoolean("enableAutoTarget", false);
            
            enableParticleKiller = getBoolean("enableParticleKiller", false);
            enableSmoothFPS = getBoolean("enableSmoothFPS", true);
            enableAdaptiveFPS = getBoolean("enableAdaptiveFPS", true);
            enableChunkLoaderOptimizer = getBoolean("enableChunkLoaderOptimizer", true);
            enableLaunchAcceleration = getBoolean("enableLaunchAcceleration", true);
            enableLightingOptimization = getBoolean("enableLightingOptimization", true);
            enableLogicOptimization = getBoolean("enableLogicOptimization", true);
            enableNetworkOptimization = getBoolean("enableNetworkOptimization", true);
            enableZoomFeature = getBoolean("enableZoomFeature", true);
            enableFullbrightToggle = getBoolean("enableFullbrightToggle", true);
            enableCoordinatesDisplay = getBoolean("enableCoordinatesDisplay", true);
            enableFasterBlockBreaking = getBoolean("enableFasterBlockBreaking", true);
            enableReduceShakeBobbing = getBoolean("enableReduceShakeBobbing", true);
            enablePumpkinOverlayDisabler = getBoolean("enablePumpkinOverlayDisabler", true);
            enableFireOverlayOpacity = getBoolean("enableFireOverlayOpacity", true);
            enableVignetteDisabler = getBoolean("enableVignetteDisabler", true);
            enableSmoothWorldLoading = getBoolean("enableSmoothWorldLoading", true);
            enableLightUpdatesOptimizer = getBoolean("enableLightUpdatesOptimizer", true);
            enableReducedCollisions = getBoolean("enableReducedCollisions", true);
            enableEntityCleanup = getBoolean("enableEntityCleanup", true);
            enableFarEntitySimplifier = getBoolean("enableFarEntitySimplifier", true);
            enableModCompatibility = getBoolean("enableModCompatibility", true);
            enableOptimizedClouds = getBoolean("enableOptimizedClouds", true);
            
            userRenderDistance = getInt("userRenderDistance", 12);
            enableBlockEntityCulling = getBoolean("enableBlockEntityCulling", true);
            enableGameModeProfiles = getBoolean("enableGameModeProfiles", true);
            enableAutoOptimizer = getBoolean("enableAutoOptimizer", true);
            enableFPSBoost = getBoolean("enableFPSBoost", true);
            enableAutoBoost = getBoolean("enableAutoBoost", true);
            enableSmartCleanupAdvanced = getBoolean("enableSmartCleanupAdvanced", true);
            enableMobileTouchControls = getBoolean("enableMobileTouchControls", true);
            enableMobilePowerSaving = getBoolean("enableMobilePowerSaving", true);
            enablePerformanceGraph = getBoolean("enablePerformanceGraph", true);
            enablePerformanceScore = getBoolean("enablePerformanceScore", true);
            enableFPSHistory = getBoolean("enableFPSHistory", true);
            enableSmartAIAssistant = getBoolean("enableSmartAIAssistant", true);
            enableWorldAnalyzer = getBoolean("enableWorldAnalyzer", true);
            enableFPSPredictor = getBoolean("enableFPSPredictor", true);
            enablePerformancePrediction = getBoolean("enablePerformancePrediction", true);
            
            uiTheme = getInt("uiTheme", 0);
            
            debugMode = getBoolean("debugMode", false);
            showFps = getBoolean("showFps", true);
            showPerformanceStats = getBoolean("showPerformanceStats", false);
            
            System.out.println("[Nebula] Configurações carregadas!");
            
        } catch (Exception e) {
            System.err.println("[Nebula] Erro ao carregar configurações: " + e.getMessage());
            saveConfig();
        }
    }
    
    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }
    
    private static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static void saveConfig() {
        try {
            File file = new File(CONFIG_FILE);
            file.getParentFile().mkdirs();
            
            properties.setProperty("enableNebula", String.valueOf(enableNebula));
            properties.setProperty("enableEntityCulling", String.valueOf(enableEntityCulling));
            properties.setProperty("enableAngleCulling", String.valueOf(enableAngleCulling));
            properties.setProperty("entityRenderDistance", String.valueOf(entityRenderDistance));
            properties.setProperty("enableAggressiveCulling", String.valueOf(enableAggressiveCulling));
            properties.setProperty("enableBlockCulling", String.valueOf(enableBlockCulling));
            properties.setProperty("blockCullingDistance", String.valueOf(blockCullingDistance));
            properties.setProperty("enableVerticalCulling", String.valueOf(enableVerticalCulling));
            properties.setProperty("enableParticleLimit", String.valueOf(enableParticleLimit));
            properties.setProperty("maxParticles", String.valueOf(maxParticles));
            properties.setProperty("enableDynamicResolution", String.valueOf(enableDynamicResolution));
            properties.setProperty("minResolutionPercent", String.valueOf(minResolutionPercent));
            properties.setProperty("maxResolutionPercent", String.valueOf(maxResolutionPercent));
            properties.setProperty("targetFps", String.valueOf(targetFps));
            properties.setProperty("enableTextureStreaming", String.valueOf(enableTextureStreaming));
            properties.setProperty("textureStreamingRadius", String.valueOf(textureStreamingRadius));
            properties.setProperty("enableIntelligentTicking", String.valueOf(enableIntelligentTicking));
            properties.setProperty("entityTickReductionDistance", String.valueOf(entityTickReductionDistance));
            properties.setProperty("enableSmartChunkLoading", String.valueOf(enableSmartChunkLoading));
            properties.setProperty("enableMemoryManagement", String.valueOf(enableMemoryManagement));
            properties.setProperty("enableDynamicShaders", String.valueOf(enableDynamicShaders));
            properties.setProperty("enableGarbageCollection", String.valueOf(enableGarbageCollection));
            properties.setProperty("enableThreadPool", String.valueOf(enableThreadPool));
            properties.setProperty("enableEntityLOD", String.valueOf(enableEntityLOD));
            properties.setProperty("enableAdaptiveEntityLOD", String.valueOf(enableAdaptiveEntityLOD));
            properties.setProperty("enableDayNightOptimization", String.valueOf(enableDayNightOptimization));
            properties.setProperty("enableAudioOptimization", String.valueOf(enableAudioOptimization));
            properties.setProperty("enableAutoTuner", String.valueOf(enableAutoTuner));
            properties.setProperty("enableFramePrediction", String.valueOf(enableFramePrediction));
            properties.setProperty("enableTextureUpscaling", String.valueOf(enableTextureUpscaling));
            properties.setProperty("enableThermalProtection", String.valueOf(enableThermalProtection));
            properties.setProperty("enableResourcePreloading", String.valueOf(enableResourcePreloading));
            properties.setProperty("enableSelectiveCulling", String.valueOf(enableSelectiveCulling));
            properties.setProperty("enableStateBasedRendering", String.valueOf(enableStateBasedRendering));
            properties.setProperty("enableSmartSleep", String.valueOf(enableSmartSleep));
            properties.setProperty("enableBottleneckDetection", String.valueOf(enableBottleneckDetection));
            properties.setProperty("enableStateCompression", String.valueOf(enableStateCompression));
            properties.setProperty("enableDimensionOptimizer", String.valueOf(enableDimensionOptimizer));
            properties.setProperty("enableBatterySaver", String.valueOf(enableBatterySaver));
            properties.setProperty("enableAntiGhostBlock", String.valueOf(enableAntiGhostBlock));
            properties.setProperty("enableSmartEntityAI", String.valueOf(enableSmartEntityAI));
            properties.setProperty("enableWaterOptimization", String.valueOf(enableWaterOptimization));
            properties.setProperty("enableDynamicChunkLoading", String.valueOf(enableDynamicChunkLoading));
            properties.setProperty("enableSmartCleanup", String.valueOf(enableSmartCleanup));
            properties.setProperty("enableNightVisionOptimizer", String.valueOf(enableNightVisionOptimizer));
            properties.setProperty("enableSprintOptimization", String.valueOf(enableSprintOptimization));
            properties.setProperty("enablePerformanceAnalytics", String.valueOf(enablePerformanceAnalytics));
            properties.setProperty("enableAutoProfile", String.valueOf(enableAutoProfile));
            properties.setProperty("enableGameModeDetection", String.valueOf(enableGameModeDetection));
            properties.setProperty("enableThermalMonitor", String.valueOf(enableThermalMonitor));
            properties.setProperty("enableSmartVentilation", String.valueOf(enableSmartVentilation));
            properties.setProperty("enableAFKDetection", String.valueOf(enableAFKDetection));
            properties.setProperty("enableSmartReboot", String.valueOf(enableSmartReboot));
            properties.setProperty("enableAdaptiveGUIScale", String.valueOf(enableAdaptiveGUIScale));
            properties.setProperty("enableTouchOptimization", String.valueOf(enableTouchOptimization));
            properties.setProperty("enableDynamicMusic", String.valueOf(enableDynamicMusic));
            properties.setProperty("enableSmartWeather", String.valueOf(enableSmartWeather));
            properties.setProperty("enableCombatMode", String.valueOf(enableCombatMode));
            properties.setProperty("enableScreenDimmer", String.valueOf(enableScreenDimmer));
            properties.setProperty("enableAutoTarget", String.valueOf(enableAutoTarget));
            properties.setProperty("enableParticleKiller", String.valueOf(enableParticleKiller));
            properties.setProperty("enableSmoothFPS", String.valueOf(enableSmoothFPS));
            properties.setProperty("enableAdaptiveFPS", String.valueOf(enableAdaptiveFPS));
            properties.setProperty("enableChunkLoaderOptimizer", String.valueOf(enableChunkLoaderOptimizer));
            properties.setProperty("enableLaunchAcceleration", String.valueOf(enableLaunchAcceleration));
            properties.setProperty("enableLightingOptimization", String.valueOf(enableLightingOptimization));
            properties.setProperty("enableLogicOptimization", String.valueOf(enableLogicOptimization));
            properties.setProperty("enableNetworkOptimization", String.valueOf(enableNetworkOptimization));
            properties.setProperty("enableZoomFeature", String.valueOf(enableZoomFeature));
            properties.setProperty("enableFullbrightToggle", String.valueOf(enableFullbrightToggle));
            properties.setProperty("enableCoordinatesDisplay", String.valueOf(enableCoordinatesDisplay));
            properties.setProperty("enableFasterBlockBreaking", String.valueOf(enableFasterBlockBreaking));
            properties.setProperty("enableReduceShakeBobbing", String.valueOf(enableReduceShakeBobbing));
            properties.setProperty("enablePumpkinOverlayDisabler", String.valueOf(enablePumpkinOverlayDisabler));
            properties.setProperty("enableFireOverlayOpacity", String.valueOf(enableFireOverlayOpacity));
            properties.setProperty("enableVignetteDisabler", String.valueOf(enableVignetteDisabler));
            properties.setProperty("enableSmoothWorldLoading", String.valueOf(enableSmoothWorldLoading));
            properties.setProperty("enableLightUpdatesOptimizer", String.valueOf(enableLightUpdatesOptimizer));
            properties.setProperty("enableReducedCollisions", String.valueOf(enableReducedCollisions));
            properties.setProperty("enableEntityCleanup", String.valueOf(enableEntityCleanup));
            properties.setProperty("enableFarEntitySimplifier", String.valueOf(enableFarEntitySimplifier));
            properties.setProperty("enableModCompatibility", String.valueOf(enableModCompatibility));
            properties.setProperty("enableOptimizedClouds", String.valueOf(enableOptimizedClouds));
            
            properties.setProperty("userRenderDistance", String.valueOf(userRenderDistance));
            properties.setProperty("enableBlockEntityCulling", String.valueOf(enableBlockEntityCulling));
            properties.setProperty("enableGameModeProfiles", String.valueOf(enableGameModeProfiles));
            properties.setProperty("enableAutoOptimizer", String.valueOf(enableAutoOptimizer));
            properties.setProperty("enableFPSBoost", String.valueOf(enableFPSBoost));
            properties.setProperty("enableAutoBoost", String.valueOf(enableAutoBoost));
            properties.setProperty("enableSmartCleanupAdvanced", String.valueOf(enableSmartCleanupAdvanced));
            properties.setProperty("enableMobileTouchControls", String.valueOf(enableMobileTouchControls));
            properties.setProperty("enableMobilePowerSaving", String.valueOf(enableMobilePowerSaving));
            properties.setProperty("enablePerformanceGraph", String.valueOf(enablePerformanceGraph));
            properties.setProperty("enablePerformanceScore", String.valueOf(enablePerformanceScore));
            properties.setProperty("enableFPSHistory", String.valueOf(enableFPSHistory));
            properties.setProperty("enableSmartAIAssistant", String.valueOf(enableSmartAIAssistant));
            properties.setProperty("enableWorldAnalyzer", String.valueOf(enableWorldAnalyzer));
            properties.setProperty("enableFPSPredictor", String.valueOf(enableFPSPredictor));
            properties.setProperty("enablePerformancePrediction", String.valueOf(enablePerformancePrediction));
            
            properties.setProperty("uiTheme", String.valueOf(uiTheme));
            
            properties.setProperty("debugMode", String.valueOf(debugMode));
            properties.setProperty("showFps", String.valueOf(showFps));
            properties.setProperty("showPerformanceStats", String.valueOf(showPerformanceStats));
            
            FileOutputStream fos = new FileOutputStream(file);
            properties.store(fos, "Nebula Optimizer Configuration");
            fos.close();
            
        } catch (Exception e) {
            System.err.println("[Nebula] Erro ao salvar configurações: " + e.getMessage());
        }
    }
    
    public static void reload() {
        loaded = false;
        init();
    }
}
