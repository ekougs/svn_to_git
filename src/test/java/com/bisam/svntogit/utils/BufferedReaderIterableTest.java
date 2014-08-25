package com.bisam.svntogit.utils;

import com.bisam.svntogit.FileTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger LOG = LoggerFactory.getLogger(BufferedReaderIterableTest.class);

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
    File logTempFile = logTempPath.toFile();
    if (!deleteLogTemp(logTempFile)) {
      return;
    }
    java.nio.file.Files.copy(logPath, logTempPath);
    FileInputStream stream =
      new FileInputStream(logTempFile);
    BufferedReader logBufferedReader = new BufferedReader(new InputStreamReader(stream));
    BufferedReaderIterable bufferedReaderIterable =
      new BufferedReaderIterable(logBufferedReader);

    if (!deleteLogTemp(logTempFile)) {
      return;
    }
    stream.close();

    Iterator<String> bufferedReaderIterator = bufferedReaderIterable.iterator();
    String errorLogLine = bufferedReaderIterator.next();
    assertEquals("Stream has been closed abruptly", errorLogLine);
    assertFalse(bufferedReaderIterator.hasNext());
    assertNull(bufferedReaderIterator.next());
  }

  private boolean deleteLogTemp(File logTempFile) {
    boolean deletionSucceeded = delete(logTempFile);
    if (!deletionSucceeded) {
      LOG.warn("Test could not be launched as log temp file could not be deleted.");
    }
    return deletionSucceeded;
  }

  private boolean delete(File logTempFile) {
    int attempts = 0;
    while (logTempFile.exists()) {
      logTempFile.delete();
      if (++attempts == 5) {
        return false;
      }
    }
    return true;
  }
}
