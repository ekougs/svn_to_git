package com.bisam.svntogit;

class Logs {
    static void appendln(String... elements) {
        appendln(Strings.append(elements));
    }
    static void appendln(String line) {
        System.out.append(line).append(Files.LINE_SEPARATOR);
    }

    static void appendln() {
        appendln(Strings.EMPTY);
    }
}
