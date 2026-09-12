package com.nebula.mixin;

import com.nebula.NebulaConfig;
import com.nebula.lib.api.CullingAPI;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At("HEAD"), cancellable = true)
    private void nebula$cullBlockEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrices,
                                        VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        try {
            if (!NebulaConfig.enableNebula) return;
            if (!NebulaConfig.enableBlockEntityCulling) return;
            if (!CullingAPI.isBlockCullingEnabled()) return;
            if (blockEntity == null) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.player == null) return;

            BlockPos pos = blockEntity.getPos();
            Vec3d playerPos = client.player.getPos();

            double dx = pos.getX() + 0.5 - playerPos.x;
            double dy = pos.getY() + 0.5 - playerPos.y;
            double dz = pos.getZ() + 0.5 - playerPos.z;
            double distSq = dx * dx + dy * dy + dz * dz;

            double max = CullingAPI.getBlockCullingDistance() * 16.0;
            if (distSq > max * max) {
                ci.cancel();
            }
        } catch (Throwable ignored) {
        }
    }
}
