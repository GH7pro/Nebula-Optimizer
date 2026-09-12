package com.nebula.shaders;

import com.nebula.mixin.GameRendererAccessor;
import com.nebula.shaders.presets.ShaderPresets;
import com.nebula.shaders.generator.ShaderPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Aplica de verdade os presets de pós-processamento do Nebula, usando o
 * PRÓPRIO sistema de shader do Minecraft (o mesmo mecanismo que deixa a
 * tela verde ao espectrar um creeper, ou treme com Náusea —
 * client.gameRenderer.loadPostProcessor / disablePostProcessor).
 *
 * Antes, enableShader() só marcava uma variável como "ativa" e imprimia uma
 * mensagem no console — nada mudava na tela de verdade. Os 5 presets
 * (Default/Vibrant/Retro/Cinematic/Night Vision) que já existiam em
 * ShaderPresets.java geram o mesmo GLSL que está empacotado em
 * assets/nebula/shaders/program/nebula_colorgrade.vsh/.fsh — cada preset é
 * só um .json pequeno com valores diferentes pra esse mesmo shader.
 */
public class ShaderManager {
    private static final ShaderManager INSTANCE = new ShaderManager();

    private final List<ShaderPreset> presets = new ArrayList<>();
    private int activeIndex = 0; // 0 = "Default (sem efeito)"
    private boolean lastLoadFailed = false;
    private String lastError = null;

    private ShaderManager() {
    }

    public static ShaderManager getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        presets.clear();
        for (ShaderPreset p : ShaderPresets.getAllPresets()) {
            // "Default" não precisa de um .json próprio: o índice 0 já
            // desliga o pós-processamento direto (mais barato que rodar um
            // shader que não muda nada).
            if (!p.getName().equalsIgnoreCase("Default")) {
                presets.add(p);
            }
        }
        System.out.println("🎨 [Nebula] Shader Manager inicializado com " + presets.size() + " presets.");
    }

    public List<ShaderPreset> getPresets() {
        return presets;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public String getActiveName() {
        if (activeIndex <= 0 || activeIndex > presets.size()) return "Default (No Shader)";
        return presets.get(activeIndex - 1).getName();
    }

    public boolean isShaderEnabled() {
        return activeIndex > 0;
    }

    public boolean didLastLoadFail() {
        return lastLoadFailed;
    }

    public String getLastError() {
        return lastError;
    }

    /** index 0 = desliga (Default). 1..N = presets.getPresets().get(index-1). */
    public boolean applyByIndex(int index) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.gameRenderer == null) return false;

        if (index <= 0) {
            ((GameRendererAccessor) client.gameRenderer).nebula$disablePostProcessor();
            activeIndex = 0;
            lastLoadFailed = false;
            lastError = null;
            System.out.println("🎨 [Nebula] Shader desativado.");
            return true;
        }

        if (index > presets.size()) return false;
        ShaderPreset preset = presets.get(index - 1);
        String resourceName = presetResourceName(preset.getName());

        try {
            GameRendererAccessor accessor = (GameRendererAccessor) client.gameRenderer;
            accessor.nebula$disablePostProcessor();
            accessor.nebula$loadPostProcessor(new Identifier("nebula", "shaders/post/" + resourceName + ".json"));
            activeIndex = index;
            lastLoadFailed = false;
            lastError = null;
            System.out.println("🎨 [Nebula] Shader aplicado: " + preset.getName());
            return true;
        } catch (Exception e) {
            lastLoadFailed = true;
            lastError = e.getMessage();
            System.err.println("🎨 [Nebula] Falha ao carregar shader '" + preset.getName() + "': " + e);
            // Volta pro estado seguro (sem shader) em vez de deixar a tela quebrada.
            try {
                ((GameRendererAccessor) client.gameRenderer).nebula$disablePostProcessor();
            } catch (Exception ignored) {
            }
            activeIndex = 0;
            return false;
        }
    }

    public void disableShader() {
        applyByIndex(0);
    }

    /** "Vibrant" -> "nebula_vibrant", "Default" -> não usado (índice 0 desliga direto) */
    private String presetResourceName(String presetName) {
        String slug = presetName.toLowerCase().replace(" ", "");
        return "nebula_" + slug;
    }
}
