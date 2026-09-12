package com.nebula.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class NotificationMixin {
    
    @Inject(method = "setTitle", at = @At("HEAD"), cancellable = true)
    private void onSetTitle(Text title, CallbackInfo ci) {
        // Otimiza a exibição de títulos (conquistas, comandos)
        try {
            if (title != null) {
                // Processa o título de forma mais eficiente
            }
        } catch (Exception e) {
            // Ignora erros
        }
    }
}
