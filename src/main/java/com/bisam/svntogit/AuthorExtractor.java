package com.bisam.svntogit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class AuthorExtractor {
  public static final String LOG_INFO_SEPARATOR = " | ";
  public static final String LOG_INFO_SEPARATOR_REGEX = " \\| ";
  private final String logFileLocation;

  private AuthorExtractor(String logFileLocation) {
    this.logFileLocation = logFileLocation;
  }

  public static AuthorExtractor init(String logFileLocation) throws IOException {
    File logFile = new File(logFileLocation);
    if (!logFile.isFile()) {
      throw new IllegalArgumentException("Log file location must point to a file.");
    }
    return new AuthorExtractor(logFileLocation);
  }

  public Authors getAuthors() throws IOException {
    try (BufferedReader logFileReader = new BufferedReader(new FileReader(logFileLocation))) {
      Authors authors = new Authors();
      String nextLine = logFileReader.readLine();
      while (nextLine != null) {
        if (nextLine.contains(LOG_INFO_SEPARATOR)) {
          String[] infoElements = nextLine.split(LOG_INFO_SEPARATOR_REGEX);
          if (infoElements.length == 4) {
            String authorName = infoElements[1].trim();
            authors.add(authorName);
          }
        }
        nextLine = logFileReader.readLine();
      }
      return authors;
    }
  }

  public class Authors implements Iterable<String> {
    private final HashSet<String> authors = new HashSet<>();

    private void add(String author) {
      authors.add(author);
    }

    public Iterator<String> iterator() {
      return authors.iterator();
    }
  }
}
