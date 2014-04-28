package com.bisam.svntogit.utils;

import com.bisam.svntogit.utils.Strings;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class StringsTest {
  @Test
  public void testApppendingVoid() throws Exception {
    assertEquals(Strings.EMPTY, Strings.append());
  }

  @Test
  public void testApppendingNull() throws Exception {
    assertEquals(Strings.EMPTY, Strings.append(null));
  }

  @Test
  public void testApppendingValidArguments() throws Exception {
    assertEquals("Pascal Sennen", Strings.append("Pascal ", "Sennen"));
  }

  @Test
  public void testApppendingValidArgumentsNull() throws Exception {
    assertEquals("Pascal nullSennen", Strings.append("Pascal ", null, "Sennen"));
  }
}
