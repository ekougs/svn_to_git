package com.bisam.svntogit;

import com.bisam.svntogit.branchrepair.BranchesRepairer;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class BranchRepairMain {
  public static void main(String[] args) throws IOException, InterruptedException {
    long start = new Date().getTime();
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args);
    BranchesRepairer.repair(new File(options.getGitRepo()));
    Main.logStep(start, "Branches repair : ");
  }
}
