package com.laxer.jalloc;

import com.laxer.jalloc.util.OutOfMemoryException;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public abstract class ByteHandle implements AutoCloseable {
    public static final long NULL = -1;

    protected final MemorySegment mem;
    private final Arena arena = Arena.ofConfined();

    private long current = 0;

    protected ByteHandle(long capacity, long alignment) {
        mem = arena.allocate(capacity, alignment);
    }

    protected final long nextBytes(long bytes) {
        if (bytes <= 0) {
            throw new IllegalArgumentException("You can not allocate a negative amount of bytes.");
        }

        if (current + bytes > mem.byteSize()) {
            throw new OutOfMemoryException("The device has run out of free memory. Please increase the capacity.");
        }
        long ptr = current;
        current += bytes;
        return ptr;
    }

    protected final void cpyBytes(long ptr, long dest, long bytes) {
        MemorySegment.copy(mem, ptr, mem, dest, bytes);
    }

    protected final long cpyBytes(long ptr, long bytes) {
        long dest = nextBytes(bytes);
        cpyBytes(ptr, dest, bytes);
        return dest;
    }

    public abstract void cpy(long ptr, long dest);

    public abstract long cpy(long ptr);

    protected final boolean equalsBytes(long ptr1, long ptr2, long bytes) {
        return ptr1 == ptr2 || MemorySegment.mismatch(
                mem, ptr1, ptr1 + bytes,
                mem, ptr2, ptr2 + bytes
        ) == -1;
    }

    public abstract boolean equals(long ptr1, long ptr2);

    public void clear() {
        current = 0;
    }

    @Override
    public void close() {
        clear();
        arena.close();
    }

    public static long requireNonNull(long ptr) {
        if (ptr == NULL) throw new NullPointerException("The pointer is NULL");
        return ptr;
    }
}
