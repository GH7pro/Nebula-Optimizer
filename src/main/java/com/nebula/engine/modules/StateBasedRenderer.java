package com.nebula.engine.modules;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StateBasedRenderer implements NebulaModule {
    private static final StateBasedRenderer INSTANCE = new StateBasedRenderer();
    private boolean enabled = true;
    private Map<BlockPos, Integer> blockStates = new HashMap<>();
    private Set<BlockPos> changedBlocks = new HashSet<>();
    
    private StateBasedRenderer() {}
    
    public static StateBasedRenderer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "State-based Rendering";
    }
    
    @Override
    public void initialize() {
        System.out.println("🔄 [Nebula] State-based Renderer inicializado!");
    }
    
    @Override
    public void tick() {
        // O trabalho real seria feito nos Mixins
        // Rastreia mudanças de blocos e marca para re-renderizar
    }
    
    public void markDirty(BlockPos pos) {
        changedBlocks.add(pos);
    }
    
    public boolean needsRender(BlockPos pos) {
        return changedBlocks.contains(pos);
    }
    
    public void clearDirty() {
        changedBlocks.clear();
    }
    
    @Override
    public void shutdown() {
        System.out.println("🔄 [Nebula] State-based Renderer desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.get().enableStateBasedRendering;
    }
}
