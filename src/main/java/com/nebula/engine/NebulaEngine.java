package com.nebula.engine;

public class NebulaEngine {

    private static NebulaEngine INSTANCE;

    private final ModuleManager moduleManager;

    private NebulaEngine() {
        this.moduleManager = new ModuleManager();
    }

    public static NebulaEngine getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new NebulaEngine();
        }

        return INSTANCE;
    }

    public void initialize() {

        System.out.println("[Nebula] ===============================");
        System.out.println("[Nebula] Starting Nebula Engine...");
        System.out.println("[Nebula] ===============================");

        moduleManager.initializeModules();

        System.out.println("[Nebula] Engine Ready!");
    }

    public void tick() {
        moduleManager.tickModules();
    }

    public void shutdown() {
        moduleManager.shutdownModules();
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
