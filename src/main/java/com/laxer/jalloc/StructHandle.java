package com.laxer.jalloc;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.VarHandle;

public abstract class StructHandle extends ByteHandle {
    private final long structSize;

    protected StructHandle(long structSize, long capacity, long alignment) {
        super(capacity * structSize, alignment);
        this.structSize = structSize;
    }

    protected long nextStruct() {
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

    protected void set(@NotNull VarHandle handle, long obj, boolean v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, byte v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, char v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, short v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, int v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, float v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, long v) {
        handle.set(mem, obj * structSize, v);
    }

    protected void set(@NotNull VarHandle handle, long obj, double v) {
        handle.set(mem, obj * structSize, v);
    }

    protected boolean getBoolean(@NotNull VarHandle handle, long obj) {
        return (boolean) handle.get(mem, obj * structSize);
    }

    protected byte getByte(@NotNull VarHandle handle, long obj) {
        return (byte) handle.get(mem, obj * structSize);
    }

    protected char getChar(@NotNull VarHandle handle, long obj) {
        return (char) handle.get(mem, obj * structSize);
    }

    protected short getShort(@NotNull VarHandle handle, long obj) {
        return (short) handle.get(mem, obj * structSize);
    }

    protected int getInt(@NotNull VarHandle handle, long obj) {
        return (int) handle.get(mem, obj * structSize);
    }

    protected float getFloat(@NotNull VarHandle handle, long obj) {
        return (float) handle.get(mem, obj * structSize);
    }

    protected long getLong(@NotNull VarHandle handle, long obj) {
        return (long) handle.get(mem, obj * structSize);
    }

    protected double getDouble(@NotNull VarHandle handle, long obj) {
        return (double) handle.get(mem, obj * structSize);
    }
}
