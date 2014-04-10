package com.bisam.svntogit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

public class FileTestCase {
  static final LinkedList<String> EXPECTED_LOG_LINES = new LinkedList<>();

  static {
    FileTestCase.EXPECTED_LOG_LINES.add("Branch 0 creation");
    FileTestCase.EXPECTED_LOG_LINES.add("------------------------------------------------------------------------");
    FileTestCase.EXPECTED_LOG_LINES.add("r5 | pascal | 2014-03-18 16:58:50 +0100 (mar., 18 mars 2014) | 1 line");
    FileTestCase.EXPECTED_LOG_LINES.add(Strings.EMPTY);
    FileTestCase.EXPECTED_LOG_LINES.add("Start a common conversation");
    FileTestCase.EXPECTED_LOG_LINES.add("------------------------------------------------------------------------");
    FileTestCase.EXPECTED_LOG_LINES.add("r4 | sennen | 2014-03-18 16:54:35 +0100 (mar., 18 mars 2014) | 1 line");
    FileTestCase.EXPECTED_LOG_LINES.add(Strings.EMPTY);
    FileTestCase.EXPECTED_LOG_LINES.add("Tags creation");
    FileTestCase.EXPECTED_LOG_LINES.add("------------------------------------------------------------------------");
    FileTestCase.EXPECTED_LOG_LINES.add("r3 | sennen | 2014-03-18 16:54:23 +0100 (mar., 18 mars 2014) | 1 line");
    FileTestCase.EXPECTED_LOG_LINES.add(Strings.EMPTY);
    FileTestCase.EXPECTED_LOG_LINES.add("Branches creation");
    FileTestCase.EXPECTED_LOG_LINES.add("------------------------------------------------------------------------");
    FileTestCase.EXPECTED_LOG_LINES.add("r2 | sennen | 2014-03-18 16:54:07 +0100 (mar., 18 mars 2014) | 1 line");
    FileTestCase.EXPECTED_LOG_LINES.add(Strings.EMPTY);
    FileTestCase.EXPECTED_LOG_LINES.add("Trunk creation");
    FileTestCase.EXPECTED_LOG_LINES.add("------------------------------------------------------------------------");
    FileTestCase.EXPECTED_LOG_LINES.add("r1 | sennen | 2014-03-18 16:53:51 +0100 (mar., 18 mars 2014) | 1 line");
    FileTestCase.EXPECTED_LOG_LINES.add(Strings.EMPTY);
    FileTestCase.EXPECTED_LOG_LINES.add("Project to port repo creation");
    FileTestCase.EXPECTED_LOG_LINES.add("------------------------------------------------------------------------");
  }

  public static BufferedReaderIterable createBufferedReaderIterable(String fileName) throws FileNotFoundException {
    return new BufferedReaderIterable(new BufferedReader(new FileReader(fileName)));
  }
}
