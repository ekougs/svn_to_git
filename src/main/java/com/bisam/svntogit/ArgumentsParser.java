package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArgumentsParser {
  private static final String FILE_OPTION = "--file";
  private static final String REPO_OPTION = "--repo";
  private static final String GIT_REPO_OPTION = "--git-repo";
  private static final String AUTHOR_FILE_PROVIDED = "--author-file-provided";
  private static final List<String> OPTIONS =
    Collections.unmodifiableList(Arrays.asList(FILE_OPTION, REPO_OPTION, GIT_REPO_OPTION, AUTHOR_FILE_PROVIDED));

  static Options getOptions(String[] args) throws IOException {
    String svnRepo = null;
    String gitRepo = "";
    String authorsFilePath = null;
    boolean authorsFileProvided = false;
    int step = 2;
    for (int i = 0; i < args.length; i += step) {
      String option = args[i];
      if (!OPTIONS.contains(option)) {
        throw new IllegalArgumentException("Value and option must be separated with a space character. This option is not allowed : " + option + ".");
      }
      if (FILE_OPTION.equals(option)) {
        authorsFilePath = args[i + 1];
        step = 2;
      }
      if (REPO_OPTION.equals(option)) {
        svnRepo = args[i + 1];
        step = 2;
      }
      if (GIT_REPO_OPTION.equals(option)) {
        gitRepo = args[i + 1];
        step = 2;
      }
      if (AUTHOR_FILE_PROVIDED.equals(option)) {
        authorsFileProvided = true;
        step = 1;
      }
    }
    if (svnRepo == null) {
      throw new IllegalArgumentException(REPO_OPTION + " is mandatory.");
    }
    return new Options(svnRepo, authorsFilePath, gitRepo, authorsFileProvided);
  }

  static class Options {
    private final String svnRepo;
    private final String authorsFilePath;
    private final String gitRepo;
    private final boolean authorsFileProvided;

    private Options(String svnRepo, String authorsFilePath, String gitRepo, boolean authorsFileProvided) throws IOException {
      this.svnRepo = svnRepo;
      this.authorsFilePath = authorsFilePath == null ? getDefaultFilePath() : authorsFilePath;
      this.gitRepo = gitRepo;
      this.authorsFileProvided = authorsFileProvided;
    }

    private String getDefaultFilePath() throws IOException {
      String defaultDirectory = Files.getLocalFilePath(Main.class, "");
      File file = new File(defaultDirectory + "/authors");
      if (!file.exists()) {
        if (!file.createNewFile()) {
          throw new IOException("Could not create authors file");
        }
      }
      return file.getAbsolutePath();
    }

    String getSvnRepo() {
      return svnRepo;
    }

    String getAuthorsFilePath() {
      return authorsFilePath;
    }

    String getGitRepo() {
      return gitRepo;
    }

    boolean isAuthorsFileProvided() {
      return authorsFileProvided;
    }
  }
}
