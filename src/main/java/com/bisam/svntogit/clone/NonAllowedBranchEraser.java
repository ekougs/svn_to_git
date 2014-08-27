package com.bisam.svntogit.clone;

import com.bisam.svntogit.utils.BufferedReaderIterable;
import com.bisam.svntogit.utils.Executors;
import com.bisam.svntogit.utils.Gits;
import com.bisam.svntogit.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NonAllowedBranchEraser {
  private static final Logger LOGGER = LoggerFactory.getLogger(NonAllowedBranchEraser.class);
  private static final String ERROR_LOG = "error_branch_eraser.log";
  private final File gitRepoFile;
  private final String allowedBranchesPath;
  private final Collection<String> existingBranches;

  private NonAllowedBranchEraser(File gitRepoFile, String allowedBranchesPath, Collection<String> existingBranches) {
    this.gitRepoFile = gitRepoFile;
    this.allowedBranchesPath = allowedBranchesPath;
    this.existingBranches = existingBranches;
  }

  public static NonAllowedBranchEraser init(String gitRepo, String allowedBranchesPath, Collection<String> existingBranches) {
    return new NonAllowedBranchEraser(new File(gitRepo), allowedBranchesPath, existingBranches);
  }

  public void remove() throws IOException, InterruptedException {
    final Set<String> allowedBranches = new HashSet<>();
    try (FileReader allowedBranchesFileReader = new FileReader(allowedBranchesPath);
         BufferedReader allowedBranchesBufferedReader = new BufferedReader(allowedBranchesFileReader)) {
      for (String branch : new BufferedReaderIterable(allowedBranchesBufferedReader)) {
        String branchName = branch.trim();
        if (existingBranches.contains(branchName)) {
          LOGGER.debug("Branch has been allowed ", branchName);
          allowedBranches.add(branchName);
        }
        else {
          logIncorrectFile("Branch does not exist '", branchName, "', check your file.");
          return;
        }
      }
      if (allowedBranches.isEmpty()) {
        logIncorrectFile("No allowed branch found, check your file.");
        return;
      }
      NonAllowedBranchesRetriever retriever = new NonAllowedBranchesRetriever(allowedBranches);
      Gits.iterateOnBranches(gitRepoFile, retriever, ERROR_LOG);
      StringBuilder branchDeletionCommandBuilder = new StringBuilder(Strings.append(Gits.GIT_BRANCH_DELETION));
      for (String branchToDelete : retriever.branchesToDelete) {
        branchDeletionCommandBuilder.append(branchToDelete).append(" ");
      }
      String branchDeletionCommand = branchDeletionCommandBuilder.toString();
      LOGGER.debug(branchDeletionCommand);
      Executors.executeCommandAndWait(branchDeletionCommand, gitRepoFile);
    }
    catch (FileNotFoundException e) {
      LOGGER.debug("Exception : No file found for allowedBranches : '", allowedBranchesPath, "'");
    }
  }

  private void logIncorrectFile(String... error) {
    LOGGER.debug(Strings.append(error));
    LOGGER.debug("Existing branches : ", existingBranches.toString());
  }

  private static class NonAllowedBranchesRetriever implements Gits.BranchConsumer {
    private final Set<String> branchesToDelete;
    private final Set<String> allowedBranches;

    public NonAllowedBranchesRetriever(Set<String> allowedBranches) {
      this.allowedBranches = allowedBranches;
      branchesToDelete = new HashSet<>();
    }

    @Override
    public void consume(String branchName) {
      if (canBeDeleted(branchName)) {
        LOGGER.debug("Branch '", branchName, "' will be deleted");
        branchesToDelete.add(branchName);
      }
    }

    private boolean canBeDeleted(String branchName) {
      return !Gits.MASTER.equals(branchName) && !allowedBranches.contains(branchName);
    }
  }
}
