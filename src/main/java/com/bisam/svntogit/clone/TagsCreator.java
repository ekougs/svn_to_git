package com.bisam.svntogit.clone;

import com.bisam.svntogit.utils.*;

import java.io.File;
import java.io.IOException;

public class TagsCreator implements InputStreamReaderRunnable.InputStreamLineHandler {
  public static final String PREFIX = "refs/remotes/svn/tags/";
  private static final String ERROR_LOG = "error_tags.log";
  private final File gitRepo;

  public TagsCreator(String gitRepo) {
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
      Logs.appendln(tag, " ", committerName, " <", committerEmail, "> ", commitDate);

      String tagComment =
        Strings.append("Tag: ", tag, " sha1: ", sha1, " using '", committerName, "' <", committerEmail, "> on ", commitDate);
      Logs.appendln(tagComment);
      ProcessBuilder gitCreationCommandBuilder =
        new ProcessBuilder("git", "tag", "-a", tag, "-m", tagComment, sha1).directory(gitRepo);
      Executors.processAll(gitCreationCommandBuilder.start(), NULL, InputStreamToOutputs.getErrorStreamHandler(ERROR_LOG));

      String updateRefTagCommand = Strings.append("git update-ref -d ", originalTagName);
      Logs.appendln(updateRefTagCommand);
      execute(updateRefTagCommand);
      Logs.appendln();
    }
    catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
  }

  private String getResult(String command)
    throws InterruptedException, IOException {
    InputStreamResultProvider resultProvider = new InputStreamResultProvider();
    execute(command, resultProvider);
    return resultProvider.getResult();
  }

  private void execute(String command) throws InterruptedException, IOException {
    Executors.executeAll(command, NULL, ERROR_LOG, gitRepo);
  }

  private void execute(String command, InputStreamResultProvider resultProvider) throws InterruptedException, IOException {
    Executors.executeAll(command, resultProvider, ERROR_LOG, gitRepo);
  }

}
