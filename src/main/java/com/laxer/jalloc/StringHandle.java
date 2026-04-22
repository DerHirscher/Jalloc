package com.laxer.jalloc;

import org.jetbrains.annotations.NotNull;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class StringHandle extends ByteHandle {
    private final StringType type;

    public StringHandle(long capacity) {
        this(capacity, StringType.LONG_UTF16);
    }

    public StringHandle(long byteCapacity, @NotNull StringType type) {
        super(byteCapacity, type.headerBytes);
        this.type = type;
    }

    public long newEmptyString(int maxStrLen) {
        if (maxStrLen > type.maxlen) {
            throw new IllegalArgumentException("The input exceeds the maximum string length");
        }

        long str = nextBytes(paddedBytes(maxStrLen));
        setLen(str, 0);
        return str;
    }

    public long newString(byte[] bytes) {
        Objects.requireNonNull(bytes);

        return newString(bytes.length / type.charBytes, bytes);
    }

    public long newString(String s) {
        Objects.requireNonNull(s);

        return newString(s.length(), s.getBytes(type.encoding));
    }

    private long newString(int strlen, byte[] bytes) {
        if (strlen > type.maxlen) {
            throw new IllegalArgumentException("The input exceeds the maximum string length");
        }

        long str = nextBytes(paddedBytes(strlen));
        setLen(str, strlen);

        MemorySegment.copy(
                bytes, 0,
                mem, ValueLayout.JAVA_BYTE,
                str + type.headerBytes,
                strlen * type.charBytes
        );

        return str;
    }

    @Override
    public void cpy(long str, long dest) {
        int totalBytes = paddedBytes(len(str));
        int destBytes = paddedBytes(len(dest));

        if (totalBytes > destBytes) {
            throw new IllegalArgumentException("The length of the destination must be greater or equal than the base string");
        }
        cpyBytes(str, dest, totalBytes);
    }

    public void unsafeCpy(long str, long dest) {
        int totalBytes = paddedBytes(len(str));
        cpyBytes(str, dest, totalBytes);
    }

    @Override
    public long cpy(long str) {
        long dest = nextBytes(paddedBytes(len(str)));
        unsafeCpy(str, dest);
        return dest;
    }

    @Override
    public boolean equals(long str1, long str2) {
        int str1Len = len(str1);
        return str1Len == len(str2) && equalsBytes(str1, str2, type.headerBytes + ((long) str1Len * type.charBytes));
    }

    public void setChar(long str, int index, char c) {
        switch (type) {
            case SHORT_ASCII, LONG_ASCII_ALIGNED, LONG_ASCII_UNALIGNED -> {
                if (c > 127) {
                    c = '?';
                }
                mem.set(ValueLayout.JAVA_BYTE, str + type.headerBytes + (long) index, (byte) c);
            }
            case SHORT_UTF16 -> mem.set(ValueLayout.JAVA_CHAR_UNALIGNED, str + type.headerBytes + (long) index * type.charBytes, c);
            case LONG_UTF16 -> mem.set(ValueLayout.JAVA_CHAR, str + type.headerBytes + (long) index * type.charBytes, c);
        }
    }

    public char charAt(long str, int index) {
        return switch (type) {
            case SHORT_ASCII, LONG_ASCII_ALIGNED, LONG_ASCII_UNALIGNED -> (char) mem.get(ValueLayout.JAVA_BYTE, str + type.headerBytes + (long) index);
            case SHORT_UTF16 -> mem.get(ValueLayout.JAVA_CHAR_UNALIGNED, str + type.headerBytes + (long) index * type.charBytes);
            case LONG_UTF16 -> mem.get(ValueLayout.JAVA_CHAR, str + type.headerBytes + (long) index * type.charBytes);
        };
    }

    private void setLen(long str, int len) {
        switch (type) {
            case SHORT_ASCII, SHORT_UTF16 -> mem.set(ValueLayout.JAVA_BYTE, str, (byte) len);
            case LONG_ASCII_ALIGNED, LONG_UTF16 -> mem.set(ValueLayout.JAVA_SHORT, str, (short) len);
            case LONG_ASCII_UNALIGNED -> mem.set(ValueLayout.JAVA_SHORT_UNALIGNED, str, (short) len);
        }
    }

    public int len(long str) {
        return switch (type) {
            case SHORT_ASCII, SHORT_UTF16 -> mem.get(ValueLayout.JAVA_BYTE, str) & 0xFF;
            case LONG_ASCII_ALIGNED, LONG_UTF16 -> mem.get(ValueLayout.JAVA_SHORT, str) & 0xFFFF;
            case LONG_ASCII_UNALIGNED -> mem.get(ValueLayout.JAVA_SHORT_UNALIGNED, str) & 0xFFFF;
        };
    }

    public boolean contains(long str, char c) {
        for (int i = 0; i < len(str); i++) {
            if (charAt(str, i) == c) return true;
        }
        return false;
    }

    public boolean contains(long str, long chars) {
        int strLen = len(str);
        int charsLen = len(chars);

        if (charsLen == 0) return true;
        if (charsLen > strLen) return false;

        long charsByteLen = (long) charsLen * type.charBytes;
        long strStart = str + type.headerBytes;
        long charsStart = chars + type.headerBytes;

        for (int i = 0; i <= strLen - charsLen; i++) {
            long currentOffset = strStart + ((long) i * type.charBytes);

            if (equalsBytes(currentOffset, charsStart, charsByteLen)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEmpty(long str) {
        return len(str) == 0;
    }

    public boolean startsWith(long str, char c) {
        if (isEmpty(str)) return false;

        return charAt(str, 0) == c;
    }

    public boolean endsWith(long str, char c) {
        if (isEmpty(str)) return false;

        return charAt(str, len(str) - 1) == c;
    }

    public int indexOf(long str, char c) {
        if (isEmpty(str)) return -1;

        for (int i = 0; i < len(str); i++) {
            if (charAt(str, i) == c) return i;
        }
        return -1;
    }

    public void toUpperCase(long str, long dest) {
        int strlen = len(str);
        if (paddedBytes(strlen) > paddedBytes(len(dest))) {
            throw new IllegalArgumentException("The destination must be large enough.");
        }
        setLen(dest, strlen);
        unsafeToUpperCase0(str, dest, strlen);
    }

    public long toUpperCase(long str) {
        int strlen = len(str);
        long dest = nextBytes(paddedBytes(strlen));
        setLen(dest, strlen);
        unsafeToUpperCase0(str, dest, strlen);
        return dest;
    }

    public void thisToUpperCase(long str) {
        unsafeToUpperCase0(str, str, len(str));
    }

    public void unsafeToUpperCase(long str, long dest) {
        int strlen = len(str);
        setLen(dest, strlen);
        unsafeToUpperCase0(str, dest, strlen);
    }

    private void unsafeToUpperCase0(long str, long dest, int strlen) {
        long srcOffset = str + type.headerBytes;
        long destOffset = dest + type.headerBytes;

        if (type.encoding == StandardCharsets.US_ASCII) {
            for (int i = 0; i < strlen; i++) {
                byte b = mem.get(ValueLayout.JAVA_BYTE, srcOffset + i);
                if (b >= 'a' && b <= 'z') {
                    b -= 32;
                }
                mem.set(ValueLayout.JAVA_BYTE, destOffset + i, b);
            }
        } else {
            ValueLayout.OfChar layout = type == StringType.SHORT_UTF16 ? ValueLayout.JAVA_CHAR_UNALIGNED : ValueLayout.JAVA_CHAR;
            for (int i = 0; i < strlen; i++) {
                char c = mem.get(layout, srcOffset + (long) i * 2);
                mem.set(layout, destOffset + (long) i * 2, Character.toUpperCase(c));
            }
        }
    }

    public void toLowerCase(long str, long dest) {
        int strlen = len(str);
        if (paddedBytes(strlen) > paddedBytes(len(dest))) {
            throw new IllegalArgumentException("The destination must be large enough.");
        }
        setLen(dest, strlen);
        unsafeToLowerCase0(str, dest, strlen);
    }

    public long toLowerCase(long str) {
        int strlen = len(str);
        long dest = nextBytes(paddedBytes(strlen));
        setLen(dest, strlen);
        unsafeToLowerCase0(str, dest, strlen);
        return dest;
    }

    public void thisToLowerCase(long str) {
        unsafeToLowerCase0(str, str, len(str));
    }

    public void unsafeToLowerCase(long str, long dest) {
        int strlen = len(str);
        setLen(dest, strlen);
        unsafeToLowerCase0(str, dest, strlen);
    }

    private void unsafeToLowerCase0(long str, long dest, int strlen) {
        long srcOffset = str + type.headerBytes;
        long destOffset = dest + type.headerBytes;

        if (type.encoding == StandardCharsets.US_ASCII) {
            for (int i = 0; i < strlen; i++) {
                byte b = mem.get(ValueLayout.JAVA_BYTE, srcOffset + i);
                if (b >= 'A' && b <= 'Z') {
                    b += 32;
                }
                mem.set(ValueLayout.JAVA_BYTE, destOffset + i, b);
            }
        } else {
            ValueLayout.OfChar layout = type == StringType.SHORT_UTF16 ? ValueLayout.JAVA_CHAR_UNALIGNED : ValueLayout.JAVA_CHAR;
            for (int i = 0; i < strlen; i++) {
                char c = mem.get(layout, srcOffset + (long) i * 2);
                mem.set(layout, destOffset + (long) i * 2, Character.toLowerCase(c));
            }
        }
    }

    public void concat(long str1, long str2, long dest) {
        int str1Len =  len(str1);
        int str2Len =  len(str2);
        int destLen = str1Len + str2Len;

        if (paddedBytes(destLen) > paddedBytes(len(dest))) {
            throw new IllegalArgumentException("The length of the destination must must be greater or equal the length of the concatenation");
        }

        concat0(str1, str2, dest, str1Len, str2Len, destLen);
    }

    public void unsafeConcat(long str1, long str2, long dest) {
        int str1Len =  len(str1);
        int str2Len =  len(str2);

        concat0(str1, str2, dest, str1Len, str2Len, str1Len + str2Len);
    }

    public long concat(long str1, long str2) {
        int str1Len =  len(str1);
        int str2Len =  len(str2);
        int destLen = str1Len + str2Len;

        long dest = nextBytes(paddedBytes(destLen));

        concat0(str1, str2, dest, str1Len, str2Len, destLen);
        return dest;
    }

    public String toString(long str) {
        int len = len(str);
        if (len == 0) return "";

        byte[] bytes = new byte[len * type.charBytes];
        MemorySegment.copy(mem, str + type.headerBytes, MemorySegment.ofArray(bytes), 0, bytes.length);

        return new String(bytes, type.encoding);
    }

    private int paddedBytes(int strlen) {
        int bytes = type.headerBytes + strlen * type.charBytes;
        if (type == StringType.LONG_ASCII_ALIGNED && (bytes & 1) != 0) bytes++;
        return bytes;
    }

    private void concat0(long str1, long str2, long dest, int str1Len, int str2Len, int destLen) {
        setLen(dest, destLen);

        long str1Bytes = (long) str1Len * type.charBytes;

        if (str1Len != 0) {
            cpyBytes(str1 + type.headerBytes, dest + type.headerBytes, str1Bytes);
        }
        if (str2Len != 0) {
            cpyBytes(str2 + type.headerBytes, dest + type.headerBytes + str1Bytes, (long) str2Len * type.charBytes);
        }
    }

    public enum StringType {
        //ASCII encoding, max String length 255, aligned bytes
        SHORT_ASCII(1, 1, StandardCharsets.US_ASCII),

        //ASCII encoding, max String length 65.535, aligned bytes, faster, but could use more storage
        LONG_ASCII_ALIGNED(2, 1, StandardCharsets.US_ASCII),

        //ASCII encoding, max String length 65.535, unaligned bytes, uses less storage, but is a bit slower
        LONG_ASCII_UNALIGNED(2, 1, StandardCharsets.US_ASCII),

        //UTF16 encoding, max String length 255, unaligned bytes
        SHORT_UTF16(1, 2, StandardCharsets.UTF_16LE),

        //UTF16 encoding, max String length 65.535, aligned bytes
        LONG_UTF16(2, 2, StandardCharsets.UTF_16LE),
        ;

        private final int headerBytes;
        private final int charBytes;
        private final int maxlen;
        private final Charset encoding;

        StringType(int headerBytes, int charBytes, Charset encoding) {
            this.headerBytes = headerBytes;
            this.charBytes = charBytes;
            this.maxlen = Math.unsignedPowExact(2, headerBytes * 8) - 1;
            this.encoding = encoding;
        }
    }
}