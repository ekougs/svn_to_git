package com.bisam.svntogit.branchrepair;

import com.bisam.svntogit.utils.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class BranchesSHA1s {
  private final HashMap<String, SHA1s> branchesRevisions = new HashMap<>();

  static BranchesSHA1s get(File gitRepository) throws IOException, InterruptedException {
    BranchesSHA1s branchesSHA1s = new BranchesSHA1s();
    branchesSHA1s.load(gitRepository);
    return branchesSHA1s;
  }

  private BranchesSHA1s() {
  }

  private void load(File gitRepository) throws IOException, InterruptedException {
    BranchSHA1sLoader branchSHA1sLoader = new BranchSHA1sLoader(gitRepository);
    Gits.iterateOnBranches(gitRepository, branchSHA1sLoader,
                           BranchesRepairer.BRANCHES_REPAIRER_ERROR);
    branchesRevisions.putAll(branchSHA1sLoader.branchesRevisions);
  }

  SHA1s sha1s(String branchName) {
    return branchesRevisions.get(branchName);
  }

  static class SHA1s implements Iterable<SHA1> {
    private final HashMap<String, Date> sha1s = new HashMap<>();
    private final TreeMap<Date, String> sha1sByDate = new TreeMap<>();

    private SHA1s() {
    }

    private void add(String sha1, Date date) {
      sha1s.put(sha1, date);
      sha1sByDate.put(date, sha1);
    }

    boolean contains(String sha1) {
      return sha1s.containsKey(sha1);
    }

    @Override
    public Iterator<SHA1> iterator() {
      return sha1sCopySet().iterator();
    }

    public SHA1 sha1(String sha1) {
      Date sha1Date = sha1s.get(sha1);
      return new SHA1(sha1, sha1Date);
    }

    public SHA1 previous(SHA1 sha1) {
      Date previousSHA1Date = sha1sByDate.floorKey(sha1.date());
      return new SHA1(sha1sByDate.get(previousSHA1Date), previousSHA1Date);
    }

    private TreeSet<SHA1> sha1sCopySet() {
      TreeSet<SHA1> sha1s = new TreeSet<>();
      for (Map.Entry<String, Date> sha1Entry : this.sha1s.entrySet()) {
        sha1s.add(new SHA1(sha1Entry.getKey(), sha1Entry.getValue()));
      }
      return sha1s;
    }
  }

  static class SHA1 implements Comparable<SHA1> {
    private final String sha1;
    private final Date date;

    private SHA1(String sha1, Date date) {
      this.sha1 = sha1;
      this.date = date;
    }

    public String sha1() {
      return sha1;
    }

    Date date() {
      return date;
    }

    @Override
    public int compareTo(SHA1 otherSHA1) {
      return date.compareTo(otherSHA1.date);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      SHA1 sha11 = (SHA1)o;

      return sha1.equals(sha11.sha1);
    }

    @Override
    public int hashCode() {
      return sha1.hashCode();
    }
  }

  private static class BranchSHA1sLoader implements Gits.BranchConsumer {
    private final HashMap<String, SHA1s> branchesRevisions = new HashMap<>();
    private final File gitRepository;

    public BranchSHA1sLoader(File gitRepository) {
      this.gitRepository = gitRepository;
    }

    @Override
    public void consume(String branchName) {
      try {
        SHA1sLoader sha1sLoader = new SHA1sLoader();
        String branchRevListCommand = Strings.append(Gits.GIT_LOG_SHA1_COMMIT_DATE_BRANCH_COMMAND, branchName);
        Logs.appendln(branchRevListCommand);
        Executors.executeAll(branchRevListCommand,
                             sha1sLoader,
                             BranchesRepairer.BRANCHES_REPAIRER_ERROR,
                             gitRepository);
        branchesRevisions.put(branchName, sha1sLoader.sha1s);

      }
      catch (InterruptedException | IOException e) {
        throw new RuntimeException(e);
      }

    }
  }

  private static class SHA1sLoader implements InputStreamReaderRunnable.InputStreamLineHandler {
    // 2014-04-23 14:54:36 +0000
    private static final DateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");
    private final SHA1s sha1s = new SHA1s();

    @Override
    public void handleLine(String logLine) {
      logLine = logLine.replace("'", Strings.EMPTY);
      String[] logLineElements = logLine.split(";");
      try {
        String sha1 = logLineElements[0].trim();
        Date sha1Date = ISO_8601_FORMAT.parse(logLineElements[1].trim());
        sha1s.add(sha1, sha1Date);
      }
      catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() {
    }
  }
}
