package com.nebula.optimizer;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.PerformanceMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

import java.util.HashMap;
import java.util.Map;

public class AutoOptimizer implements NebulaModule {
    private static final AutoOptimizer INSTANCE = new AutoOptimizer();
    private boolean enabled = true;
    private int tickCounter = 0;
    private boolean isOptimizing = false;
    private int currentTest = 0;
    private Map<Integer, Double> testResults = new HashMap<>();
    private int bestConfig = 2;
    private double bestFps = 0;
    
    private static final int[] TEST_CONFIGS = {0, 1, 2, 3, 4};
    private static final String[] CONFIG_NAMES = {
        "Ultra Low", "Low", "Medium", "High", "Ultra"
    };
    
    private AutoOptimizer() {}
    
    public static AutoOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Auto-Optimizer";
    }
    
    @Override
    public void initialize() {
        System.out.println("🎯 [Nebula] Auto-Optimizer inicializado!");
        // Removido o new Thread() para evitar crashes e conflitos com a Render Thread.
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableAutoOptimizer) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) return;

        tickCounter++;
        
        // Se não estiver otimizando, espera 30 segundos (600 ticks) para iniciar
        if (!isOptimizing && tickCounter % 600 == 0) {
            startOptimization();
        }
        
        // Se estiver otimizando, testa a próxima config a cada 10 segundos (200 ticks)
        if (isOptimizing && tickCounter % 200 == 0) {
            testNextConfig();
        }
    }
    
    private void startOptimization() {
        System.out.println("🎯 [Nebula] 🔧 Auto-Optimizer iniciando...");
        System.out.println("🎯 [Nebula] Testando 5 configurações diferentes...");
        isOptimizing = true;
        currentTest = 0;
        testResults.clear();
        bestFps = 0;
        bestConfig = 2;
        testNextConfig();
    }
    
    private void testNextConfig() {
        if (currentTest >= TEST_CONFIGS.length) {
            finishOptimization();
            return;
        }
        
        int config = TEST_CONFIGS[currentTest];
        applyConfig(config);
        System.out.println("🎯 [Nebula] 📊 Testando [" + CONFIG_NAMES[config] + "] (" + (currentTest + 1) + "/" + TEST_CONFIGS.length + ")");
        
        // Coleta FPS médio
        double fps = PerformanceMonitor.getInstance().getCurrentFPS();
        testResults.put(config, fps);
        
        if (fps > bestFps) {
            bestFps = fps;
            bestConfig = config;
        }
        
        currentTest++;
    }
    
    private void applyConfig(int config) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        
        // NÃO MEXEMOS MAIS EM ViewDistance NEM SimulationDistance AQUI!
        // Isso garante que as chunks não vão recarregar do nada.
        switch (config) {
            case 0: // Ultra Low
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getParticles().setValue(ParticlesMode.MINIMAL);
                client.options.getEntityShadows().setValue(false);
                client.options.getMaxFps().setValue(30);
                break;
            case 1: // Low
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
                client.options.getEntityShadows().setValue(false);
                client.options.getMaxFps().setValue(45);
                break;
            case 2: // Medium
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                client.options.getParticles().setValue(ParticlesMode.DECREASED);
                client.options.getEntityShadows().setValue(true);
                client.options.getMaxFps().setValue(60);
                break;
            case 3: // High
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
                client.options.getParticles().setValue(ParticlesMode.ALL);
                client.options.getEntityShadows().setValue(true);
                client.options.getMaxFps().setValue(90);
                break;
            case 4: // Ultra
                client.options.getGraphicsMode().setValue(GraphicsMode.FANCY); // Mudado de FABULOUS para FANCY
                client.options.getParticles().setValue(ParticlesMode.ALL);
                client.options.getEntityShadows().setValue(true);
                client.options.getMaxFps().setValue(120);
                break;
        }
    }
    
    private void finishOptimization() {
        isOptimizing = false;
        System.out.println("🎯 [Nebula] ✅ Auto-Optimizer concluído!");
        System.out.println("🎯 [Nebula] 🏆 Melhor configuração visual: " + CONFIG_NAMES[bestConfig]);
        System.out.println("🎯 [Nebula] 📊 FPS médio: " + String.format("%.1f", bestFps));
        
        // Aplica a melhor configuração visual
        applyConfig(bestConfig);
    }
    
    public boolean isOptimizing() {
        return isOptimizing;
    }
    
    public int getBestConfig() {
        return bestConfig;
    }
    
    public double getBestFps() {
        return bestFps;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🎯 [Nebula] Auto-Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableAutoOptimizer;
    }
}
