package com.nebula.mixin;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireMixin {

    /**
     * Intercepta o método que gera as partículas de redstone (randomDisplayTick).
     * Se o AdaptiveOptimizer estiver em Modo Pânico ou em nível alto de otimização,
     * cancelamos a geração dessas partículas para salvar o FPS do cliente.
     */
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nebula$cancelRedstoneParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (NebulaConfig.enableNebula) {
            AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
            
            // Se estiver no Modo Pânico ou no Nível 2+ de otimização, corta as partículas!
            if (optimizer != null && (optimizer.isInPanicMode() || optimizer.getCurrentOptimizationLevel() >= 2)) {
                ci.cancel();
            }
        }
    }
}
