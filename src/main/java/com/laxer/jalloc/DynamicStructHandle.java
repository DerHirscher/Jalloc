package com.laxer.jalloc;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public abstract class DynamicStructHandle implements AutoCloseable {
    protected final MemorySegment mem;
    private final Arena arena = Arena.ofConfined();
    private final long capacity;

    private long current = 0;

    protected DynamicStructHandle(long byteCapacity) {
        mem = arena.allocate(byteCapacity);
        this.capacity = byteCapacity;
    }

    protected long next(long bytes) {
        if (current + bytes >= capacity) {
            throw new OutOfMemoryException("The device has run out of free memory. Please increase the capacity.");
        }
        current += bytes;
        return current - bytes;
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
