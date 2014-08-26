package com.bisam.svntogit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InputStreamReaderRunnable implements Runnable {
  private final BufferedReader inputBufferedReader;
  private final BlockingQueue<String> inputStreamLines = new LinkedBlockingQueue<>();
  private final ProcessEndNotifier processEndNotifier;
  private final InputStreamLineHandler inputStreamLineHandler;
  private final CyclicBarrier barrier = new CyclicBarrier(2);

  public static InputStreamReaderRunnable initForInput(Process process, InputStreamLineHandler inputStreamLineHandler) {
    return new InputStreamReaderRunnable(process.getInputStream(), inputStreamLineHandler, process);
  }

  public static InputStreamReaderRunnable initForError(Process process, InputStreamLineHandler inputStreamLineHandler) {
    return new InputStreamReaderRunnable(process.getErrorStream(), inputStreamLineHandler, process);
  }

  private InputStreamReaderRunnable(InputStream inputStream, InputStreamLineHandler inputStreamLineHandler, Process process) {
    this.inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    this.inputStreamLineHandler = inputStreamLineHandler;
    this.processEndNotifier = new ProcessEndNotifier(process);
  }

  @Override
  public void run() {
    try {
      Executors.ExecutorServiceShutter shutter = Executors.executeInParallel(new InputStreamConsumer(), processEndNotifier);
      read();
      shutter.waitForTasks().shutdown();
      inputBufferedReader.close();
    }
    catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    inputStreamLineHandler.close();
  }

  private void read() throws IOException {
    boolean keepReading = true;
    while (keepReading) {
      String nextLine = inputBufferedReader.readLine();
      if (nextLine != null) {
        inputStreamLines.offer(nextLine);
        synchronized (inputStreamLines){
          inputStreamLines.notifyAll();
        }
      }
      else {
        keepReading = !processEndNotifier.done;
      }
    }
  }

  private boolean canBeRead() {
    try {
      return inputBufferedReader.ready();
    }
    catch (IOException e) {
      return false;
    }
  }

  public static interface InputStreamLineHandler {
    void handleLine(String inputStreamLine);

    void close();

    InputStreamLineHandler NULL = new InputStreamLineHandler() {
      @Override
      public void handleLine(String inputStreamLine) {
      }

      @Override
      public void close() {
      }
    };
  }

  private class InputStreamConsumer implements Runnable {
    @Override
    public void run() {
      boolean consume = true;
      while (consume) {
        try {
          tryHandlingLine();
        }
        catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return;
        }
        consume = !processEndNotifier.done || !inputStreamLines.isEmpty();
      }
    }

    private void tryHandlingLine() throws InterruptedException {
      synchronized (inputStreamLines){
        inputStreamLines.wait(200);
      }
      String inputStreamLine = inputStreamLines.poll();
      if (inputStreamLine != null) {
        inputStreamLineHandler.handleLine(inputStreamLine);
      }
    }
  }

  private static class ProcessEndNotifier implements Runnable {
    private boolean done;
    private final Process process;

    private ProcessEndNotifier(Process process) {
      this.process = process;
    }

    @Override
    public void run() {
      try {
        process.waitFor();
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      finally {
        done = true;
      }
    }
  }
}
