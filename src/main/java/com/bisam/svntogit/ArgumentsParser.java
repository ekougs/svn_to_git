package com.bisam.svntogit;

import com.bisam.svntogit.utils.Files;
import com.bisam.svntogit.utils.Strings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentsParser {

  public static Options getOptions(String[] args, Parameter... mandatoryParameters) {
    Options options = new Options(mandatoryParameters);
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

  public static class Options {
    private final Map<String, String> optionValues = new HashMap<>();
    private List<Parameter> mandatoryParameters;

    private Options(Parameter[] mandatoryParameters) {
      this.mandatoryParameters = Arrays.asList(mandatoryParameters);
    }

    String getSvnRepo() {
      return optionValues.get(Parameter.SVN_REPO_OPTION.name);
    }

    String getAuthorsFilePath() throws IOException {
      String fileOptionName = Parameter.FILE_OPTION.name;
      return isEmptyValue(fileOptionName) ? getDefaultFilePath() : optionValues.get(fileOptionName);
    }

    String getGitRepo() {
      return getOptionalField(Parameter.GIT_REPO_OPTION.name);
    }

    String getAllowedBranchesPath() {
      return getOptionalField(Parameter.ALLOWED_BRANCHES.name);
    }

    boolean isAuthorsFileProvided() {
      return contains(Parameter.AUTHOR_FILE_PROVIDED);
    }

    boolean repairBranches() {
      return contains(Parameter.REPAIR_BRANCHES);
    }

    String getMail() {
      return getOptionalField(Parameter.AUTHOR_MAIL.name);
    }

    void set(Parameter parameter, String value) {
      optionValues.put(parameter.name, value);
    }

    void check() {
      for (Parameter parameter : Parameter.values()) {
        String parameterName = parameter.name;
        if (mandatoryParameters.contains(parameter) && isEmptyValue(parameterName)) {
          throw new IllegalArgumentException(parameterName + " is mandatory.");
        }
      }
    }

    private boolean isEmptyValue(String parameterName) {
      return !optionValues.containsKey(parameterName) ||
             optionValues.get(parameterName) == null ||
             Strings.isEmptyString(optionValues.get(parameterName));
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

    private String getOptionalField(String parameterName) {
      return isEmptyValue(parameterName) ? Strings.EMPTY : optionValues.get(parameterName);
    }

    private boolean contains(Parameter parameter) {
      return optionValues.containsKey(parameter.name);
    }
  }

  static enum Parameter {
    FILE_OPTION("--file"),
    SVN_REPO_OPTION("--repo"),
    GIT_REPO_OPTION("--git-repo"),
    AUTHOR_FILE_PROVIDED("--author-file-provided", Boolean.TRUE.toString()),
    AUTHOR_MAIL("--author-mail"),
    REPAIR_BRANCHES("--repair-branches", Boolean.TRUE.toString()),
    ALLOWED_BRANCHES("--allowed-branches");

    private final String name;
    private final int step;
    private final String value;

    private Parameter(String name) {
      this(name, 2, Strings.EMPTY);
    }

    private Parameter(String name, String value) {
      this(name, 1, value);
    }

    private Parameter(String name, int step, String value) {
      this.name = name;
      this.step = step;
      this.value = value;
    }

    public String getName() {
      return name;
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
