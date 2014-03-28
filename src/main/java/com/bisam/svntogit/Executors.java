package com.bisam.svntogit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Executors {
  static Process executeCommand(String command) throws IOException {
    Runtime rt = Runtime.getRuntime();
    return rt.exec(command);
  }

  static int executeAll(Process process, InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                        InputStreamReaderRunnable.InputStreamLineHandler errorStreamHandler)
    throws ExecutionException, InterruptedException, FileNotFoundException {
    ExecutorServiceShutter executorServiceShutter =
      executeInParallel(InputStreamReaderRunnable.init(process.getInputStream(), inputStreamHandler),
                        InputStreamReaderRunnable.init(process.getErrorStream(), errorStreamHandler));

    int processResult = process.waitFor();
    executorServiceShutter.shutdown();
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

    public void shutdown() throws ExecutionException, InterruptedException {
      for (Future<Boolean> future : futures) {
        future.get();
      }
      executorServiceToShut.shutdown();
    }
  }
}
