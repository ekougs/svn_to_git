package com.bisam.svntogit;

import java.io.File;

public class Files {
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");

  static String getLocalFilePath(Class<?> baseClass, String fileName) {
    String localDirectory = getLocalDirectory(baseClass);
    return localDirectory + File.separatorChar + fileName;
  }

  static String getLocalDirectory(Class<?> baseClass) {
    return new File(baseClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
  }

}
