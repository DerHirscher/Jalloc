package com.laxer.jalloc.list;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public abstract class LinkedListHandle extends AbstractLinkedListHandle {
    private static final StructLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_LONG.withName("first"),
            ValueLayout.JAVA_LONG.withName("last"),
            ValueLayout.JAVA_LONG.withName("size")
    );

    private static final VarHandle FIRST_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("first"));
    private static final VarHandle LAST_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("last"));
    private static final VarHandle SIZE_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("size"));

    protected LinkedListHandle(long maxListCount) {
        super(LAYOUT, maxListCount);
    }

    @Override
    protected long newLinkedList() {
        long list = nextStruct();
        setSize(list, 0);
        return list;
    }

    @Override
    protected void setFirst(long list, long firstNode) {
        set(FIRST_HANDLE, list, firstNode);
    }

    @Override
    protected void setLast(long list, long lastNode) {
        set(LAST_HANDLE, list, lastNode);
    }

    protected void setSize(long list, long size) {
        set(SIZE_HANDLE, list, size);
    }

    @Override
    public long getFirst(long list) {
        if (isEmpty(list)) return NULL;
        return getLong(FIRST_HANDLE, list);
    }

    @Override
    public long getLast(long list) {
        if (isEmpty(list)) return NULL;
        return getLong(LAST_HANDLE, list);
    }

    @Override
    public long getSize(long list) {
        return getLong(SIZE_HANDLE, list);
    }
}
