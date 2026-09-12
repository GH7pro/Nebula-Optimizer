package com.nebula.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {

    private final List<NebulaModule> modules = new ArrayList<>();

    public void register(NebulaModule module) {
        modules.add(module);
    }

    public void initializeModules() {
        for (NebulaModule module : modules) {
            if (module.isEnabled()) {
                module.initialize();
            }
        }
    }

    public void tickModules() {
        for (NebulaModule module : modules) {
            if (module.isEnabled()) {
                module.tick();
            }
        }
    }

    public void shutdownModules() {
        for (NebulaModule module : modules) {
            module.shutdown();
        }
    }

    public List<NebulaModule> getModules() {
        return Collections.unmodifiableList(modules);
    }
}
