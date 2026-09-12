package com.nebula.mixin;

import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleMixin {
    private int tickCounter = 0;

    // Injeta no método que atualiza a física das partículas
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void nebula$throttleParticles(CallbackInfo ci) {
        tickCounter++;
        
        // Atualiza as partículas apenas a cada 2 ticks (metade da velocidade)
        // Isso corta o trabalho do processador pela metade!
        if (tickCounter % 2 == 0) {
            ci.cancel(); 
        }
    }
}
