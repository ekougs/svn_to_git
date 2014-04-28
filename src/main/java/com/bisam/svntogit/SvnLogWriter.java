package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;

class SvnLogWriter {
  private static final String SVN_LOG = "svn.log";
  private static final String ERROR_LOG = "error.log";

  private final String repository;

  private SvnLogWriter(String repository) {
    this.repository = repository;
  }

  static SvnLogWriter init(String svnRepository) {
    return new SvnLogWriter(svnRepository);
  }

  boolean write() throws IOException, InterruptedException {
    File svnLog = new File(getSvnLogFilePath());
    return writeSvnLog(svnLog);
  }

  private boolean writeSvnLog(File svnLog) throws IOException, InterruptedException {
    int exitValue = Executors.executeAll("svn log " + repository, InputStreamToOutputs.init(svnLog).add(System.out), ERROR_LOG);
    return exitValue == 0;
  }

  static String getSvnLogFilePath() {
    return Files.getLocalFilePath(SvnLogWriter.class, SVN_LOG);
  }

}
