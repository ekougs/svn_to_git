package com.bisam.svntogit.utils;

import java.io.File;

public class Files {
  static final String LINE_SEPARATOR = System.getProperty("line.separator");

  public static String getLocalFilePath(Class<?> baseClass, String fileName) {
    String localDirectory = getLocalDirectory(baseClass);
    return localDirectory + File.separatorChar + fileName;
  }

  public static String getLocalDirectory(Class<?> baseClass) {
    return new File(baseClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
  }

}
