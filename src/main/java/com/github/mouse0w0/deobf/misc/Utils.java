package com.github.mouse0w0.deobf.misc;

public class Utils {

    public static String stringToUnicode(String str) {
        StringBuffer builder = new StringBuffer();
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            builder.append("\\u" + Integer.toHexString(c[i]));
        }
        return builder.toString();
    }

    public static String unicodeToString(String unicode) {
        StringBuffer builder = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int index = Integer.parseInt(hex[i], 16);
            builder.append((char) index);
        }
        return builder.toString();
    }

    public static String toSafe(String value) {
        StringBuffer builder = new StringBuffer();
        char[] c = value.toCharArray();
        for (int i = 0; i < c.length; i++) {
            String hex = Integer.toHexString(c[i]);
            for (int j = 4 - hex.length(); j > 0; j--) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(toSafe("Why?不要啊"));
    }

    public static String getClassName(String desc) {
        return desc.substring(desc.lastIndexOf('/') + 1);
    }

    public static String getClassPackage(String desc) {
        return desc.substring(0, desc.lastIndexOf('/'));
    }
}
