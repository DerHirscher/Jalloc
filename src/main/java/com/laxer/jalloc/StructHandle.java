package com.laxer.jalloc;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.VarHandle;

public abstract class StructHandle extends ByteHandle {
    private final long structSize;

    protected StructHandle(long structSize, long capacity, long alignment) {
        super(capacity * structSize, alignment);
        this.structSize = structSize;
    }

    protected long next() {
        return nextBytes(structSize);
    }

    @Override
    public void cpy(long obj, long dest) {
        cpyBytes(obj * structSize, dest * structSize, structSize);
    }

    @Override
    public long cpy(long obj) {
        return cpyBytes(obj * structSize, structSize);
    }

    @Override
    public boolean equals(long obj1, long obj2) {
        return equalsBytes(obj1 * structSize, obj2 * structSize, structSize);
    }

    protected void setFloat(@NotNull VarHandle handle, long obj, float v) {
        handle.set(mem, obj * structSize, v);
    }

    protected float getFloat(@NotNull VarHandle handle, long obj) {
        return (float) handle.get(mem, obj * structSize);
    }
}
