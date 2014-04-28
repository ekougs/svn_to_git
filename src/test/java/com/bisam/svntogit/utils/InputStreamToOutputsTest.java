package com.bisam.svntogit.utils;

import com.bisam.svntogit.FileTestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import static junit.framework.Assert.assertEquals;

public class InputStreamToOutputsTest {
  private static final String INPUT_1 = "input1";
  private static final String INPUT_2 = "input2";

  @Test
  public void testWriteToFileAndPrintStreamSequentially() throws Exception {
    File input1Temp = createTemp(INPUT_1);
    File input2Temp = createTemp(INPUT_2);
    try (PrintStream input2TempPrintStream = new PrintStream(input2Temp);
         InputStreamToOutputs inputStreamToOutputs = InputStreamToOutputs.init(input1Temp).add(input2TempPrintStream)) {
      for (String expectedLogLine : FileTestCase.EXPECTED_LOG_LINES) {
        inputStreamToOutputs.handleLine(expectedLogLine);
      }
    }

    checkTempIsCorrect(input1Temp);
    checkTempIsCorrect(input2Temp);
  }

  private static void checkTempIsCorrect(File tempFile) throws FileNotFoundException {
    LinkedList<String> expectedLogLines = new LinkedList<>(FileTestCase.EXPECTED_LOG_LINES);
    for (String actualLogLine : FileTestCase.createBufferedReaderIterable(tempFile.getAbsolutePath())) {
      assertEquals(expectedLogLines.poll(), actualLogLine);
    }
  }

  private File createTemp(String tempName) throws IOException {
    return java.nio.file.Files.createTempFile(new File(Files.getLocalDirectory(InputStreamToOutputsTest.class)).toPath(), tempName, ".tmp").toFile();
  }
}
