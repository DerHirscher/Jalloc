package com.laxer.jalloc.list;

import com.laxer.jalloc.StructHandle;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.function.IntConsumer;

public class LinkedIntListHandle extends LinkedListHandle {
    private final AbstractIntNodeHandle nodeHandle;

    protected LinkedIntListHandle(long maxListCount, @NotNull ListType listType, long totalNodeCapacity) {
        super(maxListCount);
        nodeHandle = switch (listType) {
            case SINGLE_LINKED -> new SLIntNodeHandle(totalNodeCapacity);
            case DOUBLY_LINKED -> new DLIntNodeHandle(totalNodeCapacity);
        };
    }

    public void add(long list, int value) {
        long node = nodeHandle.newNode(value);

        if (isEmpty(list)) {
            setFirst(list, node);
            if (nodeHandle instanceof DLIntNodeHandle dlnh) {
                dlnh.setPrev(node, NULL);
            }
        } else {
            long last = getLast(list);
            nodeHandle.setNext(last, node);
            if (nodeHandle instanceof DLIntNodeHandle dlnh) {
                dlnh.setPrev(node, last);
            }
        }
        nodeHandle.setNext(node, NULL);
        setLast(list, node);
        setSize(list, getSize(list) + 1);
    }

    public int get(long list, long index) {
        long size = getSize(list);
        Objects.checkIndex(index, size);

        return nodeHandle.value(getNode(list, size, index));
    }

    public long indexOf(long list, int value) {
        long current = getFirst(list);
        long index = 0;

        while (current != NULL) {
            if (nodeHandle.value(current) == value) {
                return index;
            }
            current = nodeHandle.getNext(current);
            index++;
        }
        return NULL;
    }

    public void remove(long list, long index) {
        long size = getSize(list);
        Objects.checkIndex(index, size);

        long toRemove;
        long prevNode;



    }

    private long getNode(long list, long size, long index) {
        if (nodeHandle instanceof DLIntNodeHandle dlnh && index > size / 2) {
            long current = getLast(list);
            long l = size - 1;

            while (l > index) {
                current = dlnh.getPrev(current);
                l--;
            }
            return current;
        }
        long current = getFirst(list);
        long l = 0;

        while (l < index) {
            current = nodeHandle.getNext(current);
            l++;
        }
        return current;
    }

    private long searchNode(long list, int value) {
        long current = getFirst(list);

        while (current != NULL) {
            if (nodeHandle.value(current) == value) {
                return current;
            }
            current = nodeHandle.getNext(current);
        }
        return NULL;
    }

    public void forEach(long list, IntConsumer action) {
        long current = getFirst(list);

        while (current != NULL) {
            action.accept(nodeHandle.value(current));
            current = nodeHandle.getNext(current);
        }
    }

    private static abstract class AbstractIntNodeHandle extends StructHandle {
        private AbstractIntNodeHandle(@NotNull MemoryLayout layout, long capacity) {
            super(layout.byteSize(), capacity, layout.byteAlignment());
        }

        private long newNode(int value) {
            long node = nextStruct();
            setValue(node, value);
            return node;
        }

        protected abstract void setValue(long node, int value);

        protected abstract void setNext(long node, long next);

        protected abstract int value(long node);

        protected abstract long getNext(long node);

    }

    private static class SLIntNodeHandle extends AbstractIntNodeHandle  {
        private static final StructLayout LAYOUT = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("val"),
                ValueLayout.JAVA_LONG_UNALIGNED.withName("next")
        );

        private static final VarHandle VALUE_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("val"));
        private static final VarHandle NEXT_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("next"));

        private SLIntNodeHandle(long capacity) {
            super(LAYOUT, capacity);
        }

        @Override
        protected void setValue(long node, int value) {
            set(VALUE_HANDLE, node, value);
        }

        @Override
        protected void setNext(long node, long next) {
            set(NEXT_HANDLE, node, next);
        }

        @Override
        protected int value(long node) {
            return getInt(VALUE_HANDLE, node);
        }

        @Override
        protected long getNext(long node) {
            return getLong(NEXT_HANDLE, node);
        }
    }

    protected static class DLIntNodeHandle extends AbstractIntNodeHandle {
        private static final StructLayout LAYOUT = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("val"),
                ValueLayout.JAVA_LONG_UNALIGNED.withName("next"),
                ValueLayout.JAVA_LONG_UNALIGNED.withName("prev")
        );

        private static final VarHandle VALUE_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("val"));
        private static final VarHandle NEXT_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("next"));
        private static final VarHandle PREV_HANDLE = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("prev"));

        private DLIntNodeHandle(long capacity) {
            super(LAYOUT, capacity);
        }

        @Override
        protected void setValue(long node, int value) {
            set(VALUE_HANDLE, node, value);
        }

        @Override
        protected void setNext(long node, long next) {
            set(NEXT_HANDLE, node, next);
        }

        protected void setPrev(long node, long prev) {
            set(PREV_HANDLE, node, prev);
        }

        @Override
        protected int value(long node) {
            return getInt(VALUE_HANDLE, node);
        }

        @Override
        protected long getNext(long node) {
            return getLong(NEXT_HANDLE, node);
        }

        protected long getPrev(long node) {
            return getLong(PREV_HANDLE, node);
        }
    }
}
