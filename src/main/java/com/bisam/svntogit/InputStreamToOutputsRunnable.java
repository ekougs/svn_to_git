package com.bisam.svntogit;

import java.io.*;
import java.util.ArrayList;

class InputStreamToOutputsRunnable implements Runnable {
  private int numberOfRetry;
  private File fileForWriting;
  private final InputStream inputStream;
  private final ArrayList<PrintStream> outputStreams = new ArrayList<>();

  public static InputStreamToOutputsRunnable init(InputStream inputStream, File fileForWriting) {
    return new InputStreamToOutputsRunnable(inputStream, fileForWriting);
  }

  public static InputStreamToOutputsRunnable init(InputStream inputStream, PrintStream outputStream) {
    return new InputStreamToOutputsRunnable(inputStream, outputStream);
  }

  public InputStreamToOutputsRunnable add(PrintStream outputStream) {
    outputStreams.add(outputStream);
    return this;
  }

  private InputStreamToOutputsRunnable(InputStream inputStream, File fileForWriting) {
    this.inputStream = inputStream;
    this.fileForWriting = fileForWriting;
    numberOfRetry = 0;
  }

  private InputStreamToOutputsRunnable(InputStream inputStream, PrintStream outputStream) {
    this.inputStream = inputStream;
    this.outputStreams.add(outputStream);
  }

  @Override
  public void run() {
    try {
      while (canBeRead(inputStream)) {
        write();
        numberOfRetry = 0;
      }
    }
    catch (IOException | InterruptedException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private void write() throws IOException {
    try (BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         PrintWriter inputLogWriter = getPrintWriter()) {
      int numberOfOperations = 0;
      for (String nextLine : new BufferedReaderIterable(inputBufferedReader)) {
        if (!outputStreams.isEmpty()) {
          for (PrintStream outputStream : outputStreams) {
            outputStream.append(nextLine).append(System.getProperty("line.separator"));
          }
        }
        if (inputLogWriter != null) {
          inputLogWriter.println(nextLine);
          if (numberOfOperations % 100 == 0) {
            inputLogWriter.flush();
          }
          numberOfOperations++;
        }
      }
    }
  }

  private PrintWriter getPrintWriter() throws FileNotFoundException {
    return fileForWriting == null ? null : new PrintWriter(fileForWriting);
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
}
