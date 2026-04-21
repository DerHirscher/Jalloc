package com.laxer.jalloc;

import java.lang.foreign.*;
import java.lang.foreign.StructLayout;
import java.lang.invoke.VarHandle;

public class Vec2fHandle extends StructHandle {
    private static final float EPSILON = 1e-10f;

    private static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_FLOAT.withName("x"),
            ValueLayout.JAVA_FLOAT.withName("y")
    );

    private static final VarHandle X_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("x"));
    private static final VarHandle Y_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("y"));

    public Vec2fHandle(long capacity) {
        super(LAYOUT, capacity);
    }

    public long newVec2f() {
        return newVec2f(0, 0);
    }

    public long newVec2f(float x, float y) {
        long vec = next();
        set(vec, x, y);
        return vec;
    }

    public void set(long vec, float x, float y) {
        setX(vec, x);
        setY(vec, y);
    }

    public void setX(long vec, float x) {
        setFloat(X_HANDLE, vec, x);
    }

    public void setY(long vec, float y) {
        setFloat(Y_HANDLE, vec, y);
    }

    public float getX(long vec) {
        return (float) get(X_HANDLE, vec);
    }

    public float getY(long vec) {
        return (float) get(Y_HANDLE, vec);
    }

    public void addTo(long dest, long vec2) {
        add(dest, vec2, dest);
    }

    public void add(long vec1, long vec2, long dest) {
        set(dest, getX(vec1) + getX(vec2), getY(vec1) + getY(vec2));
    }

    public long add(long vec1, long vec2) {
        long dest = next();
        add(vec1, vec2, dest);
        return dest;
    }

    public void sclThis(long vec, float scl) {
        scl(vec, scl, vec);
    }

    public void scl(long vec, float scl, long dest) {
        set(dest, getX(vec) * scl, getY(vec) * scl);
    }

    public long scl(long vec, float scl) {
        long dest = next();
        scl(vec, scl, dest);
        return dest;
    }

    public void normalizeThis(long vec) {
        normalize(vec, vec);
    }

    public void normalize(long vec, long dest) {
        float x = getX(vec);
        float y = getY(vec);

        float len2 = x * x + y * y;

        if (len2 > EPSILON) {
            float invLength = 1.0f / (float) Math.sqrt(len2);
            set(dest, x * invLength, y * invLength);
        } else {
            set(dest, 0.0f, 0.0f);
        }
    }

    public long normalize(long vec) {
        long dest = next();
        normalize(vec, dest);
        return dest;
    }

    public float len2(long vec) {
        float x = getX(vec);
        float y = getY(vec);

        return x * x + y * y;
    }

    public float len(long vec) {
        return (float) Math.sqrt(len2(vec));
    }

    public float dst2(long vec1, long vec2) {
        return dst2(getX(vec1), getX(vec2), getY(vec1), getY(vec2));
    }

    public float dst(long vec1, long vec2) {
        return (float) Math.sqrt(dst2(vec1, vec2));
    }

    public static float dst2(float x1, float x2, float y1, float y2) {
        float hdst = x2 - x1;
        float vdst = y2 - y1;
        return hdst * hdst + vdst * vdst;
    }

    public static float dst(float x1, float x2, float y1, float y2) {
        return (float) Math.sqrt(dst2(x1, x2, y1, y2));
    }

    public String toString(long vec) {
        return "(" + getX(vec) + "|" + getY(vec) + ")";
    }
}
