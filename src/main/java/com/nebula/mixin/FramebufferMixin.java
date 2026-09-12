package com.nebula.mixin;

import net.minecraft.client.gl.Framebuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Framebuffer.class)
public class FramebufferMixin {
    
    @Inject(method = "beginRead", at = @At("HEAD"))
    private void onBeginRead(CallbackInfo ci) {
        // RESOLVIDO: Desativado para não distorcer a tela do Minecraft.
        // A resolução dinâmica requer um sistema de Render Targets customizado,
        // mudar o framebuffer principal direto quebra a matriz de projeção.
        // ResolutionModule.getInstance().tick();
    }
}
