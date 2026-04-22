package com.laxer.jalloc;

public class Test {
    static void main() {

        try(StringHandle strh = new StringHandle(100)) {
            System.out.println((6 & 1) != 0);

            long str1 = strh.newString("123");
            long str2 = strh.newString("4567");
            long str3 = strh.newString("abcdefg");


            System.out.println(strh.contains(str3, 'g'));

            strh.concat(str1, str2, str3);

            System.out.println(strh.toString(str3));
        }
    }
}
