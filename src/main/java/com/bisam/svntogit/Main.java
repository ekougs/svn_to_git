package com.bisam.svntogit;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Main {
  private static final String ERROR_LOG = "error.log";

  public static void main(String[] args) throws Exception {
    ArgumentsParser.Options options = ArgumentsParser.getOptions(args);
    // Create author file if not provided
    if (!options.isAuthorsFileProvided()) {
      boolean authorsFileReady = writeAuthorFile(options);
      if (!authorsFileReady) {
        return;
      }
    }

    // Launch initial svn git clone
    long start = new Date().getTime();
    createGitRepo(options);
    System.out.append("Clone : ").append(String.valueOf((new Date().getTime() - start) / 1000)).append(" s").append(Files.LINE_SEPARATOR);

    start = new Date().getTime();
    // TODO create tags
//    createTags();
    System.out.append("Tags : ").append(String.valueOf((new Date().getTime() - start) / 1000)).append(" s").append(Files.LINE_SEPARATOR);

    start = new Date().getTime();
    // TODO create branches
    System.out.append("Branches : ").append(String.valueOf((new Date().getTime() - start) / 1000)).append(" s").append(Files.LINE_SEPARATOR);
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

    Process process =
      Executors.executeCommand(
        "git svn clone --prefix=svn/ --no-metadata --authors-file=" + options.getAuthorsFilePath() + " " + options.getSvnRepo() +
        " " + gitRepo + " --trunk=trunk --tags=tags --branches=branches");
    Executors.executeAll(process, InputStreamToOutputs.init(System.out),
                         InputStreamToOutputs.init(new File(Files.getLocalFilePath(Main.class, ERROR_LOG))));
  }

  private static void createTags() throws IOException, ExecutionException, InterruptedException {
  }

  private static void deleteGitRepo(String gitRepo) throws IOException {
    File gitRepoDirectory = new File(gitRepo);
    if (gitRepoDirectory.exists()) {
      FileUtils.deleteDirectory(gitRepoDirectory);
    }
  }
}
