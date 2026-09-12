package com.nebula.compatibility;

import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

public class ModDetector {
    private static final ModDetector INSTANCE = new ModDetector();
    private Map<String, Boolean> modCache = new HashMap<>();
    private boolean initialized = false;
    
    // ===== LISTA DE MODS CONHECIDOS =====
    public static final String PIXELMON = "pixelmon";
    public static final String CREATE = "create";
    public static final String BOTANIA = "botania";
    public static final String MEKANISM = "mekanism";
    public static final String IC2 = "ic2";
    public static final String THERMAL = "thermal";
    public static final String IMMERSIVE = "immersiveengineering";
    public static final String TINKERS = "tconstruct";
    public static final String APPLIED_ENERGISTICS = "ae2";
    public static final String REFINED_STORAGE = "refinedstorage";
    public static final String JOURNEYMAP = "journeymap";
    public static final String JEI = "jei";
    public static final String REI = "rei";
    public static final String SODIUM = "sodium";
    public static final String OPTIFINE = "optifine";
    public static final String PHOSPHOR = "phosphor";
    public static final String LITHIUM = "lithium";
    public static final String FERRITECORE = "ferritecore";
    
    private ModDetector() {}
    
    public static ModDetector getInstance() {
        return INSTANCE;
    }
    
    public void initialize() {
        if (initialized) return;
        
        System.out.println("[Nebula] 🔍 Detectando mods...");
        
        // Detecta mods pelo Fabric Loader
        FabricLoader loader = FabricLoader.getInstance();
        
        // Mods populares
        checkMod(loader, PIXELMON, "pixelmon");
        checkMod(loader, CREATE, "create");
        checkMod(loader, BOTANIA, "botania");
        checkMod(loader, MEKANISM, "mekanism");
        checkMod(loader, IC2, "ic2");
        checkMod(loader, THERMAL, "thermal");
        checkMod(loader, IMMERSIVE, "immersiveengineering");
        checkMod(loader, TINKERS, "tconstruct");
        checkMod(loader, APPLIED_ENERGISTICS, "appliedenergistics2");
        checkMod(loader, REFINED_STORAGE, "refinedstorage");
        checkMod(loader, JOURNEYMAP, "journeymap");
        checkMod(loader, JEI, "jei");
        checkMod(loader, REI, "roughlyenoughitems");
        checkMod(loader, SODIUM, "sodium");
        checkMod(loader, OPTIFINE, "optifine");
        checkMod(loader, PHOSPHOR, "phosphor");
        checkMod(loader, LITHIUM, "lithium");
        checkMod(loader, FERRITECORE, "ferritecore");
        
        // ===== PIXELMON ESPECÍFICO =====
        // O Pixelmon pode ter IDs diferentes
        if (!modCache.containsKey(PIXELMON)) {
            // Tenta detectar pelo nome do pacote ou classes
            try {
                Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
                modCache.put(PIXELMON, true);
                System.out.println("  ✅ Pixelmon detectado (via classe)");
            } catch (ClassNotFoundException e) {
                // Não encontrou
            }
        }
        
        initialized = true;
        
        // ===== LOG DOS MODS DETECTADOS =====
        System.out.println("[Nebula] 📊 Mods detectados:");
        for (Map.Entry<String, Boolean> entry : modCache.entrySet()) {
            if (entry.getValue()) {
                System.out.println("  ✅ " + entry.getKey());
            }
        }
    }
    
    private void checkMod(FabricLoader loader, String modId, String modName) {
        boolean present = loader.isModLoaded(modName);
        modCache.put(modId, present);
        if (present) {
            System.out.println("  ✅ " + modId + " detectado");
        }
    }
    
    public boolean isModLoaded(String modId) {
        return modCache.getOrDefault(modId, false);
    }
    
    public boolean isPixelmonLoaded() {
        return isModLoaded(PIXELMON);
    }
    
    public boolean isCreateLoaded() {
        return isModLoaded(CREATE);
    }
    
    public boolean isBotaniaLoaded() {
        return isModLoaded(BOTANIA);
    }
    
    public boolean isMekanismLoaded() {
        return isModLoaded(MEKANISM);
    }
    
    public boolean isSodiumLoaded() {
        return isModLoaded(SODIUM);
    }
    
    public boolean isLithiumLoaded() {
        return isModLoaded(LITHIUM);
    }
    
    public boolean isFerriteCoreLoaded() {
        return isModLoaded(FERRITECORE);
    }
    
    public String getDetectedMods() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : modCache.entrySet()) {
            if (entry.getValue()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(entry.getKey());
            }
        }
        return sb.toString();
    }
}
