package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager implements NebulaModule {
    private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    private ExecutorService executor;
    private boolean enabled = true;
    
    private static final int THREAD_COUNT = 2;
    
    private ThreadPoolManager() {}
    
    public static ThreadPoolManager getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Thread Pool Optimization";
    }
    
    @Override
    public void initialize() {
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
        System.out.println("⚡ [Nebula] Thread Pool inicializado com " + THREAD_COUNT + " threads!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        // Executa tarefas leves em background
        executor.submit(() -> {
            // Processa chunks distantes
            // Cálculos de física simplificados
            // Atualizações de entidades longe do jogador
        });
    }
    
    public void submitAsync(Runnable task) {
        if (executor != null && !executor.isShutdown()) {
            executor.submit(task);
        }
    }
    
    @Override
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        System.out.println("⚡ [Nebula] Thread Pool desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableThreadPool;
    }
}
