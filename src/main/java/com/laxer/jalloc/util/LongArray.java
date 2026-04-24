package com.laxer.jalloc.util;

import java.util.Objects;

public class LongArray {
    private static final int MIN_CAPS = 10;

    private long[] data;
    private int size = 0;

    public LongArray(int initCapacity) {
        data = new long[Math.max(initCapacity, MIN_CAPS)];
    }

    public void add(long v) {
        if (size == data.length) grow();
        data[size] = v;
        size++;
    }

    public void insert(long v, int i) {
        Objects.checkIndex(i, size);
        if (size == data.length) grow();
        System.arraycopy(data, i, data, i + 1, size - i);
        data[i] = v;
        size++;
    }

    public long get(int i) {
        Objects.checkIndex(i, size);
        return data[i];
    }

    public void remove(int i) {
        Objects.checkIndex(i, size);
        System.arraycopy(data, i + 1, data, i, size - i - 1);
        size--;
    }

    public boolean remove(long v) {
        int i = indexOf(v);
        if (i == -1) return false;
        remove(i);
        return true;
    }

    public int indexOf(long v) {
        for (int i = 0; i < size; i++) {
            if (data[i] == v) return i;
        }
        return -1;
    }

    public boolean contains(long v) {
        return indexOf(v) != -1;
    }

    public int size() {
        return size;
    }

    public long[] toArray() {
        long[] r = new long[size];
        System.arraycopy(data, 0, r, 0, size);
        return r;
    }

    private void grow() {
        long[] n = new long[(int) (data.length * 1.5f)];
        System.arraycopy(data, 0, n, 0, data.length);
        data = n;
    }

    public void trimToSize() {
        if (size < data.length) {
            long[] n = new long[Math.max(size, MIN_CAPS)];
            System.arraycopy(data, 0, n, 0, size);
            data = n;
        }
    }
}
