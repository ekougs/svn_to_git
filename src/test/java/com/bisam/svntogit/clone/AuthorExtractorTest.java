package com.bisam.svntogit.clone;

import org.junit.Test;

import java.util.Iterator;

import static junit.framework.TestCase.*;

public class AuthorExtractorTest {

  @Test
  public void testSampleFile() throws Exception {
    AuthorExtractor.Authors authors = AuthorExtractor.init(AuthorExtractorTest.class.getResource("log.txt").getPath()).getAuthors();
    assertThat(authors)
      .nextAuthorEquals("pascal")
      .nextAuthorEquals("sennen")
      .noMoreAuthor();
  }

  private AuthorAssertion assertThat(AuthorExtractor.Authors authors) {
    return new AuthorAssertion(authors);
  }

  private static class AuthorAssertion {
    private final Iterator<String> authors;

    private AuthorAssertion(AuthorExtractor.Authors authors) {
      this.authors = authors.iterator();
    }

    private AuthorAssertion nextAuthorEquals(String expectedAuthor) {
      assertTrue(authors.hasNext());
      assertEquals(expectedAuthor, authors.next());
      return this;
    }

    public void noMoreAuthor() {
      assertFalse(authors.hasNext());
    }
  }
}
