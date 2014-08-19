package com.bisam.svntogit.branchrepair;

import com.bisam.svntogit.utils.*;

import java.io.File;
import java.io.IOException;

public class BranchesRepairer {
  static final String BRANCHES_REPAIRER_ERROR = "branch_repair_error.log";
  private static final String TEMP = "_TEMP";

  public static void repair(File gitRepository) throws IOException, InterruptedException {
    Logs.appendln(Gits.GIT_BRANCH_LIST_COMMAND);
    Executors.executeAll(Gits.GIT_BRANCH_LIST_COMMAND, new BranchRepairer(gitRepository),
                         BRANCHES_REPAIRER_ERROR, gitRepository);
  }

  private static class BranchRepairer implements InputStreamReaderRunnable.InputStreamLineHandler {
    private final File gitRepository;
    private BranchesSHA1s branchesSHA1s;

    public BranchRepairer(File gitRepository) {
      this.gitRepository = gitRepository;
    }

    @Override
    public void handleLine(String line) {
      String branchName = Gits.getBranchName(line);
      if (Gits.MASTER.equals(branchName) || Gits.NO_BRANCH.equals(branchName)) {
        return;
      }
      try {
        String gitRevListWithoutParentCommand =
          Strings.append(Gits.GIT_REV_LIST_WITHOUT_PARENT_COMMAND, branchName);
        Logs.appendln(gitRevListWithoutParentCommand);
        InputStreamResultProvider branchCommitAncestorSHA1Provider = new InputStreamResultProvider();
        Executors.executeAll(gitRevListWithoutParentCommand, branchCommitAncestorSHA1Provider,
                             BRANCHES_REPAIRER_ERROR, gitRepository);

        String ancestorSHA1 = branchCommitAncestorSHA1Provider.getResult();
        Logs.appendln(branchName, " ancestor SHA1 : ", ancestorSHA1);
        if (isPluggedToMaster(branchName, ancestorSHA1)) {
          Logs.appendln(branchName, "(", ancestorSHA1, ")", " is plugged to master");
          return;
        }
        String previousSHA1 = getPreviousSHA1OnMaster(branchName, ancestorSHA1);
        Logs.appendln(branchName, " previous SHA1 : ", previousSHA1);

        String tempBranch = Strings.append(branchName, TEMP);
        String tempBranchCreationFromSHA1Command =
          Strings.append(Gits.GIT_CHECKOUT_WITH_SHA1, tempBranch, " ", previousSHA1);
        Logs.appendln(tempBranchCreationFromSHA1Command);
        Executors.executeCommandAndWait(tempBranchCreationFromSHA1Command, gitRepository);

        String linkBranchToTrunkCommand =
          Strings.append(Gits.GIT_REBASE, tempBranch, " ", branchName);
        Logs.appendln(linkBranchToTrunkCommand);
        Executors.executeAll(linkBranchToTrunkCommand, InputStreamToOutputs.initConsole(),
                             BRANCHES_REPAIRER_ERROR, gitRepository);

        String tempBranchDeletionCommand =
          Strings.append(Gits.GIT_BRANCH_DELETION, tempBranch);
        Logs.appendln(tempBranchDeletionCommand);
        Executors.executeCommandAndWait(tempBranchDeletionCommand, gitRepository);
      }
      catch (InterruptedException | IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() {
    }

    private boolean isPluggedToMaster(String branchName, String ancestorSHA1)
      throws IOException, InterruptedException {
      BranchesSHA1s branchesSHA1s = branchesSHA1s();
      return branchesSHA1s.sha1s(branchName).contains(ancestorSHA1) &&
             branchesSHA1s.sha1s(Gits.MASTER).contains(ancestorSHA1);
    }

    private String getPreviousSHA1OnMaster(String branchName, String branchCreationSHA1String)
      throws IOException, InterruptedException {
      BranchesSHA1s branchesSHA1s = branchesSHA1s();
      BranchesSHA1s.SHA1 branchCreationSHA1 = branchesSHA1s.sha1s(branchName).sha1(branchCreationSHA1String);
      BranchesSHA1s.SHA1s masterSHA1s = branchesSHA1s.sha1s(Gits.MASTER);
      BranchesSHA1s.SHA1 previousSHA1OnMaster = masterSHA1s.previous(branchCreationSHA1);
      return previousSHA1OnMaster.sha1();
    }

    private BranchesSHA1s branchesSHA1s() throws IOException, InterruptedException {
      if (branchesSHA1s == null) {
        branchesSHA1s = BranchesSHA1s.get(gitRepository);
      }
      return branchesSHA1s;
    }
  }
}
