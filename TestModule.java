package com.nebula.engine.modules;

import com.nebula.engine.NebulaModule;

public class TestModule implements NebulaModule {

    @Override
    public String getName() {
        return "Test Module";
    }

    @Override
    public void initialize() {
        System.out.println("[Nebula] Test Module initialized!");
    }

    @Override
    public void tick() {

    }

    @Override
    public void shutdown() {
        System.out.println("[Nebula] Test Module shutdown!");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

