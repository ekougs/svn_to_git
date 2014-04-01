package com.bisam.svntogit;

import java.io.*;
import java.util.ArrayList;

class InputStreamToOutputs implements InputStreamReaderRunnable.InputStreamLineHandler {
    private final ArrayList<PrintStream> outputStreams = new ArrayList<>();
    private int numberOfOperations = 0;
    private PrintWriter inputLogWriter;

    public static InputStreamToOutputs init(File fileForWriting) throws IOException {
        return new InputStreamToOutputs(fileForWriting);
    }

    public static InputStreamToOutputs init(PrintStream outputStream) {
        return new InputStreamToOutputs(outputStream);
    }

    static InputStreamToOutputs getErrorStreamHandler(String errorLogFileName) throws IOException {
        return init(new File(Files.getLocalFilePath(Main.class, errorLogFileName)));
    }

    public InputStreamToOutputs add(PrintStream outputStream) {
        outputStreams.add(outputStream);
        return this;
    }

    private InputStreamToOutputs(File fileForWriting) throws IOException {
        inputLogWriter = getPrintWriter(fileForWriting);
    }

    private InputStreamToOutputs(PrintStream outputStream) {
        this.outputStreams.add(outputStream);
    }

    private PrintWriter getPrintWriter(File fileForWriting) throws IOException {
        return fileForWriting == null ? null : new PrintWriter(new FileWriter(fileForWriting, true));
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
