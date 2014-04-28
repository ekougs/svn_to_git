package com.bisam.svntogit;

/**
* Created by rd on 23/04/14.
*/
class InputStreamResultProvider implements InputStreamReaderRunnable.InputStreamLineHandler {
  private String result;

  @Override
  public void handleLine(String result) {
    this.result = result;
  }

  @Override
  public void close() {
  }

  String getResult() {
      return result;
  }
}
