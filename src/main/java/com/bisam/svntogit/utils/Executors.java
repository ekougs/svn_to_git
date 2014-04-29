package com.bisam.svntogit.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class Executors {
  public static Process executeCommand(String command) throws IOException {
    return executeCommand(command, new File(Files.getLocalDirectory(Executors.class)));
  }

  public static Process executeCommand(String command, File directory) throws IOException {
    Runtime rt = Runtime.getRuntime();
    return rt.exec(command, new String[0], directory);
  }

  public static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler, String errorLogFileName)
    throws InterruptedException, IOException {
    return executeAll(command, inputStreamHandler, InputStreamToOutputs.getErrorStreamHandler(errorLogFileName));
  }

  public static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler, String errorLogFileName, File directory)
    throws InterruptedException, IOException {
    return executeAll(command, inputStreamHandler, InputStreamToOutputs.getErrorStreamHandler(errorLogFileName), directory);
  }

  private static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                                InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler, File directory)
    throws InterruptedException, IOException {
    Process process = executeCommand(command, directory);
    return processAll(process, inputStreamHandler, errorStreamHandler);
  }

  public static int processAll(Process process, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                        InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler)
    throws InterruptedException {
    ExecutorServiceShutter executorServiceShutter =
      executeInParallel(InputStreamReaderRunnable.init(process.getInputStream(), inputStreamHandler),
                        InputStreamReaderRunnable.init(process.getErrorStream(), errorStreamHandler));

    int processResult = process.waitFor();
    executorServiceShutter.waitForTasks().shutdown();
    return processResult;
  }

  private static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                                InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler)
    throws InterruptedException, IOException {
    Process process = executeCommand(command);
    ExecutorServiceShutter executorServiceShutter =
      executeInParallel(InputStreamReaderRunnable.init(process.getInputStream(), inputStreamHandler),
                        InputStreamReaderRunnable.init(process.getErrorStream(), errorStreamHandler));

    int processResult = process.waitFor();
    executorServiceShutter.waitForTasks().shutdown();
    return processResult;
  }

  public static ExecutorServiceShutter executeInParallel(Runnable... runnables) {
    if (runnables == null || runnables.length == 0) {
      throw new IllegalArgumentException("Must submit at least one runnable");
    }
    int numberOfThreads = runnables.length;
    final ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);
    for (Runnable runnable : runnables) {
      executorService.execute(new CountingDownRunnable(runnable, countDownLatch));
    }
    return new ExecutorServiceShutter(executorService, countDownLatch);
  }

    public static void executeCommandAndWait(String command, File directory) throws IOException, InterruptedException {
      Process process = executeCommand(command, directory);
      processAll(process, InputStreamReaderRunnable.InputStreamLineHandler.NULL, InputStreamReaderRunnable.InputStreamLineHandler.NULL);
  }

    public static class ExecutorServiceShutter {
    private final ExecutorService executorServiceToShut;
    private final CountDownLatch countDownLatch;

    private ExecutorServiceShutter(ExecutorService executorServiceToShut, CountDownLatch countDownLatch) {
      this.executorServiceToShut = executorServiceToShut;
      this.countDownLatch = countDownLatch;
    }

    public ExecutorServiceShutter waitForTasks() throws InterruptedException {
      countDownLatch.await();
      return this;
    }

    public void shutdown() {
      executorServiceToShut.shutdown();
    }
  }

  private static class CountingDownRunnable implements Runnable {
    private final Runnable runnable;
    private final CountDownLatch countDownLatch;

    public CountingDownRunnable(Runnable runnable, CountDownLatch countDownLatch) {
      this.runnable = runnable;
      this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
      runnable.run();
      countDownLatch.countDown();
    }
  }
}
