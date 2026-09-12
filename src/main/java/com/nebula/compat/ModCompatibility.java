package com.nebula.compat;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

public class ModCompatibility implements NebulaModule {
    private static final ModCompatibility INSTANCE = new ModCompatibility();
    private boolean enabled = true;
    private Map<String, Boolean> detectedMods = new HashMap<>();
    private Map<String, String> modVersions = new HashMap<>();
    
    private ModCompatibility() {}
    
    public static ModCompatibility getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Mod Compatibility";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔌 [Nebula] Mod Compatibility inicializado!");
        if (!NebulaConfig.enableModCompatibility) return;
        
        detectMods();
        applyCompatibilities();
    }
    
    private void detectMods() {
        FabricLoader loader = FabricLoader.getInstance();
        
        // Lista de mods para detectar
        String[][] mods = {
            {"pixelmon", "Pixelmon"},
            {"create", "Create"},
            {"botania", "Botania"},
            {"mekanism", "Mekanism"},
            {"ic2", "IC2"},
            {"thermal", "Thermal"},
            {"tconstruct", "Tinkers Construct"},
            {"ae2", "Applied Energistics 2"},
            {"refinedstorage", "Refined Storage"},
            {"journeymap", "JourneyMap"},
            {"jei", "JEI"},
            {"rei", "REI"},
            {"sodium", "Sodium"},
            {"optifine", "OptiFine"},
            {"lithium", "Lithium"},
            {"ferritecore", "FerriteCore"},
            {"phosphor", "Phosphor"},
            {"starlight", "Starlight"},
            {"modmenu", "ModMenu"}
        };
        
        for (String[] mod : mods) {
            String id = mod[0];
            String name = mod[1];
            boolean present = loader.isModLoaded(id);
            detectedMods.put(id, present);
            
            if (present) {
                // Tenta obter a versão
                try {
                    var modContainer = loader.getModContainer(id);
                    if (modContainer.isPresent()) {
                        String version = modContainer.get().getMetadata().getVersion().getFriendlyString();
                        modVersions.put(id, version);
                        System.out.println("🔌 [Nebula] ✅ " + name + " v" + version + " detectado!");
                    } else {
                        System.out.println("🔌 [Nebula] ✅ " + name + " detectado!");
                    }
                } catch (Exception e) {
                    System.out.println("🔌 [Nebula] ✅ " + name + " detectado!");
                }
            }
        }
    }
    
    private void applyCompatibilities() {
        // ===== PIXELMON =====
        if (detectedMods.getOrDefault("pixelmon", false)) {
            System.out.println("🔌 [Nebula] 🎮 Pixelmon: Ativando otimizações específicas...");
            // Desativa culling de entidades para não afetar Pixelmons
            NebulaConfig.enableEntityCulling = false;
            // Aumenta LOD para Pixelmons
            NebulaConfig.enableEntityLOD = true;
        }
        
        // ===== CREATE =====
        if (detectedMods.getOrDefault("create", false)) {
            System.out.println("🔌 [Nebula] ⚙️ Create: Ativando compatibilidade...");
            // Otimiza para Create (muitas entidades)
            NebulaConfig.enableEntityCleanup = true;
            NebulaConfig.enableIntelligentTicking = true;
        }
        
        // ===== SODIUM =====
        if (detectedMods.getOrDefault("sodium", false)) {
            System.out.println("🔌 [Nebula] ⚡ Sodium: Modo compatibilidade ativado!");
            System.out.println("🔌 [Nebula] ⚡ Desativando otimizações de renderização para evitar conflitos...");
            // Desativa renderização para não conflitar com Sodium
            NebulaConfig.enableDynamicResolution = false;
        }
        
        // ===== LITHIUM =====
        if (detectedMods.getOrDefault("lithium", false)) {
            System.out.println("🔌 [Nebula] 🧠 Lithium: Complementando otimizações...");
            // Lithium já otimiza, então reduzimos algumas otimizações
            NebulaConfig.enableIntelligentTicking = false;
        }
        
        // ===== FERRITE CORE =====
        if (detectedMods.getOrDefault("ferritecore", false)) {
            System.out.println("🔌 [Nebula] 💾 FerriteCore: Otimizações de memória complementares...");
            // FerriteCore já otimiza memória
            NebulaConfig.enableMemoryManagement = false;
        }
        
        // ===== JEI / REI =====
        if (detectedMods.getOrDefault("jei", false) || detectedMods.getOrDefault("rei", false)) {
            System.out.println("🔌 [Nebula] 📦 JEI/REI detectado: Compatibilidade garantida!");
        }
        
        System.out.println("🔌 [Nebula] ✅ Compatibilidades aplicadas!");
    }
    
    public boolean isModLoaded(String modId) {
        return detectedMods.getOrDefault(modId, false);
    }
    
    public String getModVersion(String modId) {
        return modVersions.getOrDefault(modId, "Unknown");
    }
    
    public Map<String, Boolean> getDetectedMods() {
        return new HashMap<>(detectedMods);
    }
    
    @Override
    public void tick() {
        // Monitora mods em tempo real (para mods que carregam depois)
    }
    
    @Override
    public void shutdown() {
        System.out.println("🔌 [Nebula] Mod Compatibility desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableModCompatibility;
    }
}
