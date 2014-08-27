package com.bisam.svntogit;

import com.bisam.svntogit.branchrepair.BranchesRepairer;
import com.bisam.svntogit.utils.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class BranchRepairMain {
  private static final Logger LOGGER = LoggerFactory.getLogger(BranchRepairMain.class);

  public static void main(String[] args) throws IOException, InterruptedException {
    long start = new Date().getTime();
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args, ArgumentsParser.Parameter.GIT_REPO_OPTION);
    String gitRepo = options.getGitRepo();
    if (!Files.fileExists(gitRepo)) {
      LOGGER.debug("You provided invalid directory '", gitRepo, "'");
      return;
    }
    BranchesRepairer.repair(new File(gitRepo));
    Main.logStep(start, "Branches repair : ");
  }
}
