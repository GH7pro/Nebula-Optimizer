package com.nebula.mixin;

import com.nebula.engine.modules.ThermalCuller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityCullerMixin {

    /**
     * Intercepta o "tick" de IA dos mobs. Se o ThermalCuller estiver ativo
     * e o mob estiver a mais de 24 blocos do jogador, cancelamos o tick.
     * O mob fica paradinho, economizando CPU e evitando que o celular esquente.
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nebula$freezeMobAi(CallbackInfo ci) {
        if (ThermalCuller.shouldFreezeEntities) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                // Converte a classe para Entity para poder medir a distância
                Entity mob = (Entity) (Object) this;
                
                // Se o mob estiver longe do jogador, congela!
                if (mob.distanceTo(client.player) > 24.0f) {
                    ci.cancel();
                }
            }
        }
    }
}
