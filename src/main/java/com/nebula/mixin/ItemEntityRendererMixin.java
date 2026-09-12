package com.nebula.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    // Injeta nosso código no momento exato em que o Minecraft vai desenhar um item no chão
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void nebula$cullFarItems(ItemEntity entity, float x, float y, float z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Se o jogador estiver no jogo
        if (client.player != null) {
            // Calcula a distância do jogador até o item ao quadrado (é mais rápido que calcular a raiz)
            double distanceSq = entity.squaredDistanceTo(client.player);
            
            // Se o item estiver a mais de 15 blocos de distância (15^2 = 225)
            if (distanceSq > 225.0) {
                // Cancela o desenho! O Minecraft não perde tempo renderizando isso.
                ci.cancel();
            }
        }
    }
}
