package com.bisam.svntogit.utils;

/**
 * Created by rd on 23/04/14.
 */
public class InputStreamResultProvider implements InputStreamReaderRunnable.InputStreamLineHandler {
  private String result;

  @Override
  public void handleLine(String result) {
    this.result = result;
  }

  @Override
  public void close() {
  }

  public String getResult() {
    return result;
  }
}
