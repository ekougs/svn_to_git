package com.bisam.svntogit;

import java.io.File;

public class Files {
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");

  static String getLocalFilePath(Class<?> baseClass, String fileName) {
    String localDirectory = new File(baseClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    return localDirectory + File.separatorChar + fileName;
  }

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
