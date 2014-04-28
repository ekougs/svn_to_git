package com.bisam.svntogit.utils;

import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.assertEquals;

public class FilesTest {
  private static final String TEMP_TXT = "temp.txt";

  @Test
  public void testLocalDirectory() throws Exception {
    Path targetPath = Paths.get(FilesTest.class.getResource(Strings.EMPTY).toURI()).getParent().getParent().getParent().getParent().getParent();
    assertEquals(targetPath.toFile(), new File(Files.getLocalDirectory(FilesTest.class)));
  }

  @Test
  public void testLocalFilePath() throws Exception {
    Path targetPath = Paths.get(FilesTest.class.getResource(Strings.EMPTY).toURI()).getParent().getParent().getParent().getParent().getParent();
    Path tempPath = Paths.get(targetPath.toString(), TEMP_TXT);
    assertEquals(tempPath.toFile(), new File(Files.getLocalFilePath(FilesTest.class, TEMP_TXT)));
  }
}
