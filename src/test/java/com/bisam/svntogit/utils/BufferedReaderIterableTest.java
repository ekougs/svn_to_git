package com.bisam.svntogit.utils;

import com.bisam.svntogit.FileTestCase;
import org.junit.Test;

import java.util.LinkedList;

import static junit.framework.TestCase.assertEquals;

public class BufferedReaderIterableTest {

  @Test
  public void testStringBufferIsIterable() throws Exception {
    LinkedList<String> expectedLogLines = new LinkedList<>(FileTestCase.EXPECTED_LOG_LINES);
    BufferedReaderIterable bufferedReaderIterable =
      FileTestCase.createBufferedReaderIterable(BufferedReaderIterableTest.class.getResource("log.txt").getFile());

    for (String actualLogLine : bufferedReaderIterable) {
      assertEquals(expectedLogLines.poll(), actualLogLine);
    }
  }
}
