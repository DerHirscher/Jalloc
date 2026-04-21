package com.laxer.jalloc;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public abstract class ByteHandle implements AutoCloseable {
    protected final MemorySegment mem;
    private final Arena arena = Arena.ofConfined();

    private long current = 0;

    protected ByteHandle(long capacity, long alignment) {
        mem = arena.allocate(capacity, alignment);
    }

    protected final long nextBytes(long bytes) {
        if (current + bytes >= mem.byteSize()) {
            throw new OutOfMemoryException("The device has run out of free memory. Please increase the capacity.");
        }
        long ptr = current;
        current += bytes;
        return ptr;
    }

    protected final void cpyBytes(long ptr, long dest, long bytes) {
        long srcOff = ptr * bytes;
        long destOff = dest * bytes;
        MemorySegment.copy(mem, srcOff, mem, destOff, bytes);
    }

    protected final long cpyBytes(long ptr, long bytes) {
        long dest = nextBytes(bytes);
        cpyBytes(ptr, dest, bytes);
        return dest;
    }

    public abstract void cpy(long ptr, long dest);

    public abstract long cpy(long ptr);

    public void clear() {
        current = 0;
    }

    @Override
    public void close() {
        clear();
        arena.close();
    }
}
