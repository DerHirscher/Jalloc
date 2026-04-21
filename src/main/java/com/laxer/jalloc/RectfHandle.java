package com.laxer.jalloc;

import org.jetbrains.annotations.NotNull;

import java.lang.foreign.*;
import java.lang.foreign.StructLayout;
import java.lang.invoke.VarHandle;

public class RectfHandle extends StructHandle {
    private static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_FLOAT.withName("x"),
            ValueLayout.JAVA_FLOAT.withName("y"),
            ValueLayout.JAVA_FLOAT.withName("w"),
            ValueLayout.JAVA_FLOAT.withName("h")
    );

    private static final VarHandle X_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("x"));
    private static final VarHandle Y_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("y"));
    private static final VarHandle W_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("w"));
    private static final VarHandle H_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("h"));

    public RectfHandle(long capacity) {
        super(LAYOUT, capacity);
    }

    public long newRectf() {
        return newRectf(0, 0);
    }

    public long newRectf(float w, float h) {
        return newRectf(0, 0, w, h);
    }

    public long newRectf(float x, float y, float w, float h) {
        long rect = next();
        set(rect, x, y, w, h);
        return rect;
    }

    public void set(long rect, float x, float y, float w, float h) {
        setPos(rect, x, y);
        setDim(rect, w, h);
    }

    public void setPos(long rect, float x, float y) {
        setX(rect, x);
        setY(rect, y);
    }

    public void setDim(long rect, float w, float h) {
        setWidth(rect, w);
        setHeight(rect, h);
    }

    public void setX(long rect, float x) {
        setFloat(X_HANDLE, rect, x);
    }

    public void setY(long rect, float y) {
        setFloat(Y_HANDLE, rect, y);
    }

    public void setWidth(long rect, float w) {
        setFloat(W_HANDLE, rect, w);
    }

    public void setHeight(long rect, float h) {
        setFloat(H_HANDLE, rect, h);
    }

    public float getX(long rect) {
        return (float) get(X_HANDLE, rect);
    }

    public float getY(long rect) {
        return (float) get(Y_HANDLE, rect);
    }

    public float getWidth(long rect) {
        return (float) get(W_HANDLE, rect);
    }

    public float getHeight(long rect) {
        return (float) get(H_HANDLE, rect);
    }

    public float area(long rect) {
        return area(getX(rect), getY(rect));
    }

    public static float area(float w, float h) {
        return w * h;
    }

    public boolean contains(long rect, @NotNull Vec2fHandle handle, long vec) {
        return contains(rect, handle.getX(vec), handle.getY(vec));
    }

    public boolean contains(long rect, float x, float y) {
        return contains(getX(rect), getY(rect), getWidth(rect), getHeight(rect), x, y);
    }

    public static boolean contains(float rectX, float rectY, float rectWidth, float rectHeight, float pointX, float pointY) {
        return pointX >= rectX
                && pointX <= rectX + rectWidth
                && pointY >= rectY
                && pointY <= rectY + rectHeight;
    }
}