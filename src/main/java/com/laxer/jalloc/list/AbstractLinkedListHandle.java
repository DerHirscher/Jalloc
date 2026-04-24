package com.laxer.jalloc.list;

import com.laxer.jalloc.StructHandle;
import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

public abstract class AbstractLinkedListHandle extends StructHandle {
    protected AbstractLinkedListHandle(MemoryLayout layout, long capacity) {
        super(layout.byteSize(), capacity, layout.byteAlignment());
    }

    protected abstract long newLinkedList();

    protected abstract void setFirst(long list, long firstNode);

    protected abstract void setLast(long list, long lastNode);

    public abstract long getFirst(long list);

    public abstract long getLast(long list);

    public abstract long getSize(long list);

    protected final boolean isEmpty(long list) {
        return getSize(list) == 0;
    }

//    public enum ElementType {
//        BOOLEAN(1, false),
//        BYTE(1, false),
//        CHAR(2, false),
//        SHORT(2, false),
//        INT(4, false),
//        FLOAT(4, false),
//        LONG(8, true),
//        DOUBLE(8, true);
//
//        private final int bytes;
//        private final boolean nodeAligned;
//
//        ElementType(int bytes, boolean nodeAligned) {
//            this.bytes = bytes;
//            this.nodeAligned = nodeAligned;
//        }
//    }

    public enum ListType {
        SINGLE_LINKED,
        DOUBLY_LINKED,
    }

//    protected abstract static class AbstractNodeHandle extends StructHandle {
//        protected AbstractNodeHandle(MemoryLayout layout, long capacity) {
//            super(layout.byteSize(), capacity, layout.byteAlignment());
//        }
//
//        protected void setNext(long node, long next) {
//            mem.set(ptrLayout(), node + type.bytes, next);
//        }
//
//        protected long next(long node) {
//            return mem.get(ptrLayout(), node + type.bytes);
//        }
//
//        protected ValueLayout.OfLong ptrLayout() {
//            return type.nodeAligned ? ValueLayout.JAVA_LONG : ValueLayout.JAVA_LONG_UNALIGNED;
//        }
//
////        protected void setValue(long node, Object value) {
////            switch (type) {
////                case BOOLEAN -> mem.set(ValueLayout.JAVA_BOOLEAN, node, (boolean) value);
////                case BYTE -> mem.set(ValueLayout.JAVA_BYTE, node, (byte) value);
////                case CHAR -> mem.set(ValueLayout.JAVA_CHAR, node, (char) value);
////                case SHORT -> mem.set(ValueLayout.JAVA_SHORT, node, (short) value);
////                case INT -> mem.set(ValueLayout.JAVA_INT, node, (int) value);
////                case FLOAT -> mem.set(ValueLayout.JAVA_FLOAT, node, (float) value);
////                case LONG -> mem.set(ValueLayout.JAVA_LONG, node, (long) value);
////                case DOUBLE -> mem.set(ValueLayout.JAVA_DOUBLE, node, (double) value);
////            };
////        }
////
////        protected Object value(long node) {
////            return switch (type) {
////                case BOOLEAN -> mem.get(ValueLayout.JAVA_BOOLEAN, node);
////                case BYTE -> mem.get(ValueLayout.JAVA_BYTE, node);
////                case CHAR -> mem.get(ValueLayout.JAVA_CHAR, node);
////                case SHORT -> mem.get(ValueLayout.JAVA_SHORT, node);
////                case INT -> mem.get(ValueLayout.JAVA_INT, node);
////                case FLOAT -> mem.get(ValueLayout.JAVA_FLOAT, node);
////                case LONG -> mem.get(ValueLayout.JAVA_LONG, node);
////                case DOUBLE -> mem.get(ValueLayout.JAVA_DOUBLE, node);
////            };
////        }
//    }
//
//    protected static class SingleLinkedNodeHandle extends AbstractNodeHandle {
//        protected SingleLinkedNodeHandle(@NotNull ElementType type, long capacity) {
//            super(type, 0, capacity);
//        }
//    }
//
//    protected static class DoublyLinkedNodeHandle extends AbstractNodeHandle {
//        protected DoublyLinkedNodeHandle(@NotNull ElementType type, long capacity) {
//            super(type, 1, capacity);
//        }
//
//        protected void setPrev(long node, long prev) {
//            mem.set(ptrLayout(), node + type.bytes + PTR_BYTES, prev);
//        }
//
//        protected long prev(long node) {
//            return mem.get(ptrLayout(), node + type.bytes + PTR_BYTES);
//        }
//    }
}
