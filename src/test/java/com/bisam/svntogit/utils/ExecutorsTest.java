package com.bisam.svntogit.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

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
        assertFalse(hasError());
    }

    @Test
    public void testExecuteAllNoErrorOnFile() throws Exception {
        LastInputRetriever lastInputRetriever = new LastInputRetriever();
        Executors.executeAll(getEchoCommand(),
                             lastInputRetriever,
                             ERROR,
                             new File(Files.getLocalDirectory(ExecutorsTest.class)));
        assertFalse(hasError());
    }

    @Test
    public void testExecuteAllError() throws Exception {
        LastInputRetriever lastErrorRetriever = new LastInputRetriever();
        Executors.processAll(
                Executors.executeCommand(getCommand("nimporte quoi")),
                InputStreamReaderRunnable.InputStreamLineHandler.NULL,
                lastErrorRetriever);
        assertFalse(lastErrorRetriever.inputLine.toString().trim().isEmpty());
    }

    private boolean hasError() throws FileNotFoundException {
        Iterator<String> errorFileLineIterator =
                new BufferedReaderIterable(
                        new BufferedReader(new FileReader(Files.getLocalFilePath(ExecutorsTest.class, ERROR))))
                        .iterator();
        return errorFileLineIterator.hasNext();
    }

    private String getEchoCommand() {
        return getCommand("echo hello");
    }

    private String getCommand(String command) {
        StringBuilder commandBuilder = new StringBuilder();
        if (OS.contains("Win")) {
            commandBuilder.append("cmd.exe /c ");
        } else {
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
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executed = true;
        }
    }

    private static class LastInputRetriever implements InputStreamReaderRunnable.InputStreamLineHandler {
        private final StringBuilder inputLine = new StringBuilder();

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
