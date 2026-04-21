package com.laxer.jalloc;

import java.lang.foreign.ValueLayout;
import java.util.Objects;

public class StringHandle extends DynamicStructHandle {
    private static final int LEN_BYTES = 2;

    public StringHandle(long byteCapacity) {
        super(byteCapacity);
    }

    public long newString(String s) {
        Objects.requireNonNull(s);

        int strlen = s.length();
        long str = next(LEN_BYTES + (long) strlen * Character.BYTES);
        setLen(str, (short) strlen);

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            setChar(str, i, chars[i]);
        }
        return str;
    }

    public void setChar(long str, int index, char c) {
        mem.set(ValueLayout.JAVA_CHAR, str + LEN_BYTES + (long) index * Character.BYTES, c);
    }

    public char charAt(long str, int index) {
        return mem.get(ValueLayout.JAVA_CHAR, str + LEN_BYTES + (long) index * Character.BYTES);
    }

    private void setLen(long str, short len) {
        mem.set(ValueLayout.JAVA_SHORT, str, len);
    }

    public int getLength(long str) {
        return mem.get(ValueLayout.JAVA_SHORT, str);
    }

    public String toString(long str) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < getLength(str); i++) {
            s.append(charAt(str, i));
        }
        return s.toString();
    }
}
