package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

class TagsCreator implements InputStreamReaderRunnable.InputStreamLineHandler {
  public static final String PREFIX = "refs/remotes/svn/tags/";
  private static final String ERROR_LOG = "error_tags.log";
  private final File gitRepo;

  TagsCreator(String gitRepo) {
    this.gitRepo = new File(gitRepo);
  }

  @Override
  public void handleLine(String originalTagName) {
    String tag = originalTagName.substring(PREFIX.length());
    try {
      String committerName = getResult("git show -s --pretty=format:%an " + originalTagName);
      String committerEmail = getResult("git show -s --pretty=format:%ae " + originalTagName);
      String commitDate = getResult("git show -s --pretty=format:%ad " + originalTagName);
      String sha1 = getResult("git rev-parse " + originalTagName);
      System.out.append(Strings.append(tag, " ", committerName, " <", committerEmail, "> ", commitDate))
        .append(Files.LINE_SEPARATOR);

      String tagComment =
        Strings.append("Tag: ", tag, " sha1: ", sha1, " using '", committerName, "' <", committerEmail, "> on ", commitDate);
      System.out.append(tagComment).append(Files.LINE_SEPARATOR);
      ProcessBuilder gitCreationCommandBuilder =
        new ProcessBuilder("git", "tag", "-a", tag, "-m", tagComment, sha1).directory(gitRepo);
      Executors.processAll(gitCreationCommandBuilder.start(), NULL, InputStreamToOutputs.getErrorStreamHandler(ERROR_LOG));

      String updateRefTagCommand = Strings.append("git update-ref -d ", originalTagName);
      System.out.append(updateRefTagCommand).append(Files.LINE_SEPARATOR);
      getResult(updateRefTagCommand);
      System.out.append(Files.LINE_SEPARATOR);
    }
    catch (IOException | InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
  }

  private String getResult(String command)
    throws ExecutionException, InterruptedException, IOException {
    InputStreamResultProvider resultProvider = new InputStreamResultProvider();
    Executors.executeAll(command, resultProvider, ERROR_LOG, gitRepo);
    return resultProvider.result;
  }

  private static class InputStreamResultProvider implements InputStreamReaderRunnable.InputStreamLineHandler {
    private String result;

    @Override
    public void handleLine(String result) {
      this.result = result;
    }

    @Override
    public void close() {
    }
  }
}
