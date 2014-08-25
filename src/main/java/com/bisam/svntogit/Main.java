package com.bisam.svntogit;

import com.bisam.svntogit.branchrepair.BranchesRepairer;
import com.bisam.svntogit.clone.*;
import com.bisam.svntogit.utils.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.bisam.svntogit.utils.Strings.append;
import static com.bisam.svntogit.utils.Strings.isEmptyString;

public class Main {
  private static final String ERROR_LOG = "error_main.log";
  private static final String GIT_SVN_LOG = "git_svn.log";

  public static void main(String[] args) throws Exception {
    launchGitRepoCreation(args);
  }

  private static void launchGitRepoCreation(String[] args) throws IOException, InterruptedException {
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args, ArgumentsParser.Parameter.SVN_REPO_OPTION);
    if (isEmptyString(options.getMail())) {
      throw new IllegalArgumentException(
        append("E-mail must be supplied via ", ArgumentsParser.Parameter.AUTHOR_MAIL.getName(), " option if you don't provide a mail supplier."));
    }
    launchGitRepoCreation(options, new SingleMailSupplier(options.getMail()));
  }

  public static void launchGitRepoCreation(String[] args, MailSupplier mailSupplier)
    throws IOException, InterruptedException {
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args);
    launchGitRepoCreation(options, mailSupplier);
  }

  private static void launchGitRepoCreation(ArgumentsParser.Options options, MailSupplier mailSupplier)
    throws IOException, InterruptedException {
    long start = new Date().getTime();
    long stepStart;
    if (!options.isAuthorsFileProvided()) {
      stepStart = new Date().getTime();
      boolean authorsFileReady = writeAuthorFile(options, mailSupplier);
      if (!authorsFileReady) {
        return;
      }
      logStep(stepStart, "Author file : ");
    }

    stepStart = new Date().getTime();
    createGitRepo(options);
    logStep(stepStart, "Clone : ");

    stepStart = new Date().getTime();
    String gitRepo = options.getGitRepo();
    createTags(gitRepo);
    logStep(stepStart, "Tags : ");

    stepStart = new Date().getTime();
    createBranches(gitRepo, options.getAllowedBranchesPath());
    logStep(stepStart, "Branches : ");

    if (options.repairBranches()) {
      stepStart = new Date().getTime();
      BranchesRepairer.repair(new File(gitRepo));
      logStep(stepStart, "Branches repair : ");
    }

    logStep(start, "Total : ");
  }

  static void createTags(String gitRepo) throws IOException, InterruptedException {
    String tagBranchListCommand = append("git for-each-ref --format=%(refname) ", TagsCreator.PREFIX, "*");
    Executors.executeAll(tagBranchListCommand, new TagsCreator(gitRepo), ERROR_LOG, new File(gitRepo));
  }

  private static boolean writeAuthorFile(ArgumentsParser.Options options, MailSupplier mailSupplier)
    throws IOException, InterruptedException {
    boolean hasWritten = SvnLogWriter.init(options.getSvnRepo()).write();
    if (!hasWritten) {
      return false;
    }
    AuthorExtractor.Authors authors = AuthorExtractor.init(SvnLogWriter.getSvnLogFilePath()).getAuthors();
    AuthorFileWriter.init(options.getAuthorsFilePath(), mailSupplier).write(authors);
    return true;
  }

  private static void createGitRepo(ArgumentsParser.Options options) throws IOException, InterruptedException {
    String gitRepo = options.getGitRepo();
    deleteGitRepo(gitRepo);

    String gitSvnCloneCommand =
      append("git svn clone --prefix=svn/ --no-metadata --authors-file=", options.getAuthorsFilePath(), " ", options.getSvnRepo(),
             " ", gitRepo, " --trunk=trunk --tags=tags --branches=branches");

    Executors.executeAll(gitSvnCloneCommand, InputStreamToOutputs.init(Files.getLocalFile(Main.class, GIT_SVN_LOG)).addConsole(),
                         ERROR_LOG);
  }

  private static void createBranches(String gitRepo, String allowedBranchesPath) throws IOException, InterruptedException {
    String branchListCommand = append("git for-each-ref --format=%(refname) ", BranchesCreator.PREFIX);
    File gitRepoFile = new File(gitRepo);
    BranchesCreator branchesCreator = new BranchesCreator(gitRepo);
    Executors.executeAll(branchListCommand, branchesCreator, ERROR_LOG, gitRepoFile);
    if (Strings.isEmptyString(allowedBranchesPath)) {
      return;
    }
    if (Files.fileExists(allowedBranchesPath)) {
      NonAllowedBranchEraser.init(gitRepo, allowedBranchesPath, branchesCreator.getExistingBranches()).remove();
    }
    else {
      Logs.appendln("Allowed branch file not found '", allowedBranchesPath, "'");
    }
  }

  private static void deleteGitRepo(String gitRepo) throws IOException {
    File gitRepoDirectory = new File(gitRepo);
    if (gitRepoDirectory.exists()) {
      FileUtils.deleteDirectory(gitRepoDirectory);
    }
  }

  static void logStep(long start, String step) {
    Logs.appendln();
    Logs.appendln(append(step, String.valueOf((new Date().getTime() - start) / 1000), " s"));
    Logs.appendln();
  }
}
