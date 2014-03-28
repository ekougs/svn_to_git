package com.bisam.svntogit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

class InputStreamToOutputs implements InputStreamReaderRunnable.InputStreamLineHandler {
  private final ArrayList<PrintStream> outputStreams = new ArrayList<>();
  private int numberOfOperations = 0;
  private PrintWriter inputLogWriter;

  public static InputStreamToOutputs init(File fileForWriting) throws FileNotFoundException {
    return new InputStreamToOutputs(fileForWriting);
  }

  public static InputStreamToOutputs init(PrintStream outputStream) {
    return new InputStreamToOutputs(outputStream);
  }

  static InputStreamToOutputs getErrorStreamHandler(String errorLogFileName) throws FileNotFoundException {
    return init(new File(Files.getLocalFilePath(Main.class, errorLogFileName)));
  }

  public InputStreamToOutputs add(PrintStream outputStream) {
    outputStreams.add(outputStream);
    return this;
  }

  private InputStreamToOutputs(File fileForWriting) throws FileNotFoundException {
    inputLogWriter = getPrintWriter(fileForWriting);
  }

  private InputStreamToOutputs(PrintStream outputStream) {
    this.outputStreams.add(outputStream);
  }

  private PrintWriter getPrintWriter(File fileForWriting) throws FileNotFoundException {
    return fileForWriting == null ? null : new PrintWriter(fileForWriting);
  }

  @Override
  public void handleLine(String inputStreamLine) {
    if (!outputStreams.isEmpty()) {
      for (PrintStream outputStream : outputStreams) {
        outputStream.append(inputStreamLine).append(Files.LINE_SEPARATOR);
      }
    }
    if (inputLogWriter != null) {
      inputLogWriter.println(inputStreamLine);
      if (numberOfOperations % 100 == 0) {
        inputLogWriter.flush();
      }
      numberOfOperations++;
    }
  }

  @Override
  public void close() {
    if (inputLogWriter != null) {
      inputLogWriter.close();
    }
  }
}
