package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class Executors {
  static Process executeCommand(String command) throws IOException {
    return executeCommand(command, new File(Files.getLocalDirectory(Executors.class)));
  }

  static Process executeCommand(String command, File file) throws IOException {
    Runtime rt = Runtime.getRuntime();
    return rt.exec(command, new String[0], file);
  }

  static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler, String errorLogFileName)
    throws ExecutionException, InterruptedException, IOException {
    return executeAll(command, inputStreamHandler, InputStreamToOutputs.getErrorStreamHandler(errorLogFileName));
  }

  static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler, String errorLogFileName, File file)
    throws ExecutionException, InterruptedException, IOException {
    return executeAll(command, inputStreamHandler, InputStreamToOutputs.getErrorStreamHandler(errorLogFileName), file);
  }

  private static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                                InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler, File file)
    throws ExecutionException, InterruptedException, IOException {
    Process process = executeCommand(command, file);
    return processAll(process, inputStreamHandler, errorStreamHandler);
  }

  static int processAll(Process process, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                        InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler)
    throws ExecutionException, InterruptedException, IOException {
    ExecutorServiceShutter executorServiceShutter =
      executeInParallel(InputStreamReaderRunnable.init(process.getInputStream(), inputStreamHandler),
                        InputStreamReaderRunnable.init(process.getErrorStream(), errorStreamHandler));

    int processResult = process.waitFor();
    executorServiceShutter.waitForTasks().shutdown();
    return processResult;
  }

  private static int executeAll(String command, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                                InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler)
    throws ExecutionException, InterruptedException, IOException {
    Process process = executeCommand(command);
    ExecutorServiceShutter executorServiceShutter =
      executeInParallel(InputStreamReaderRunnable.init(process.getInputStream(), inputStreamHandler),
                        InputStreamReaderRunnable.init(process.getErrorStream(), errorStreamHandler));

    int processResult = process.waitFor();
    executorServiceShutter.waitForTasks().shutdown();
    return processResult;
  }

  static ExecutorServiceShutter executeInParallel(Runnable... runnables) throws ExecutionException, InterruptedException {
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

  public static class ExecutorServiceShutter {
    private final ExecutorService executorServiceToShut;
    private final CountDownLatch countDownLatch;

    private ExecutorServiceShutter(ExecutorService executorServiceToShut, CountDownLatch countDownLatch) {
      this.executorServiceToShut = executorServiceToShut;
      this.countDownLatch = countDownLatch;
    }

    public ExecutorServiceShutter waitForTasks() throws ExecutionException, InterruptedException {
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
