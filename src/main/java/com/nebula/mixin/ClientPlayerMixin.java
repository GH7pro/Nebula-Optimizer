package com.nebula.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin {
    private int packetTickCounter = 0;

    // Injeta no método que envia sua posição para o servidor
    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void nebula$throttleNetworkPackets(CallbackInfo ci) {
        packetTickCounter++;
        // O vanilla envia pacotes a cada tick. Nós enviamos a cada 2 ticks.
        // Isso reduz o tráfego de rede em 50%, diminuindo o lag e melhorando o ping!
        if (packetTickCounter % 2 != 0) {
            ci.cancel();
        }
    }
}
