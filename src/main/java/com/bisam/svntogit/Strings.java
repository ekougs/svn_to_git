package com.bisam.svntogit;

public class Strings {
  public static final String EMPTY = "";

  static String append(String... stringsToAppend) {
        if (stringsToAppend == null) {
            return Strings.EMPTY;
        }
        StringBuilder appendedStringBuilder = new StringBuilder();
        for (String stringToAppend : stringsToAppend) {
            appendedStringBuilder.append(stringToAppend);
        }
        return appendedStringBuilder.toString();
    }

  static boolean isEmptyString(String value) {
    return value == null || EMPTY.equals(value.trim());
  }
}
