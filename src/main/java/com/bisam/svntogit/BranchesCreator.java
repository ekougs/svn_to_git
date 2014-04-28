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
      deleteSvnTrunkBranch(originalBranch);
      return;
    }
    String branch = originalBranch.substring(PREFIX.length());
    try {
      createBranch(branch, originalBranch);
    }
    catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
  }

  private void deleteSvnTrunkBranch(String originalBranch) {
    String gitDeleteTrunkCommand = Strings.append("git branch -D ", originalBranch);
    Logs.appendln(gitDeleteTrunkCommand);
    try {
        Executors.executeCommand(gitDeleteTrunkCommand, gitRepo).waitFor();
    }
    catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
    }
  }

  private void createBranch(String branch, String originalBranch) throws InterruptedException, IOException {
    String gitBranchCommand = Strings.append("git branch -t ", branch, " ", originalBranch);
    Logs.appendln(gitBranchCommand);
    Executors.executeCommand(gitBranchCommand, gitRepo).waitFor();
    String updateRefTagCommand = Strings.append("git update-ref -d ", originalBranch);
    Logs.appendln(updateRefTagCommand);
    Executors.executeCommand(updateRefTagCommand, gitRepo).waitFor();
    Logs.appendln();
  }
}
