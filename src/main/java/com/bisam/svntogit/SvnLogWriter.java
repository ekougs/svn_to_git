package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SvnLogWriter {
  private static final String SVN_LOG = "svn.log";
  private static final String ERROR_LOG = "error.log";

  private final String repository;

  private SvnLogWriter(String repository) {
    this.repository = repository;
  }

  static SvnLogWriter init(String svnRepository) {
    return new SvnLogWriter(svnRepository);
  }

  boolean write() throws IOException, InterruptedException, ExecutionException {
    File svnLog = new File(getSvnLogFilePath());
    return writeSvnLog(svnLog);
  }

  private boolean writeSvnLog(File svnLog) throws IOException, InterruptedException, ExecutionException {
    final Process process = Executors.executeCommand("svn log " + repository);
    int exitValue = Executors.executeAll(process, InputStreamToOutputs.init(svnLog).add(System.out),
                                         InputStreamToOutputs.init(new File(Files.getLocalFilePath(SvnLogWriter.class, ERROR_LOG))));

    return exitValue == 0;
  }

  static String getSvnLogFilePath() {
    return Files.getLocalFilePath(SvnLogWriter.class, SVN_LOG);
  }

}
