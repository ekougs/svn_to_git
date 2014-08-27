package com.bisam.svntogit.clone;

import com.bisam.svntogit.utils.Executors;
import com.bisam.svntogit.utils.InputStreamReaderRunnable;
import com.bisam.svntogit.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BranchesCreator implements InputStreamReaderRunnable.InputStreamLineHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(BranchesCreator.class);
  public static final String PREFIX = "refs/remotes/svn/";
  private static final String TRUNK = "refs/remotes/svn/trunk";
  private final File gitRepo;
  private final List<String> branches = new ArrayList<>();

  public BranchesCreator(String gitRepo) {
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

  public Collection<String> getExistingBranches() {
    return Collections.unmodifiableCollection(branches);
  }

  private void deleteSvnTrunkBranch(String originalBranch) {
    String gitDeleteTrunkCommand = Strings.append("git branch -D ", originalBranch);
    LOGGER.debug(gitDeleteTrunkCommand);
    try {
      Executors.executeCommand(gitDeleteTrunkCommand, gitRepo).waitFor();
    }
    catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void createBranch(String branch, String originalBranch) throws InterruptedException, IOException {
    String gitBranchCommand = Strings.append("git branch -t ", branch, " ", originalBranch);
    LOGGER.debug(gitBranchCommand);
    Executors.executeCommand(gitBranchCommand, gitRepo).waitFor();
    String updateRefTagCommand = Strings.append("git update-ref -d ", originalBranch);
    LOGGER.debug(updateRefTagCommand);
    Executors.executeCommand(updateRefTagCommand, gitRepo).waitFor();
    branches.add(branch);
    LOGGER.debug("");
  }
}
