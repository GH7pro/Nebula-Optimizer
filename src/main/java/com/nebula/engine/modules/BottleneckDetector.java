package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import com.nebula.PerformanceMonitor;

public class BottleneckDetector implements NebulaModule {
    private static final BottleneckDetector INSTANCE = new BottleneckDetector();
    private boolean enabled = true;
    private int tickCounter = 0;
    
    private static final int CHECK_INTERVAL = 200;
    
    private BottleneckDetector() {}
    
    public static BottleneckDetector getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Bottleneck Detection";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔍 [Nebula] Bottleneck Detector inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        tickCounter++;
        if (tickCounter % CHECK_INTERVAL != 0) return;
        
        detectBottlenecks();
    }
    
    private void detectBottlenecks() {
        double fps = PerformanceMonitor.getInstance().getCurrentFPS();
        double cpuUsage = getCPUUsage();
        double ramUsage = getRAMUsage();
        
        if (fps < 30) {
            if (cpuUsage > 80) {
                System.out.println("🔍 [Nebula] 🖥️ Gargalo: CPU (" + (int)cpuUsage + "%)");
                System.out.println("🔍 [Nebula] Sugestão: Reduza distância de renderização");
            } else if (ramUsage > 80) {
                System.out.println("🔍 [Nebula] 💾 Gargalo: RAM (" + (int)ramUsage + "%)");
                System.out.println("🔍 [Nebula] Sugestão: Aumente a memória alocada");
            } else {
                System.out.println("🔍 [Nebula] 🎮 Gargalo: GPU");
                System.out.println("🔍 [Nebula] Sugestão: Reduza qualidade gráfica");
            }
        }
    }
    
    private double getCPUUsage() {
        try {
            Process process = Runtime.getRuntime().exec("top -bn1 | grep 'Cpu(s)'");
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            String line = reader.readLine();
            reader.close();
            
            if (line != null) {
                String[] parts = line.split(",");
                for (String part : parts) {
                    if (part.contains("us")) {
                        return Double.parseDouble(part.replace("us", "").trim());
                    }
                }
            }
        } catch (Exception e) {}
        return 50;
    }
    
    private double getRAMUsage() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return (double) usedMemory / maxMemory * 100;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🔍 [Nebula] Bottleneck Detector desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableBottleneckDetection;
    }
}
