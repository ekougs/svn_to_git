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
    String gitRepo = options.getGitRepo();
    deleteGitRepo(gitRepo);

    Runtime rt = Runtime.getRuntime();
    Process process =
      rt.exec("svn-all-fast-export --identify-map " + options.getAuthorsFilePath() + " --stats " + options.getSvnRepo() +
              " " + gitRepo + " --rules rules");
//      rt.exec("git svn clone --prefix=svn/ --no-metadata --authors-file=" + options.getAuthorsFilePath() + " " + options.getSvnRepo() +
//              " " + gitRepo + " --trunk=trunk --tags=tags --branches=branches");

    Runnable shutdownPoolRunnable =
      ExecutorUtils.executeAll(InputStreamToOutputsRunnable.init(process.getInputStream(), System.out),
                               InputStreamToOutputsRunnable.init(process.getErrorStream(), new File(Files.getLocalFilePath(Main.class, ERROR_LOG))));

    process.waitFor();
    shutdownPoolRunnable.run();

    System.out.append("Clone : ").append(String.valueOf((new Date().getTime() - start) / 1000)).append(" s");
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

  private static void deleteGitRepo(String gitRepo) throws IOException {
    File gitRepoDirectory = new File(gitRepo);
    if (gitRepoDirectory.exists()) {
      FileUtils.deleteDirectory(gitRepoDirectory);
    }
  }
}
