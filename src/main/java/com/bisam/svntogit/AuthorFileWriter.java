package com.bisam.svntogit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthorFileWriter {
  private final String authorFilePath;
  private final String authorMail;

  private AuthorFileWriter(String authorFilePath, String authorMail) {
    this.authorFilePath = authorFilePath;
    this.authorMail = authorMail;
  }

  static AuthorFileWriter init(String authorFilePath, String authorMail) {
    return new AuthorFileWriter(authorFilePath, authorMail);
  }

  void write(AuthorExtractor.Authors authors) throws IOException {
    try (PrintWriter authorsPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(authorFilePath)))) {
      for (String author : authors) {
        authorsPrintWriter.println(getAuthorLine(author, authorMail));
      }
    }
  }

  private static String getAuthorLine(String author, String authorMail) {
    return Strings.append(author, " = ", author, " <", authorMail, ">");
  }
}
