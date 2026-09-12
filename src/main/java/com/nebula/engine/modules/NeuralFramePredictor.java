package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.PerformanceMonitor;

public class NeuralFramePredictor implements NebulaModule {
    private static final NeuralFramePredictor INSTANCE = new NeuralFramePredictor();
    private boolean enabled = true;
    private float[][] previousFrames = new float[3][];
    private boolean isPredicting = false;
    
    private NeuralFramePredictor() {}
    
    public static NeuralFramePredictor getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Neural Frame Prediction";
    }
    
    @Override
    public void initialize() {
        System.out.println("🧠 [Nebula] Neural Frame Predictor inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        double fps = PerformanceMonitor.getInstance().getCurrentFPS();
        if (fps < 30) {
            // Ativa previsão quando FPS está baixo
            if (!isPredicting) {
                isPredicting = true;
                System.out.println("🧠 [Nebula] Ativando predição de quadros...");
            }
        } else if (fps > 45 && isPredicting) {
            isPredicting = false;
            System.out.println("🧠 [Nebula] Desativando predição de quadros");
        }
    }
    
    public boolean shouldPredictFrame() {
        return isPredicting && enabled && NebulaConfig.get().enableFramePrediction;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🧠 [Nebula] Neural Frame Predictor desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableFramePrediction;
    }
}
