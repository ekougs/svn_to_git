package com.bisam.svntogit.utils;

import java.io.File;
import java.io.IOException;

public class Gits {
    private static final String FORMAT_CHARACTER = "'";
    public static final String GIT_LOG_SHA1_COMMIT_DATE_BRANCH_COMMAND =
            Strings.append("git log --pretty=format:", FORMAT_CHARACTER, "%H;%cd", FORMAT_CHARACTER, " --date=iso ");
    public static final String MASTER = "master";
    public static final String NO_BRANCH = "(no branch)";
    public static final String GIT_BRANCH_LIST_COMMAND = "git branch";
    public static final String GIT_REV_LIST_WITHOUT_PARENT_COMMAND = "git rev-list --max-parents=0 ";
    public static final String GIT_CHECKOUT_WITH_SHA1 = "git checkout -b ";
    public static final String GIT_REBASE = "git rebase ";
    public static final String GIT_BRANCH_DELETION = "git branch -D ";

    public static void iterateOnBranches(File gitRepository,
                                  InputStreamReaderRunnable.InputStreamLineHandler inputStreamHandler,
                                  String errorFilePath)
            throws InterruptedException, IOException {
        Executors.executeAll(GIT_BRANCH_LIST_COMMAND, inputStreamHandler,
                             errorFilePath, gitRepository);
    }
}
