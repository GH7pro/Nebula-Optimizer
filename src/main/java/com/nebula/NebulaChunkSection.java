package com.nebula;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NebulaChunkSection {

    private static final ExecutorService BUILD_POOL = Executors.newFixedThreadPool(2);
    
    private final Chunk chunk;
    private volatile boolean dirty = true;
    private NebulaMesh mesh;
    private long lastBuildTime = 0;

    public NebulaChunkSection(Chunk chunk) {
        this.chunk = chunk;
    }

    public boolean needsRebuild() {
        return dirty;
    }

    public void rebuildAsync() {
        long now = System.currentTimeMillis();
        if (now - lastBuildTime < 100) return;

        lastBuildTime = now;
        CompletableFuture.runAsync(this::rebuildGreedy, BUILD_POOL);
    }

    private void rebuildGreedy() {
        NebulaMesh newMesh = new NebulaMesh();

        try {
            for (Direction dir : Direction.values()) {
                greedyMeshDirection(newMesh, dir);
            }
        } catch (Exception ignored) {}

        newMesh.finishBuilding();
        this.mesh = newMesh;
        this.dirty = false;
    }

    private void greedyMeshDirection(NebulaMesh mesh, Direction dir) {
        boolean[][] mask = new boolean[16][16];

        for (int slice = 0; slice < 16; slice++) {
            for (int u = 0; u < 16; u++) {
                for (int v = 0; v < 16; v++) {
                    BlockPos pos = getPos(dir, slice, u, v);
                    BlockState state = chunk.getBlockState(pos);

                    if (state.isAir() || !state.isOpaque()) {
                        mask[u][v] = false;
                        continue;
                    }

                    BlockPos neighbor = pos.offset(dir);
                    BlockState neighborState = chunk.getBlockState(neighbor);
                    mask[u][v] = neighborState.isAir() || !neighborState.isOpaque();
                }
            }

            for (int u = 0; u < 16; u++) {
                for (int v = 0; v < 16; ) {
                    if (!mask[u][v]) {
                        v++;
                        continue;
                    }

                    int width = 1;
                    while (u + width < 16 && mask[u + width][v]) {
                        width++;
                    }

                    int height = 1;
                    boolean done = false;
                    while (v + height < 16 && !done) {
                        for (int k = 0; k < width; k++) {
                            if (!mask[u + k][v + height]) {
                                done = true;
                                break;
                            }
                        }
                        if (!done) height++;
                    }

                    addMergedFace(mesh, dir, slice, u, v, width, height);

                    for (int dx = 0; dx < width; dx++) {
                        for (int dy = 0; dy < height; dy++) {
                            mask[u + dx][v + dy] = false;
                        }
                    }

                    v += height;
                }
            }
        }
    }

    private BlockPos getPos(Direction dir, int slice, int u, int v) {
        return switch (dir.getAxis()) {
            case X -> new BlockPos(slice, u, v);
            case Y -> new BlockPos(u, slice, v);
            case Z -> new BlockPos(u, v, slice);
        };
    }

    private void addMergedFace(NebulaMesh mesh, Direction dir, int slice, int u, int v, int width, int height) {
        int color = 0xFFFFFFFF;
        int light = 0x00F000F0;

        float x = u;
        float y = v;
        float w = width;
        float h = height;

        switch (dir) {
            case UP -> mesh.addQuad(x, slice+1, y, x+w, slice+1, y, x+w, slice+1, y+h, x, slice+1, y+h, color, light);
            case DOWN -> mesh.addQuad(x, slice, y+h, x+w, slice, y+h, x+w, slice, y, x, slice, y, color, light);
            case NORTH -> mesh.addQuad(x+w, y, slice, x, y, slice, x, y+h, slice, x+w, y+h, slice, color, light);
            case SOUTH -> mesh.addQuad(x, y, slice+1, x+w, y, slice+1, x+w, y+h, slice+1, x, y+h, slice+1, color, light);
            case WEST -> mesh.addQuad(slice, y, x+w, slice, y, x, slice, y+h, x, slice, y+h, x+w, color, light);
            case EAST -> mesh.addQuad(slice+1, y, x, slice+1, y, x+w, slice+1, y+h, x+w, slice+1, y+h, x, color, light);
        }
    }

    public void render() {
        if (mesh != null) mesh.draw();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void delete() {
        if (mesh != null) {
            mesh.delete();
            mesh = null;
        }
    }
}
