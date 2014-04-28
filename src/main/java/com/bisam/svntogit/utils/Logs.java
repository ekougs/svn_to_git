package com.bisam.svntogit.utils;

public class Logs {
    public static void appendln(String... elements) {
        appendln(Strings.append(elements));
    }
    public static void appendln(String line) {
        System.out.append(line).append(Files.LINE_SEPARATOR);
    }

    public static void appendln() {
        appendln(Strings.EMPTY);
    }
}
