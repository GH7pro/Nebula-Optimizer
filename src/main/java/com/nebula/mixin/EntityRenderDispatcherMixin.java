package com.nebula.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void nebula$cullFarEntities(E entity, double x, double y, double z, float tickDelta, float cameraYaw, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Nunca esconde o próprio jogador
        if (entity == client.player) return;

        if (client.player != null) {
            double distanceSq = entity.squaredDistanceTo(client.player);
            // Não desenha nenhuma entidade a mais de 25 blocos de distância
            if (distanceSq > 625.0) {
                ci.cancel();
            }
        }
    }
}
