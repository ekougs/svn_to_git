package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;

class BranchesCreator implements InputStreamReaderRunnable.InputStreamLineHandler {
  public static final String PREFIX = "refs/remotes/svn/";
  private static final String TRUNK = "refs/remotes/svn/trunk";
  private final File gitRepo;

  BranchesCreator(String gitRepo) {
    this.gitRepo = new File(gitRepo);
  }

  @Override
  public void handleLine(String originalBranch) {
    if (TRUNK.equals(originalBranch)) {
      return;
    }
    String branch = originalBranch.substring(PREFIX.length());
    try {
      String gitBranchCommand = Strings.append("git branch -t ", branch, " ", originalBranch);
      System.out.append(gitBranchCommand).append(Files.LINE_SEPARATOR);
      Executors.executeCommand(gitBranchCommand, gitRepo).waitFor();
      String updateRefTagCommand = Strings.append("git update-ref -d ", originalBranch);
      System.out.append(updateRefTagCommand).append(Files.LINE_SEPARATOR);
      Executors.executeCommand(updateRefTagCommand, gitRepo).waitFor();
      System.out.append(Files.LINE_SEPARATOR);
    }
    catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
  }
}
