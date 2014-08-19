package com.bisam.svntogit.utils;

public class Strings {
  public static final String EMPTY = "";

  public static String append(String... stringsToAppend) {
    if (stringsToAppend == null) {
      return Strings.EMPTY;
    }
    StringBuilder appendedStringBuilder = new StringBuilder();
    for (String stringToAppend : stringsToAppend) {
      appendedStringBuilder.append(stringToAppend);
    }
    return appendedStringBuilder.toString();
  }

  public static boolean isEmptyString(String value) {
    return value == null || EMPTY.equals(value.trim());
  }
}
