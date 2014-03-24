package com.bisam.svntogit;

import java.io.File;

public class Files {
  static String getLocalFilePath(Class<?> baseClass, String fileName) {
    String localDirectory = new File(baseClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    return localDirectory + File.separatorChar + fileName;
  }
}
