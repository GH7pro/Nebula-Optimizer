package com.nebula;

import java.util.LinkedList;
import java.util.Queue;

public class PerformanceMonitor {
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    private Queue<Double> fpsHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 20;
    private double currentFPS = 60.0;
    private double averageFPS = 60.0;
    private long lastUpdate = System.currentTimeMillis();
    private int frameCount = 0;
    
    // NOVO: Variáveis para detecção de tendência e pânico
    private double previousAverageFPS = 60.0;
    private double fpsTrend = 0.0; // Negativo = caindo, Positivo = subindo
    private boolean suddenDropDetected = false;
    
    private PerformanceMonitor() {}
    
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }
    
    public void tick() {
        frameCount++;
        long now = System.currentTimeMillis();
        
        if (now - lastUpdate >= 1000) {
            previousAverageFPS = averageFPS; // NOVO: Salva o FPS de antes
            currentFPS = (frameCount * 1000.0) / (now - lastUpdate);
            
            fpsHistory.add(currentFPS);
            if (fpsHistory.size() > HISTORY_SIZE) {
                fpsHistory.poll();
            }
            
            averageFPS = fpsHistory.stream().mapToDouble(Double::doubleValue).average().orElse(currentFPS);
            
            // NOVO: Calcula a tendência (Heurística simples no lugar da rede neural pesada)
            fpsTrend = averageFPS - previousAverageFPS;
            
            // NOVO: Detecta Modo Pânico (Queda de mais de 40% no FPS em 1 segundo)
            if (previousAverageFPS > 30 && averageFPS < (previousAverageFPS * 0.60)) {
                suddenDropDetected = true;
            } else {
                suddenDropDetected = false;
            }
            
            frameCount = 0;
            lastUpdate = now;
            
            NebulaConfig config = NebulaConfig.get();
            if (config.showFps) {
                System.out.printf("🎮 [Nebula] FPS: %.1f (Média: %.1f)%n", currentFPS, averageFPS);
            }
        }
    }
    
    public double getCurrentFPS() {
        return averageFPS > 0 ? averageFPS : currentFPS;
    }
    
    public double getRawFPS() {
        return currentFPS;
    }

    // NOVO: Retorna a tendência do FPS (ex: -2.5 significa que está caindo 2.5 FPS por segundo)
    public double getFpsTrend() {
        return fpsTrend;
    }

    // NOVO: Retorna true se o FPS sofreu uma queda brusca repentina
    public boolean isSuddenDropDetected() {
        return suddenDropDetected;
    }
}
