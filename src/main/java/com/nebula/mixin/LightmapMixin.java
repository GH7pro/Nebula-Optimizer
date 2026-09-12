package com.nebula.mixin;

import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapMixin {
    private int tickCounter = 0;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void nebula$throttleLightmap(float delta, CallbackInfo ci) {
        tickCounter++;
        
        // Atualiza a iluminação a cada 5 ticks (4 vezes por segundo)
        // Em vez de atualizar 60 vezes por segundo (60 FPS)
        if (tickCounter < 5) {
            ci.cancel(); // Cancela a atualização pesada da luz
        } else {
            tickCounter = 0; // Deixa o Minecraft atualizar a luz nesse frame
        }
    }
}
