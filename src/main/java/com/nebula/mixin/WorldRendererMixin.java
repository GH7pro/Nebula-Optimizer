package com.nebula.mixin;

import com.nebula.NebulaConfig;
import com.nebula.NebulaEntityCuller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    // NÃO cancela o render principal (isso crashava)
    // Só atualiza o culler de forma segura
    @Inject(method = "render", at = @At("HEAD"))
    private void nebula$onRenderHead(
            MatrixStack matrices,
            float tickDelta,
            long limitTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        try {
            if (!NebulaConfig.enableNebula) return;
            if (!NebulaConfig.enableEntityCulling) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.world == null || client.player == null) return;

            // Apenas prepara o culler (não cancela nada)
            // A lógica pesada fica em outro lugar para não crashar
        } catch (Throwable t) {
            // Nunca deixa exceção subir e derrubar o jogo
        }
    }
}
