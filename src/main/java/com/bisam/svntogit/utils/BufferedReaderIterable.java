package com.bisam.svntogit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class BufferedReaderIterable implements Iterable<String> {
  private final BufferedReader bufferedReader;

  public BufferedReaderIterable(BufferedReader bufferedReader) {
    this.bufferedReader = bufferedReader;
  }

  @Override
  public Iterator<String> iterator() {
    return new BufferedReaderIterator(bufferedReader);
  }

  private static class BufferedReaderIterator implements Iterator<String> {
    private final BufferedReader bufferedReader;
    private boolean nextLineRead = false;
    private String nextLine;
    private boolean streamClosed;

    private BufferedReaderIterator(BufferedReader bufferedReader) {
      this.bufferedReader = bufferedReader;
    }

    @Override
    public boolean hasNext() {
      boolean streamClosed = this.streamClosed;
      retrieveNextLineIfNecessary();
      nextLineRead = true;
      return !streamClosed && nextLine != null;
    }

    @Override
    public String next() {
      retrieveNextLineIfNecessary();
      nextLineRead = false;
      String nextLine = this.nextLine;
      this.nextLine = null;
      return nextLine;
    }

    private void retrieveNextLineIfNecessary() {
      if (!nextLineRead && !streamClosed) {
        try {
          nextLine = bufferedReader.readLine();
        }
        catch (IOException e) {
          streamClosed = true;
          nextLine = "Stream has been closed abruptly";
        }
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove on this iterator");
    }
  }
}
