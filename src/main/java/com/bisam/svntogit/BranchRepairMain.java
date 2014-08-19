package com.bisam.svntogit;

import com.bisam.svntogit.branchrepair.BranchesRepairer;
import com.bisam.svntogit.utils.Files;
import com.bisam.svntogit.utils.Logs;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class BranchRepairMain {
  public static void main(String[] args) throws IOException, InterruptedException {
    long start = new Date().getTime();
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args, ArgumentsParser.Parameter.GIT_REPO_OPTION);
    String gitRepo = options.getGitRepo();
    if (!Files.fileExists(gitRepo)) {
      Logs.appendln("You provided invalid directory '", gitRepo, "'");
      return;
    }
    BranchesRepairer.repair(new File(gitRepo));
    Main.logStep(start, "Branches repair : ");
  }
}