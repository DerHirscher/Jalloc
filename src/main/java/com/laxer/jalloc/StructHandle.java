package com.laxer.jalloc;

import org.jetbrains.annotations.NotNull;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;

public abstract class StructHandle implements AutoCloseable {
    protected final MemorySegment mem;
    private final Arena arena = Arena.ofConfined();
    private final long capacity;
    private final long layoutSize;

    private long current = 0;

    protected StructHandle(MemoryLayout layout, long capacity) {
        this.mem = arena.allocate(layout, capacity);
        this.capacity = capacity;
        this.layoutSize = layout.byteSize();
    }

    protected long next() {
        if (current == capacity) {
            throw new OutOfMemoryException("The device has run out of free memory. Please increase the capacity.");
        }
        current++;
        return current - 1;
    }

    public void cpy(long obj, long dest) {
        long srcOff = obj * layoutSize;
        long destOff = dest * layoutSize;

        MemorySegment.copy(mem, srcOff, mem, destOff, layoutSize);
    }

    public long cpy(long obj) {
        long dest = next();
        cpy(obj, dest);
        return dest;
    }

    protected void setFloat(@NotNull VarHandle handle, long obj, float v) {
        handle.set(mem, obj * layoutSize, v);
    }

    protected Object get(@NotNull VarHandle handle, long obj) {
        return handle.get(mem, obj * layoutSize);
    }

    public void clear() {
        current = 0;
    }

    @Override
    public void close() {
        clear();
        arena.close();
    }
}