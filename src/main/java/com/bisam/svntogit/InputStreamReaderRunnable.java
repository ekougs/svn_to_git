package com.bisam.svntogit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class InputStreamReaderRunnable implements Runnable {
  private int numberOfRetry = 0;
  private final InputStream inputStream;
  private final InputStreamLineHandler inputStreamLineHandler;

  public static InputStreamReaderRunnable init(InputStream inputStream, InputStreamLineHandler inputStreamLineHandler) {
    return new InputStreamReaderRunnable(inputStream, inputStreamLineHandler);
  }

  private InputStreamReaderRunnable(InputStream inputStream, InputStreamLineHandler inputStreamLineHandler) {
    this.inputStream = inputStream;
    this.inputStreamLineHandler = inputStreamLineHandler;
  }

  @Override
  public void run() {
    try {
      while (canBeRead(inputStream)) {
        read();
        numberOfRetry = 0;
      }
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    inputStreamLineHandler.close();
  }

  private void read() throws IOException {
    try (BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
      for (String nextLine : new BufferedReaderIterable(inputBufferedReader)) {
        inputStreamLineHandler.handleLine(nextLine);
      }
    }
  }

  private boolean canBeRead(InputStream inputStream) throws IOException, InterruptedException {
    try {
      Thread.sleep(50);
      return inputStream.available() != 0 || ++numberOfRetry <= 3;
    }
    catch (IOException e) {
      return false;
    }
  }

  public static interface InputStreamLineHandler {
    void handleLine(String inputStreamLine);

    void close();
  }
}
