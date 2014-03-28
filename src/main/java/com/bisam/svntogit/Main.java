package com.bisam.svntogit;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Main {
  private static final String ERROR_LOG = "error.log";

  public static void main(String[] args) throws Exception {
    long start;
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args);
    if (!options.isAuthorsFileProvided()) {
      start = new Date().getTime();
      boolean authorsFileReady = writeAuthorFile(options);
      if (!authorsFileReady) {
        return;
      }
      System.out.append(Files.LINE_SEPARATOR);
      System.out.append(Files.append("Author file : ", String.valueOf((new Date().getTime() - start) / 1000), " s")).append(Files.LINE_SEPARATOR);
      System.out.append(Files.LINE_SEPARATOR);
    }

    start = new Date().getTime();
    createGitRepo(options);
    System.out.append(Files.LINE_SEPARATOR);
    System.out.append(Files.append("Clone : ", String.valueOf((new Date().getTime() - start) / 1000), " s")).append(Files.LINE_SEPARATOR);
    System.out.append(Files.LINE_SEPARATOR);

    start = new Date().getTime();
    createTags(options.getGitRepo());
    System.out.append(Files.LINE_SEPARATOR);
    System.out.append(Files.append("Tags : ", String.valueOf((new Date().getTime() - start) / 1000), " s")).append(Files.LINE_SEPARATOR);
    System.out.append(Files.LINE_SEPARATOR);

    start = new Date().getTime();
    createBranches(options.getGitRepo());
    System.out.append(Files.append("Branches : ", String.valueOf((new Date().getTime() - start) / 1000), " s")).append(Files.LINE_SEPARATOR);
  }

  private static boolean writeAuthorFile(ArgumentsParser.Options options) throws IOException, InterruptedException, ExecutionException {
    boolean hasWritten = SvnLogWriter.init(options.getSvnRepo()).write();
    if (!hasWritten) {
      return false;
    }
    AuthorExtractor.Authors authors = AuthorExtractor.init(SvnLogWriter.getSvnLogFilePath()).getAuthors();
    AuthorFileWriter.init(options.getAuthorsFilePath()).write(authors);
    return true;
  }

  private static void createGitRepo(ArgumentsParser.Options options) throws IOException, ExecutionException, InterruptedException {
    String gitRepo = options.getGitRepo();
    deleteGitRepo(gitRepo);

    String gitSvnCloneCommand =
      Files.append("git svn clone --prefix=svn/ --no-metadata --authors-file=", options.getAuthorsFilePath(), " ", options.getSvnRepo(), " ", gitRepo,
                   " --trunk=trunk --tags=tags --branches=branches");

    Executors.executeAll(gitSvnCloneCommand, InputStreamToOutputs.init(System.out), ERROR_LOG);
  }

  private static void createTags(String gitRepo) throws IOException, ExecutionException, InterruptedException {
    String tagBranchListCommand = Files.append("git for-each-ref --format=%(refname) ", TagsCreator.PREFIX, "*");
    Executors.executeAll(tagBranchListCommand, new TagsCreator(gitRepo), ERROR_LOG, new File(gitRepo));
  }

  private static void createBranches(String gitRepo) throws IOException, ExecutionException, InterruptedException {
    String branchListCommand = Files.append("git for-each-ref --format=%(refname) ", BranchsCreator.PREFIX);
    Executors.executeAll(branchListCommand, new BranchsCreator(gitRepo), ERROR_LOG, new File(gitRepo));
  }

  private static void deleteGitRepo(String gitRepo) throws IOException {
    File gitRepoDirectory = new File(gitRepo);
    if (gitRepoDirectory.exists()) {
      FileUtils.deleteDirectory(gitRepoDirectory);
    }
  }
}
