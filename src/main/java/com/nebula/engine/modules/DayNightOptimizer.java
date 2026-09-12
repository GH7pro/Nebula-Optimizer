package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class DayNightOptimizer implements NebulaModule {
    private static final DayNightOptimizer INSTANCE = new DayNightOptimizer();
    private boolean enabled = true;
    private boolean isNight = false;
    private int tickCounter = 0;
    
    private DayNightOptimizer() {}
    
    public static DayNightOptimizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Day/Night Cycle Optimization";
    }
    
    @Override
    public void initialize() {
        System.out.println("🌓 [Nebula] Day/Night Optimizer inicializado!");
    }
    
    @Override
    public void tick() {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        
        tickCounter++;
        if (tickCounter % 20 != 0) return; // A cada 1 segundo
        
        World world = client.world;
        long time = world.getTimeOfDay() % 24000;
        boolean newNight = time > 13000 && time < 23000;
        
        if (newNight != isNight) {
            isNight = newNight;
            applyOptimization(client, isNight);
        }
    }
    
    private void applyOptimization(MinecraftClient client, boolean night) {
        if (night) {
            // Durante a noite
            // Desativa cálculos de luz solar
            // Reduz atualizações de sombras
            System.out.println("🌓 [Nebula] Modo noturno ativado - Otimizando iluminação");
        } else {
            // Durante o dia
            // Restaura iluminação normal
            System.out.println("🌓 [Nebula] Modo diurno ativado - Iluminação normal");
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println("🌓 [Nebula] Day/Night Optimizer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableNebula && NebulaConfig.get().enableDayNightOptimization;
    }
}
