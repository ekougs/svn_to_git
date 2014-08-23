package com.bisam.svntogit.utils;

import com.bisam.svntogit.FileTestCase;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

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

  @Test
  public void testIterableInterruptedByStreamClosure() throws Exception {
    String log = BufferedReaderIterableTest.class.getResource("log.txt").getFile();
    Path logPath = new File(log).toPath();
    Path logDirectory = logPath.getParent();
    Path logTempPath = logDirectory.resolve("log_temp.txt");
    java.nio.file.Files.copy(logPath, logTempPath);
    File logTempFile = logTempPath.toFile();
    FileInputStream stream =
      new FileInputStream(logTempFile);
    BufferedReader logBufferedReader = new BufferedReader(new InputStreamReader(stream));
    BufferedReaderIterable bufferedReaderIterable =
      new BufferedReaderIterable(logBufferedReader);

    while (logTempFile.exists()) {
      logTempFile.delete();
    }
    stream.close();

    Iterator<String> bufferedReaderIterator = bufferedReaderIterable.iterator();
    String errorLogLine = bufferedReaderIterator.next();
    assertEquals("Stream has been closed abruptly", errorLogLine);
    assertFalse(bufferedReaderIterator.hasNext());
    assertNull(bufferedReaderIterator.next());
  }
}
