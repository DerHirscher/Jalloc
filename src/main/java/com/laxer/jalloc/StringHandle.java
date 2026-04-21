package com.laxer.jalloc;

import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringHandle extends ByteHandle {
    private final StringCharset charset;
    private final MaxStringLength maxLength;

    public StringHandle(long capacity) {
        this(capacity, MaxStringLength.SHORT);
    }

    public StringHandle(long byteCapacity, MaxStringLength maxStringLength) {
        this(byteCapacity, StringCharset.UTF8, maxStringLength);
    }

    public StringHandle(long byteCapacity, @NotNull StringHandle.StringCharset charset, @NotNull MaxStringLength maxStringLength) {
        super(byteCapacity);
        this.charset = charset;
        this.maxLength = maxStringLength;
    }

    public long newString(String s) {
        Objects.requireNonNull(s);
        int strlen = s.length();

        int mod = strlen % maxLength.bytes;
        int bytePadding = 0;

        if (mod != 0) {
            bytePadding = maxLength.bytes - mod;
        }

        long str = nextBytes(maxLength.bytes + (long) (strlen + bytePadding) * charset.bytes);
        setLen(str, (short) strlen);

        byte[] bytes = s.getBytes(charset.standardCharset);

        MemorySegment srcSegment = MemorySegment.ofArray(bytes);

        MemorySegment.copy(srcSegment, 0, mem, str + maxLength.bytes, (long) strlen * charset.bytes);

        return str;
    }

    @Override
    public void cpy(long ptr, long dest) {

    }

    @Override
    public long cpy(long ptr) {
        return 0;
    }

    public void setChar(long str, int index, char c) {
        mem.set(ValueLayout.JAVA_CHAR, str + maxLength.bytes + (long) index * charset.bytes, c);
    }

    public char charAt(long str, int index) {
        return mem.get(ValueLayout.JAVA_CHAR, str + maxLength.bytes + (long) index * charset.bytes);
    }

    private void setLen(long str, int len) {
        switch (maxLength) {
            case SHORT -> mem.set(ValueLayout.JAVA_BYTE, str, (byte) len);
            case LONG -> mem.set(ValueLayout.JAVA_SHORT, str, (short) len);
        }
    }

    public int getLength(long str) {
        return switch (maxLength) {
            case SHORT -> mem.get(ValueLayout.JAVA_BYTE, str);
            case LONG -> mem.get(ValueLayout.JAVA_SHORT, str);
        };
    }

    public String toString(long str) {
        int len = getLength(str);
        if (len == 0) return "";
        char[] chars = new char[len];

        MemorySegment destSegment = MemorySegment.ofArray(chars);
        MemorySegment.copy(mem, str + maxLength.bytes, destSegment, 0, (long) len * charset.bytes);

        return new String(chars);
    }

    public enum StringCharset {
        UTF8(StandardCharsets.UTF_8, 1),
        UTF16(StandardCharsets.UTF_16, 2),
        UTF32(StandardCharsets.UTF_32, 4);

        private final Charset standardCharset;
        private final int bytes;

        StringCharset(Charset charset, int bytes) {
            this.standardCharset = charset;
            this.bytes = bytes;
        }
    }

    public enum MaxStringLength {

        //max chars: 255
        SHORT(1),

        //max chars: 65.535
        LONG(2);

        private final int bytes;

        MaxStringLength(int bytes) {
            this.bytes = bytes;
        }
    }
}