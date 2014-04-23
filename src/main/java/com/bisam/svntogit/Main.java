package com.bisam.svntogit;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.bisam.svntogit.Strings.append;
import static com.bisam.svntogit.Strings.isEmptyString;

public class Main {
  private static final String ERROR_LOG = "error_main.log";

  public static void main(String[] args) throws Exception {
    launchGitRepoCreation(args);
  }

  private static void launchGitRepoCreation(String[] args) throws IOException, InterruptedException, ExecutionException {
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args);
    if (isEmptyString(options.getMail())) {
      throw new IllegalArgumentException(
        append("E-mail must be supplied via ", ArgumentsParser.Parameter.AUTHOR_MAIL.getName(), " option if you don't provide a mail supplier."));
    }
    launchGitRepoCreation(options, new SingleMailSupplier(options.getMail()));
  }

  public static void launchGitRepoCreation(String[] args, MailSupplier mailSupplier) throws IOException, InterruptedException, ExecutionException {
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args);
    launchGitRepoCreation(options, mailSupplier);
  }

  private static void launchGitRepoCreation(ArgumentsParser.Options options, MailSupplier mailSupplier)
    throws IOException, InterruptedException, ExecutionException {
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
    createTags(options.getGitRepo());
    logStep(stepStart, "Tags : ");

    stepStart = new Date().getTime();
    createBranches(options.getGitRepo());
    logStep(stepStart, "Branches : ");

    logStep(start, "Total : " );
  }

  static void createTags(String gitRepo) throws IOException, ExecutionException, InterruptedException {
    String tagBranchListCommand = append("git for-each-ref --format=%(refname) ", TagsCreator.PREFIX, "*");
    Executors.executeAll(tagBranchListCommand, new TagsCreator(gitRepo), ERROR_LOG, new File(gitRepo));
  }

  private static boolean writeAuthorFile(ArgumentsParser.Options options, MailSupplier mailSupplier)
    throws IOException, InterruptedException, ExecutionException {
    boolean hasWritten = SvnLogWriter.init(options.getSvnRepo()).write();
    if (!hasWritten) {
      return false;
    }
    AuthorExtractor.Authors authors = AuthorExtractor.init(SvnLogWriter.getSvnLogFilePath()).getAuthors();
    AuthorFileWriter.init(options.getAuthorsFilePath(), mailSupplier).write(authors);
    return true;
  }

  private static void createGitRepo(ArgumentsParser.Options options) throws IOException, ExecutionException, InterruptedException {
    String gitRepo = options.getGitRepo();
    deleteGitRepo(gitRepo);

    String gitSvnCloneCommand =
      append("git svn clone --prefix=svn/ --no-metadata --authors-file=", options.getAuthorsFilePath(), " ", options.getSvnRepo(),
             " ", gitRepo, " --trunk=trunk --tags=tags --branches=branches");

    Executors.executeAll(gitSvnCloneCommand, InputStreamToOutputs.init(System.out), ERROR_LOG);
  }

  private static void createBranches(String gitRepo) throws IOException, ExecutionException, InterruptedException {
    String branchListCommand = append("git for-each-ref --format=%(refname) ", BranchesCreator.PREFIX);
    Executors.executeAll(branchListCommand, new BranchesCreator(gitRepo), ERROR_LOG, new File(gitRepo));
  }

  private static void deleteGitRepo(String gitRepo) throws IOException {
    File gitRepoDirectory = new File(gitRepo);
    if (gitRepoDirectory.exists()) {
      FileUtils.deleteDirectory(gitRepoDirectory);
    }
  }

  static void logStep(long start, String step) {
    System.out.append(Files.LINE_SEPARATOR);
    System.out.append(append(step, String.valueOf((new Date().getTime() - start) / 1000), " s")).append(Files.LINE_SEPARATOR);
    System.out.append(Files.LINE_SEPARATOR);
  }
}
