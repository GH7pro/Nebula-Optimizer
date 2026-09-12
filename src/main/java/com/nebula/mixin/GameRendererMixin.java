package com.nebula.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Injeta no método que faz a tela tremer ao tomar dano
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void nebula$removeHurtShake(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        // Cancela a matemática pesada de rotação de câmera ao tomar dano
        ci.cancel();
    }
}
