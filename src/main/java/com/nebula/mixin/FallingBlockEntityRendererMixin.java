package com.nebula.mixin;

import com.nebula.AdaptiveOptimizer;
import com.nebula.NebulaConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntityRenderer.class)
public class FallingBlockEntityRendererMixin {

    /**
     * Intercepta a renderização dos blocos que estão caindo (areia, cascalho, bigornas).
     * Se o AdaptiveOptimizer estiver em Modo Pânico ou Nível 3+, cancelamos o desenho 3D do bloco.
     * A entidade continua existindo e caindo fisicamente, mas não é desenhada na tela, salvando muito FPS.
     */
    @Inject(method = "render(Lnet/minecraft/entity/FallingBlockEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", 
            at = @At("HEAD"), 
            cancellable = true)
    private void nebula$cancelFallingBlockRender(FallingBlockEntity fallingBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (NebulaConfig.enableNebula) {
            AdaptiveOptimizer optimizer = AdaptiveOptimizer.getInstance();
            
            // Se estiver no Modo Pânico ou no Nível 3+ de otimização, corta a renderização!
            if (optimizer != null && (optimizer.isInPanicMode() || optimizer.getCurrentOptimizationLevel() >= 3)) {
                ci.cancel();
            }
        }
    }
}
