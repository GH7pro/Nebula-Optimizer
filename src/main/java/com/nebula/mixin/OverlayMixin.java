package com.nebula.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class OverlayMixin {

    // Colocando o descritor exato (DrawContext, Entity) para o Mixin achar o método na 1.20.1
    @Inject(method = "renderVignette(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void nebula$removeVignette(DrawContext context, Entity entity, CallbackInfo ci) {
        ci.cancel();
    }
}
