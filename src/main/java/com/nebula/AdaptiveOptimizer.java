package com.nebula;

/**
 * O AdaptiveOptimizer analisa o desempenho atual do jogo usando o PerformanceMonitor
 * e ajusta dinamicamente as configurações do NebulaConfig para manter o FPS alvo.
 */
public class AdaptiveOptimizer {

    private static AdaptiveOptimizer instance;

    private int checkIntervalTicks = 100;
    private int tickCounter = 0;
    private int currentOptimizationLevel = 0;

    // NOVO: Variáveis para o Modo Pânico
    private boolean inPanicMode = false;
    private int panicCooldownTicks = 0;

    // NOVO: Backups para não sobrescrever a config original do jogador
    private boolean configsBackedUp = false;
    private int backupMaxParticles;
    private int backupEntityRenderDistance;
    private int backupBlockCullingDistance;
    private int backupEntityTickReductionDistance;
    private boolean backupParticleKiller;

    public static AdaptiveOptimizer getInstance() {
        if (instance == null) {
            instance = new AdaptiveOptimizer();
        }
        return instance;
    }

    private AdaptiveOptimizer() {}

    public void onTick() {
        if (!NebulaConfig.enableAutoOptimizer) return;

        // NOVO: Verificação do Modo Pânico (roda todo tick para ser instantâneo)
        if (PerformanceMonitor.getInstance().isSuddenDropDetected() && !inPanicMode && currentOptimizationLevel < 4) {
            triggerPanicMode();
        }

        if (inPanicMode) {
            panicCooldownTicks++;
            // Sai do modo pânico após 10 segundos (200 ticks) se o FPS estabilizar
            if (panicCooldownTicks > 200 && !PerformanceMonitor.getInstance().isSuddenDropDetected()) {
                exitPanicMode();
            }
            return;
        }

        tickCounter++;
        if (tickCounter >= checkIntervalTicks) {
            tickCounter = 0;
            evaluateAndAdjust();
        }
    }

    private void evaluateAndAdjust() {
        double currentFps = PerformanceMonitor.getInstance().getCurrentFPS();
        double trend = PerformanceMonitor.getInstance().getFpsTrend(); // NOVO: Heurística
        int targetFps = NebulaConfig.targetFps;

        if (currentFps <= 0) return;

        // NOVO: Heurística de IA. Se o FPS está caindo rápido, antecipa a otimização!
        if (currentFps < targetFps - 10 || (currentFps < targetFps && trend < -3.0)) {
            increaseOptimization();
        } else if (currentFps > targetFps + 20) {
            decreaseOptimization();
        }
    }

    // NOVO: Método para acionar o Modo Pânico
    private void triggerPanicMode() {
        inPanicMode = true;
        panicCooldownTicks = 0;
        backupConfigs(); // Salva o que o jogador tinha antes do pânico
        
        if (NebulaConfig.debugMode) {
            System.out.println("[Nebula] 🚨 MODO PÂNICO ATIVADO! Queda súbita de FPS detectada.");
        }

        // Ações imediatas e drásticas
        NebulaConfig.maxParticles = 0;
        NebulaConfig.entityRenderDistance = 6;
        NebulaConfig.enableParticleKiller = true;
    }

    // NOVO: Método para sair do Modo Pânico
    private void exitPanicMode() {
        inPanicMode = false;
        if (NebulaConfig.debugMode) {
            System.out.println("[Nebula] ✅ Modo Pânico desativado. Restaurando configurações...");
        }
        // Retorna para o nível de otimização que estava antes, ou nível 3
        currentOptimizationLevel = 3;
        applyLevel(currentOptimizationLevel);
    }

    // NOVO: Faz backup das configs antes de alterá-las
    private void backupConfigs() {
        if (!configsBackedUp) {
            backupMaxParticles = NebulaConfig.maxParticles;
            backupEntityRenderDistance = NebulaConfig.entityRenderDistance;
            backupBlockCullingDistance = NebulaConfig.blockCullingDistance;
            backupEntityTickReductionDistance = NebulaConfig.entityTickReductionDistance;
            backupParticleKiller = NebulaConfig.enableParticleKiller;
            configsBackedUp = true;
        }
    }

    // NOVO: Aplica um nível específico (usado para restaurar do pânico)
    private void applyLevel(int level) {
        switch (level) {
            case 0:
                if (configsBackedUp) {
                    NebulaConfig.maxParticles = backupMaxParticles;
                    NebulaConfig.entityRenderDistance = backupEntityRenderDistance;
                    NebulaConfig.blockCullingDistance = backupBlockCullingDistance;
                    NebulaConfig.entityTickReductionDistance = backupEntityTickReductionDistance;
                    NebulaConfig.enableParticleKiller = backupParticleKiller;
                    configsBackedUp = false;
                }
                break;
            // Caso 1, 2, 3 usam a mesma lógica do increaseOptimization abaixo
        }
    }

    private void increaseOptimization() {
        if (currentOptimizationLevel >= 4) return;
        currentOptimizationLevel++;
        
        if (!configsBackedUp) backupConfigs(); // Garante o backup antes de mudar

        if (NebulaConfig.debugMode) {
            System.out.println("[Nebula] AdaptiveOptimizer: Aumentando para Nível " + currentOptimizationLevel);
        }

        switch (currentOptimizationLevel) {
            case 1:
                NebulaConfig.maxParticles = 100;
                NebulaConfig.entityRenderDistance = 16;
                break;
            case 2:
                NebulaConfig.enableAggressiveCulling = true;
                NebulaConfig.entityTickReductionDistance = 20;
                NebulaConfig.blockCullingDistance = 8;
                break;
            case 3:
                NebulaConfig.maxParticles = 50;
                NebulaConfig.entityRenderDistance = 12;
                NebulaConfig.enableFarEntitySimplifier = true;
                break;
            case 4:
                NebulaConfig.maxParticles = 10;
                NebulaConfig.enableParticleKiller = true;
                NebulaConfig.entityRenderDistance = 8;
                NebulaConfig.enableReducedCollisions = true;
                break;
        }
    }

    private void decreaseOptimization() {
        if (currentOptimizationLevel <= 0) return;
        currentOptimizationLevel--;
        
        if (NebulaConfig.debugMode) {
            System.out.println("[Nebula] AdaptiveOptimizer: Reduzindo para Nível " + currentOptimizationLevel);
        }

        switch (currentOptimizationLevel) {
            case 0:
                // NOVO: Restaura os backups originais em vez de valores fixos!
                if (configsBackedUp) {
                    NebulaConfig.maxParticles = backupMaxParticles;
                    NebulaConfig.entityRenderDistance = backupEntityRenderDistance;
                    NebulaConfig.blockCullingDistance = backupBlockCullingDistance;
                    NebulaConfig.entityTickReductionDistance = backupEntityTickReductionDistance;
                    NebulaConfig.enableParticleKiller = backupParticleKiller;
                    configsBackedUp = false;
                }
                break;
            case 1:
                NebulaConfig.maxParticles = 100;
                NebulaConfig.entityRenderDistance = 16;
                NebulaConfig.enableParticleKiller = false;
                break;
            case 2:
                NebulaConfig.maxParticles = 50;
                NebulaConfig.entityRenderDistance = 12;
                break;
            case 3:
                NebulaConfig.maxParticles = 50;
                break;
        }
    }

    public int getCurrentOptimizationLevel() {
        return currentOptimizationLevel;
    }
    
    public boolean isInPanicMode() {
        return inPanicMode;
    }
}
