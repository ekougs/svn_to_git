package com.bisam.svntogit.clone;

import com.bisam.svntogit.utils.BufferedReaderIterable;
import com.bisam.svntogit.utils.Executors;
import com.bisam.svntogit.utils.Gits;
import com.bisam.svntogit.utils.Strings;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class NonAllowedBranchEraser {
    private static final String ERROR_LOG = "error_branch_eraser.log";
    private final File gitRepoFile;
    private final String allowedBranchesPath;

    private NonAllowedBranchEraser(File gitRepoFile, String allowedBranchesPath) {
        this.gitRepoFile = gitRepoFile;
        this.allowedBranchesPath = allowedBranchesPath;
    }

    public static NonAllowedBranchEraser init(String gitRepo, String allowedBranchesPath) {
        return new NonAllowedBranchEraser(new File(gitRepo), allowedBranchesPath);
    }

    public void remove() throws IOException, InterruptedException {
        final Set<String> allowedBranches = new HashSet<>();
        try(FileReader allowedBranchesFileReader = new FileReader(allowedBranchesPath);
            BufferedReader allowedBranchesBufferedReader = new BufferedReader(allowedBranchesFileReader)) {
            for (String branch : new BufferedReaderIterable(allowedBranchesBufferedReader)) {
                allowedBranches.add(branch.trim());
            }
            NonAllowedBranchesRetriever retriever = new NonAllowedBranchesRetriever(allowedBranches);
            Gits.iterateOnBranches(gitRepoFile, retriever, ERROR_LOG);
            StringBuilder branchDeletionCommandBuilder = new StringBuilder(Strings.append(Gits.GIT_BRANCH_DELETION));
            for (String branchToDelete : retriever.branchesToDelete) {
                branchDeletionCommandBuilder.append(branchToDelete).append(" ");
            }
            Executors.executeCommandAndWait(branchDeletionCommandBuilder.toString(), gitRepoFile);
        }
    }

    private static class NonAllowedBranchesRetriever implements Gits.BranchConsumer {
        private final Set<String> branchesToDelete;
        private final Set<String> allowedBranches;

        public NonAllowedBranchesRetriever(Set<String> allowedBranches) {
            this.allowedBranches = allowedBranches;
            branchesToDelete = new HashSet<>();
        }

        @Override
        public void consume(String branchName) {
            if (!allowedBranches.contains(branchName)) {
                branchesToDelete.add(branchName);
            }
        }
    }
}
