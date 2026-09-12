package com.nebula.mixin;

import com.nebula.gui.NebulaVideoSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoOptionsScreen.class)
public class VideoSettingsScreenMixin {

    // Guarda a tela REAL que pediu pra abrir o menu de vídeo (normalmente a
    // tela de "Options"). Precisa ser capturada aqui, no construtor — porque
    // quando init() roda, client.currentScreen já foi trocado pra ESTA
    // VideoOptionsScreen, então usar client.currentScreen como "parent"
    // (como era feito antes) apontava pra ela mesma, não pra tela anterior.
    // Isso fazia o botão "Done" não voltar pro lugar certo.
    @Unique
    private Screen nebula$realParent;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void nebula$captureParent(Screen parent, GameOptions gameOptions, CallbackInfo ci) {
        this.nebula$realParent = parent;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            GameOptions options = client.options;
            client.setScreen(new NebulaVideoSettingsScreen(this.nebula$realParent, options));
            ci.cancel();
        }
    }
}
