package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Executors {
  static Process executeCommand(String command) throws IOException {
    Runtime rt = Runtime.getRuntime();
    return rt.exec(command);
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

  static ExecutorServiceShutter executeInParallel(Runnable... runnables)
    throws ExecutionException, InterruptedException {
    if (runnables == null) {
      throw new IllegalArgumentException("Must submit at least one runnable");
    }
    int numberOfThreads = runnables.length;
    final ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(numberOfThreads);
    Future<Boolean>[] futures = (Future<Boolean>[])new Future[numberOfThreads];
    int index = 0;
    for (Runnable runnable : runnables) {
      futures[index] = executorService.submit(runnable, true);
      index++;
    }
    return new ExecutorServiceShutter(executorService, futures);
  }

  public static class ExecutorServiceShutter {
    private final ExecutorService executorServiceToShut;
    private final Future<Boolean>[] futures;

    private ExecutorServiceShutter(ExecutorService executorServiceToShut, Future<Boolean>[] futures) {
      this.executorServiceToShut = executorServiceToShut;
      this.futures = futures;
    }

    public ExecutorServiceShutter waitForTasks() throws ExecutionException, InterruptedException {
      for (Future<Boolean> future : futures) {
        future.get();
      }
      return this;
    }

    public void shutdown() {
      executorServiceToShut.shutdown();
    }
  }
}
