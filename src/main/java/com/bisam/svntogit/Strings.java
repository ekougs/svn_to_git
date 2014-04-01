package com.bisam.svntogit;

public class Strings {
    static String append(String... stringsToAppend) {
        if (stringsToAppend == null) {
            return "";
        }
        StringBuilder appendedStringBuilder = new StringBuilder();
        for (String stringToAppend : stringsToAppend) {
            appendedStringBuilder.append(stringToAppend);
        }
        return appendedStringBuilder.toString();
    }
}
