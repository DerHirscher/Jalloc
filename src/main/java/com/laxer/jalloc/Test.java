package com.laxer.jalloc;

public class Test {
    static void main() {
        try(StringHandle strh = new StringHandle(100)) {
            long str1 = strh.newString("Hello world!");

            System.out.println(strh.toString(str1) + strh.getLength(str1));
        }
    }
}
