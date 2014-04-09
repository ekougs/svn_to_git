package com.bisam.svntogit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ExecutorsTest {
  private static final String OS = System.getProperty("os.name");
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private static final String ERROR = "error.log";

  @Test
  public void testMustSubmitAtLeastOneRunnable() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Must submit at least one runnable");
    Executors.executeInParallel();
  }

  @Test
  public void testAllIsExecuted() throws Exception {
    DummyRunnable dummy1 = new DummyRunnable();
    DummyRunnable dummy2 = new DummyRunnable();
    Executors.ExecutorServiceShutter executorServiceShutter =
      Executors.executeInParallel(dummy1, dummy2);
    executorServiceShutter.waitForTasks().shutdown();
    assertTrue(dummy1.executed);
    assertTrue(dummy2.executed);
  }

  @Test
  public void testExecuteAllNoError() throws Exception {
    LastInputRetriever lastInputRetriever = new LastInputRetriever();
    Executors.executeAll(getEchoCommand(), lastInputRetriever, ERROR);
    assertEquals("hello", lastInputRetriever.inputLine.toString());
  }

  @Test
  public void testExecuteAllNoErrorOnFile() throws Exception {
    LastInputRetriever lastInputRetriever = new LastInputRetriever();
    Executors.executeAll(getEchoCommand(), lastInputRetriever, ERROR, new File(Files.getLocalDirectory(ExecutorsTest.class)));
    assertEquals("hello", lastInputRetriever.inputLine.toString());
  }

  @Test
  public void testExecuteAllError() throws Exception {
    LastInputRetriever lastErrorRetriever = new LastInputRetriever();
    Executors.processAll(
      Executors.executeCommand(getCommand("nimporte quoi")), InputStreamReaderRunnable.InputStreamLineHandler.NULL, lastErrorRetriever);
    assertFalse(lastErrorRetriever.inputLine.toString().isEmpty());
  }

  private String getEchoCommand() {
    return getCommand("echo hello");
  }

  private String getCommand(String command) {
    StringBuilder commandBuilder = new StringBuilder();
    if (OS.contains("Win")) {
      commandBuilder.append("cmd.exe /c ");
    }
    else {
      commandBuilder.append("/bin/bash -c ");
    }
    commandBuilder.append(command);
    return commandBuilder.toString();
  }

  private static class DummyRunnable implements Runnable {
    private boolean executed = false;

    @Override
    public void run() {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      executed = true;
    }
  }

  private static class LastInputRetriever implements InputStreamReaderRunnable.InputStreamLineHandler {
    private StringBuilder inputLine = new StringBuilder();

    @Override
    public void handleLine(String inputStreamLine) {
      if (!inputLine.toString().isEmpty()) {
        inputLine.append(Files.LINE_SEPARATOR);
      }
      inputLine.append(inputStreamLine);
    }

    @Override
    public void close() {
    }
  }
}
