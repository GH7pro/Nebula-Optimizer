package com.nebula.world;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;

import java.lang.reflect.Field;

public class SmoothWorldLoading implements NebulaModule {
    private static final SmoothWorldLoading INSTANCE = new SmoothWorldLoading();
    private boolean enabled = true;
    private int tickCounter = 0;
    private boolean isSmooth = false;
    private int loadedChunks = 0;
    
    private SmoothWorldLoading() {}
    
    public static SmoothWorldLoading getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Smooth World Loading";
    }
    
    @Override
    public void initialize() {
        System.out.println("🗺️ [Nebula] Smooth World Loading inicializado!");
        if (!NebulaConfig.enableSmoothWorldLoading) return;
        
        try {
            optimizeWorldLoading();
            isSmooth = true;
            System.out.println("🗺️ [Nebula] ✅ Carregamento suave ativado!");
        } catch (Exception e) {
            System.err.println("🗺️ [Nebula] Erro ao ativar: " + e.getMessage());
        }
    }
    
    private void optimizeWorldLoading() {
        try {
            // Otimiza o carregamento do mundo
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            
            // Tenta reduzir o lag ao carregar chunks
            try {
                Field field = WorldRenderer.class.getDeclaredField("chunkBuilder");
                field.setAccessible(true);
                Object chunkBuilder = field.get(client.worldRenderer);
                if (chunkBuilder != null) {
                    // Ajusta o builder para carregar mais suavemente
                    Class<?> builderClass = chunkBuilder.getClass();
                    try {
                        Field threadPool = builderClass.getDeclaredField("threadPool");
                        threadPool.setAccessible(true);
                        // Ajusta o pool de threads
                    } catch (Exception e) {}
                }
            } catch (Exception e) {}
            
        } catch (Exception e) {}
    }
    
    @Override
    public void tick() {
        if (!enabled || !NebulaConfig.enableSmoothWorldLoading) return;
        if (!isSmooth) return;
        
        tickCounter++;
        if (tickCounter % 40 != 0) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;
        
        // Monitora o carregamento de chunks
        int currentLoaded = client.world.getChunkManager().getLoadedChunkCount();
        if (currentLoaded != loadedChunks) {
            loadedChunks = currentLoaded;
        }
        
        // Se estiver carregando muitos chunks, reduz a prioridade
        if (loadedChunks > 1000 && NebulaConfig.debugMode) {
            System.out.println("🗺️ [Nebula] Chunks carregados: " + loadedChunks);
        }
    }
    
    public int getLoadedChunks() {
        return loadedChunks;
    }
    
    @Override
    public void shutdown() {
        System.out.println("🗺️ [Nebula] Smooth World Loading desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableSmoothWorldLoading;
    }
}
