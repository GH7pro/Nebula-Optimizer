package com.nebula.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * GameRenderer#loadPostProcessor(Identifier) e #disablePostProcessor()
 * existem no vanilla mas não são public — só acessíveis dentro do próprio
 * pacote do Minecraft. Um Mixin @Invoker cria uma "ponte" pública pra esses
 * métodos, sem precisar reescrever nada do GameRenderer.
 */
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Invoker("loadPostProcessor")
    void nebula$loadPostProcessor(Identifier id);

    @Invoker("disablePostProcessor")
    void nebula$disablePostProcessor();
}
