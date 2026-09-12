package com.nebula;

import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;

public class NebulaMesh {

    private static final int VERTEX_SIZE = 28; // x,y,z,color,u,v,light
    private ByteBuffer buffer;
    private int vertexCount = 0;
    private int capacity;

    public NebulaMesh() {
        this.capacity = 16384;
        this.buffer = MemoryUtil.memAlloc(capacity * VERTEX_SIZE);
    }

    public void addVertex(float x, float y, float z, int color, float u, float v, int light) {
        if (vertexCount >= capacity) {
            resize();
        }

        long ptr = MemoryUtil.memAddress(buffer) + (long) vertexCount * VERTEX_SIZE;

        MemoryUtil.memPutFloat(ptr, x);
        MemoryUtil.memPutFloat(ptr + 4, y);
        MemoryUtil.memPutFloat(ptr + 8, z);
        MemoryUtil.memPutInt(ptr + 12, color);
        MemoryUtil.memPutFloat(ptr + 16, u);
        MemoryUtil.memPutFloat(ptr + 20, v);
        MemoryUtil.memPutInt(ptr + 24, light);

        vertexCount++;
    }

    public void addQuad(float x1, float y1, float z1,
                        float x2, float y2, float z2,
                        float x3, float y3, float z3,
                        float x4, float y4, float z4,
                        int color, int light) {
        // Dois triângulos formando um quad
        addVertex(x1, y1, z1, color, 0, 0, light);
        addVertex(x2, y2, z2, color, 1, 0, light);
        addVertex(x3, y3, z3, color, 1, 1, light);

        addVertex(x1, y1, z1, color, 0, 0, light);
        addVertex(x3, y3, z3, color, 1, 1, light);
        addVertex(x4, y4, z4, color, 0, 1, light);
    }

    private void resize() {
        int newCapacity = capacity * 2;
        ByteBuffer newBuffer = MemoryUtil.memAlloc(newCapacity * VERTEX_SIZE);
        MemoryUtil.memCopy(buffer, newBuffer);
        MemoryUtil.memFree(buffer);
        buffer = newBuffer;
        capacity = newCapacity;
    }

    public void finishBuilding() {
        if (buffer != null) {
            buffer.limit(vertexCount * VERTEX_SIZE);
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void draw() {
        // Placeholder - em uma versão completa aqui entraria o upload para GPU
    }

    public void clear() {
        vertexCount = 0;
        if (buffer != null) buffer.clear();
    }

    public void delete() {
        if (buffer != null) {
            MemoryUtil.memFree(buffer);
            buffer = null;
        }
    }
}
