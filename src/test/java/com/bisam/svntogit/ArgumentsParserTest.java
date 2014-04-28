package com.bisam.svntogit;

import com.bisam.svntogit.utils.Files;
import com.bisam.svntogit.utils.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.*;

public class ArgumentsParserTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testMandatoryNotSet() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("--repo is mandatory.");
    ArgumentsParser.getOptions(new String[0]);
  }

  @Test
  public void testOnlyMandatoryParameter() throws Exception {
    ArgumentsParser.Options options = ArgumentsParser.getOptions(new String[]{"--repo", "nimportequoi"});
    assertEquals("nimportequoi", options.getSvnRepo());
    assertEquals(Strings.EMPTY, options.getMail());
    assertEquals(Strings.EMPTY, options.getGitRepo());
    assertEquals(Files.getLocalFilePath(ArgumentsParserTest.class, "authors"), options.getAuthorsFilePath());
    assertFalse(options.isAuthorsFileProvided());
  }

  @Test
  public void testWrongParameter() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Value and option must be separated with a space character. This option is not allowed : --nimportequoi.");
    ArgumentsParser.getOptions(new String[]{"--nimportequoi", "nimportequoi"});
  }

  @Test
  public void testAllParameters() throws Exception {
    ArgumentsParser.Options options =
      ArgumentsParser.getOptions(new String[]{"--author-file-provided", "--author-mail", "john@doe.com", "--repo", "repoquoi",
                                              "--file", "authorfilequoi",
                                              "--git-repo", "gitquoi", "--repair-branches"});
    assertEquals("repoquoi", options.getSvnRepo());
    assertEquals("gitquoi", options.getGitRepo());
    assertEquals("john@doe.com", options.getMail());
    assertEquals("authorfilequoi", options.getAuthorsFilePath());
    assertTrue(options.isAuthorsFileProvided());
    assertTrue(options.repairBranches());
  }
}
