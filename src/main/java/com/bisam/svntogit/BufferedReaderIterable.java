package com.bisam.svntogit;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

class BufferedReaderIterable implements Iterable<String> {
  private final BufferedReader bufferedReader;

  BufferedReaderIterable(BufferedReader bufferedReader) {
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

    private BufferedReaderIterator(BufferedReader bufferedReader) {
      this.bufferedReader = bufferedReader;
    }

    @Override
    public boolean hasNext() {
      retrieveNextLineIfNecessary();
      nextLineRead = true;
      return nextLine != null;
    }

    @Override
    public String next() {
      retrieveNextLineIfNecessary();
      nextLineRead = false;
      return nextLine;
    }

    private void retrieveNextLineIfNecessary() {
      if (!nextLineRead) {
        try {
          nextLine = bufferedReader.readLine();
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove on this iterator");
    }
  }
}
