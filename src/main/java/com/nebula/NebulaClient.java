package com.nebula;
import com.nebula.optimizer.SmartAutoTuner;

import com.nebula.engine.NebulaEngine;
import com.nebula.engine.ModuleManager;
import com.nebula.lib.api.BudgetAPI;
import com.nebula.lib.api.FocusAPI;
import com.nebula.lib.api.NebulaAPI;
import com.nebula.lib.api.PerformanceAPI;
import com.nebula.lib.api.ProfileAPI;
import com.nebula.engine.modules.SmartRedstoneEngine;
import com.nebula.engine.modules.FallingBlockBatcher;
import com.nebula.engine.modules.HopperOptimizer;
import com.nebula.engine.modules.DroppedItemOptimizer;
import com.nebula.gui.NebulaHudOverlay;
import com.nebula.engine.modules.CloudOptimizer;
import com.nebula.engine.modules.ThermalCuller;
import com.nebula.engine.modules.AnimationThrottler;
import net.minecraft.client.option.GraphicsMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import com.nebula.afk.AFKDetector;
import com.nebula.block.FasterBlockBreaking;
import com.nebula.boost.FPSBoostMode;
import com.nebula.camera.ReduceShakeBobbing;
import com.nebula.chunk.ChunkLoaderOptimizer;
import com.nebula.cleanup.SmartCleanup;
import com.nebula.compat.ModCompatibility;
import com.nebula.compatibility.GenericModAdapter;
import com.nebula.compatibility.PixelmonAdapter;
import com.nebula.coords.CoordinatesDisplay;
import com.nebula.engine.modules.AdaptiveEntityLOD;
import com.nebula.engine.modules.AudioOptimizer;
import com.nebula.engine.modules.AutoTuner;
import com.nebula.engine.modules.BatterySaver;
import com.nebula.engine.modules.BottleneckDetector;
import com.nebula.engine.modules.DayNightOptimizer;
import com.nebula.engine.modules.DimensionOptimizer;
import com.nebula.engine.modules.DynamicChunkLoader;
import com.nebula.engine.modules.DynamicShaders;
import com.nebula.engine.modules.EntityLOD;
import com.nebula.engine.modules.GameStateCompressor;
import com.nebula.engine.modules.MemoryManager;
import com.nebula.engine.modules.NeuralFramePredictor;
import com.nebula.engine.modules.NightVisionOptimizer;
import com.nebula.engine.modules.ResolutionModule;
import com.nebula.engine.modules.SelectiveCulling;
import com.nebula.engine.modules.SmartChunkLoader;
import com.nebula.engine.modules.SmartResourcePreloader;
import com.nebula.engine.modules.SmartSleepMode;
import com.nebula.engine.modules.SprintOptimizer;
import com.nebula.engine.modules.StateBasedRenderer;
import com.nebula.engine.modules.TextureStreamingModule;
import com.nebula.engine.modules.TextureUpscaler;
import com.nebula.engine.modules.ThreadPoolManager;
import com.nebula.engine.modules.TickSchedulingModule;
import com.nebula.entity.EntityCleanup;
import com.nebula.entity.FarEntitySimplifier;
import com.nebula.entity.ReducedCollisions;
import com.nebula.fps.SmoothFPS;
import com.nebula.fullbright.FullbrightToggle;
import com.nebula.launch.LaunchAccelerator;
import com.nebula.lighting.LightUpdatesOptimizer;
import com.nebula.lighting.LightingOptimizer;
import com.nebula.logic.LogicOptimizer;
import com.nebula.mobile.MobileTouchControls;
import com.nebula.network.AntiGhostBlock;
import com.nebula.network.NetworkOptimizer;
import com.nebula.optimizer.AutoOptimizer;
import com.nebula.particles.ParticleKiller;
import com.nebula.profiles.GameModeProfiles;
import com.nebula.visual.FireOverlayOpacity;
import com.nebula.visual.PumpkinOverlayDisabler;
import com.nebula.visual.VignetteDisabler;
import com.nebula.world.SmoothWorldLoading;
import com.nebula.zoom.ZoomFeature;

public class NebulaClient implements ClientModInitializer {

    public static NebulaRenderer renderer;

    @Override
    public void onInitializeClient() {
        System.out.println("[Nebula] Cliente inicializando...");

        // ===== INICIALIZA CONFIGURAÇÕES =====
        NebulaConfig.init();
        NebulaAPI.getInstance().initialize();
        com.nebula.shaders.ShaderManager.getInstance().initialize();
        ProfileAPI.detectAndApply();

        // ===== SISTEMA CENTRAL DE MÓDULOS =====
        // Antes, cada módulo novo (Sprint Optimizer, Battery Saver, Smart
        // RAM Cleaner, etc.) tinha código real e completo, mas nunca era
        // chamado de lugar nenhum — ficavam "órfãos". Todos os 65 módulos
        // abaixo já implementam a interface NebulaModule (getName/initialize/
        // tick/shutdown/isEnabled), então basta registrá-los aqui UMA VEZ.
        // O ModuleManager cuida de inicializar e tickar só os que estiverem
        // habilitados (e tudo respeita o interruptor mestre NebulaConfig.
        // enableNebula, verificado dentro de isEnabled() de cada um).
        ModuleManager mm = NebulaEngine.getInstance().getModuleManager();
        mm.register(AFKDetector.getInstance());
        mm.register(AdaptiveEntityLOD.getInstance());
        mm.register(AntiGhostBlock.getInstance());
        mm.register(AudioOptimizer.getInstance());
        mm.register(AutoOptimizer.getInstance());
        mm.register(AutoTuner.getInstance());
        mm.register(BatterySaver.getInstance());
        mm.register(BottleneckDetector.getInstance());
        mm.register(ChunkLoaderOptimizer.getInstance());
        mm.register(CoordinatesDisplay.getInstance());
        mm.register(DayNightOptimizer.getInstance());
        mm.register(DimensionOptimizer.getInstance());
        mm.register(DynamicChunkLoader.getInstance());
        mm.register(DynamicShaders.getInstance());
        mm.register(EntityCleanup.getInstance());
        mm.register(EntityLOD.getInstance());
        mm.register(FPSBoostMode.getInstance());
        mm.register(FarEntitySimplifier.getInstance());
        mm.register(FasterBlockBreaking.getInstance());
        mm.register(FireOverlayOpacity.getInstance());
        mm.register(FullbrightToggle.getInstance());
        mm.register(GameModeProfiles.getInstance());
        mm.register(GameStateCompressor.getInstance());
        mm.register(GenericModAdapter.getInstance());
        mm.register(LaunchAccelerator.getInstance());
        mm.register(LightUpdatesOptimizer.getInstance());
        mm.register(LightingOptimizer.getInstance());
        mm.register(LogicOptimizer.getInstance());
        mm.register(MemoryManager.getInstance());
        mm.register(MobileTouchControls.getInstance());
        mm.register(ModCompatibility.getInstance());
        mm.register(NetworkOptimizer.getInstance());
        mm.register(NeuralFramePredictor.getInstance());
        mm.register(NightVisionOptimizer.getInstance());
        mm.register(ParticleKiller.getInstance());
        mm.register(PixelmonAdapter.getInstance());
        mm.register(PumpkinOverlayDisabler.getInstance());
        mm.register(ReduceShakeBobbing.getInstance());
        mm.register(ReducedCollisions.getInstance());
        mm.register(ResolutionModule.getInstance());
        mm.register(SelectiveCulling.getInstance());
        mm.register(SmartChunkLoader.getInstance());
        mm.register(SmartCleanup.getInstance());
        mm.register(SmartResourcePreloader.getInstance());
        mm.register(SmartSleepMode.getInstance());
        mm.register(SmoothFPS.getInstance());
        mm.register(SmoothWorldLoading.getInstance());
        mm.register(SprintOptimizer.getInstance());
        mm.register(StateBasedRenderer.getInstance());
        mm.register(TextureStreamingModule.getInstance());
        mm.register(TextureUpscaler.getInstance());
        mm.register(ThreadPoolManager.getInstance());
        mm.register(TickSchedulingModule.getInstance());
        mm.register(VignetteDisabler.getInstance());
        mm.register(ZoomFeature.getInstance());
        mm.register(SmartRedstoneEngine.getInstance());
        mm.register(FallingBlockBatcher.getInstance());
        mm.register(HopperOptimizer.getInstance());
        mm.register(DroppedItemOptimizer.getInstance());
        mm.register(CloudOptimizer.getInstance());
        mm.register(ThermalCuller.getInstance());
        mm.register(AnimationThrottler.getInstance());

        new SmartAutoTuner().initialize();

        NebulaEngine.getInstance().initialize();

        // ===== INICIALIZA RENDERER =====
        renderer = new NebulaRenderer();

        // ===== REGISTRA O TICK =====
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) return;

                    // ESCUDO ANTI-CRASH: Celulares não suportam o modo "Fabulous".
            // Se o jogo ou outro mod tentar ativar o Fabulous, forçamos o Fast para evitar crash.
            if (client.options.getGraphicsMode().getValue() == GraphicsMode.FABULOUS) {
                client.options.getGraphicsMode().setValue(GraphicsMode.FAST);
            }

            // Orçamento de entidades por frame (ver com.nebula.lib.api.BudgetAPI)
            BudgetAPI.beginFrame();

            // Focus Mode: atualiza se o player está parado/olhando
            FocusAPI.updatePlayerState(
                client.player.getX(),
                client.player.getY(),
                client.player.getZ(),
                client.player.getYaw()
            );

            // Densidade adaptativa (baseada no FPS médio)
            PerformanceAPI.updateAdaptiveDensity();

            // Conta entidades
            int total = 0;
            for (var ignored : client.world.getEntities()) {
                total++;
            }
            PerformanceAPI.setEntityCount(total);

            // ===== TICK CENTRAL: todos os 65 módulos registrados acima =====
            NebulaEngine.getInstance().tick();

            // ===== ATUALIZA MONITORES E OTIMIZADORES =====
            PerformanceMonitor.getInstance().tick();
            AdaptiveOptimizer.getInstance().onTick();

            if (renderer != null) {
                renderer.update();
            }

            PerformanceAPI.onFrame();
        });

        // Libera os módulos e o thread pool de build ao sair do jogo.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            NebulaEngine.getInstance().shutdown();
            if (renderer != null) {
                renderer.shutdown();
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            NebulaDebug.render(context);
        });

        System.out.println("[Nebula] ✅ Cliente carregado! " + mm.getModules().size() + " módulos registrados.");
        System.out.println("[Nebula] Profile: " + ProfileAPI.getCurrentProfile().getDisplayName());
    }
}
