package com.bisam.svntogit;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorUtils {
  public static Runnable executeAll(Runnable runnableThatMustFinishBeforePoolShutdown, Runnable... runnables)
    throws ExecutionException, InterruptedException {
    int numberOfThreads = runnables == null ? 1 : runnables.length + 1;
    final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    final Future<Boolean> future = executorService.submit(runnableThatMustFinishBeforePoolShutdown, true);
    if (runnables != null) {
      for (Runnable runnable : runnables) {
        executorService.submit(runnable);
      }
    }
    Boolean aBoolean = future.get();
    executorService.shutdown();
    return new Runnable() {
      @Override
      public void run() {
//        try {
//          Boolean aBoolean = future.get();
//        }
//        catch (InterruptedException | ExecutionException e) {
//          throw new RuntimeException(e);
//        }
//        executorService.shutdown();
      }
    };
  }
}
