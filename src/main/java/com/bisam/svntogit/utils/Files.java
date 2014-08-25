package com.bisam.svntogit.utils;

import java.io.File;

public class Files {
  static final String LINE_SEPARATOR = System.getProperty("line.separator");

  public static boolean fileExists(String filePath) {
    return new File(filePath).exists();
  }

  public static File getLocalFile(Class<?> baseClass, String fileName) {
    return new File(getLocalFilePath(baseClass, fileName));
  }

  public static String getLocalFilePath(Class<?> baseClass, String fileName) {
    String localDirectory = getLocalDirectory(baseClass);
    return localDirectory + File.separatorChar + fileName;
  }

  public static String getLocalDirectory(Class<?> baseClass) {
    return new File(baseClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
  }

}
