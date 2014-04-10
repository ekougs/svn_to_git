package com.bisam.svntogit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ArgumentsParser {

  static Options getOptions(String[] args) throws IOException {
    Options options = new Options();
    Parameter parameter;
    for (int i = 0; i < args.length; i += parameter.step) {
      String option = args[i];
      parameter = Parameter.get(option);
      if (parameter == null) {
        throw new IllegalArgumentException("Value and option must be separated with a space character. This option is not allowed : " + option + ".");
      }
      options.set(parameter, parameter.getValue(args, i));
    }
    options.check();
    return options;
  }

  static class Options {
    private final Map<String, String> optionValues = new HashMap<>();

    private Options() {
    }

    String getSvnRepo() {
      return optionValues.get(Parameter.SVN_REPO_OPTION.name);
    }

    String getAuthorsFilePath() throws IOException {
      String fileOptionName = Parameter.FILE_OPTION.name;
      return isEmptyValue(fileOptionName) ? getDefaultFilePath() : optionValues.get(fileOptionName);
    }

    String getGitRepo() {
      String gitRepoOptionName = Parameter.GIT_REPO_OPTION.name;
      return isEmptyValue(gitRepoOptionName) ? Strings.EMPTY : optionValues.get(gitRepoOptionName);
    }

    boolean isAuthorsFileProvided() {
      return optionValues.containsKey(Parameter.AUTHOR_FILE_PROVIDED.name);
    }

    String getMail() {
      return optionValues.get(Parameter.AUTHOR_MAIL.name);
    }

    private void set(Parameter parameter, String value) {
      optionValues.put(parameter.name, value);
    }

    private void check() {
      for (Parameter parameter : Parameter.values()) {
        String parameterName = parameter.name;
        if (parameter.mandatory && isEmptyValue(parameterName)) {
          throw new IllegalArgumentException(parameterName + " is mandatory.");
        }
      }
    }

    private boolean isEmptyValue(String parameterName) {
      return !optionValues.containsKey(parameterName) ||
             optionValues.get(parameterName) == null ||
             Strings.EMPTY.equals(optionValues.get(parameterName).trim());
    }

    private static String getDefaultFilePath() throws IOException {
      String defaultDirectory = Files.getLocalFilePath(Main.class, Strings.EMPTY);
      File file = new File(defaultDirectory + "/authors");
      if (!file.exists()) {
        if (!file.createNewFile()) {
          throw new IOException("Could not create authors file");
        }
      }
      return file.getAbsolutePath();
    }
  }

  private static enum Parameter {
    FILE_OPTION("--file"),
    SVN_REPO_OPTION("--repo", true),
    GIT_REPO_OPTION("--git-repo"),
    AUTHOR_FILE_PROVIDED("--author-file-provided", Boolean.TRUE.toString()),
    AUTHOR_MAIL("--author-mail", true);

    private final String name;
    private final int step;
    private final String value;
    private final boolean mandatory;

    private Parameter(String name) {
      this(name, 2, Strings.EMPTY, false);
    }

    private Parameter(String name, boolean mandatory) {
      this(name, 2, Strings.EMPTY, mandatory);
    }

    private Parameter(String name, String value) {
      this(name, 1, value, false);
    }

    private Parameter(String name, int step, String value, boolean mandatory) {
      this.name = name;
      this.step = step;
      this.value = value;
      this.mandatory = mandatory;
    }

    private static Parameter get(String option) {
      for (Parameter parameter : Parameter.values()) {
        if (parameter.name.equals(option)) {
          return parameter;
        }
      }
      return null;
    }

    private String getValue(String[] args, int parameterPosition) {
      if (step == 2) {
        return args[parameterPosition + 1];
      }
      return value;
    }
  }
}
