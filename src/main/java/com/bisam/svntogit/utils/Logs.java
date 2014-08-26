package com.bisam.svntogit.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Logs {
  private static final Lock APPEND_LOCK = new ReentrantLock();

  public static void appendln(String... elements) {
    appendln(Strings.append(elements));
  }

  public static void appendln(String line) {
    APPEND_LOCK.lock();
    System.out.append(line).append(Files.LINE_SEPARATOR);
    APPEND_LOCK.unlock();
  }

  public static void appendln() {
    appendln(Strings.EMPTY);
  }
}
