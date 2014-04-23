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
      createBranch(branch);
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
    System.out.append(gitDeleteTrunkCommand).append(Files.LINE_SEPARATOR);
    try {
        Executors.executeCommand(gitDeleteTrunkCommand, gitRepo).waitFor();
    }
    catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
    }
  }

  private void createBranch(String branch) throws InterruptedException, IOException {
    String gitBranchCommand = Strings.append("git checkout ", branch);
    System.out.append(gitBranchCommand).append(Files.LINE_SEPARATOR);
    Executors.executeCommand(gitBranchCommand, gitRepo).waitFor();
    System.out.append(Files.LINE_SEPARATOR);
  }
}
