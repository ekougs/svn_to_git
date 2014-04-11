package com.bisam.svntogit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthorFileWriter {
  private final String authorFilePath;
  private final MailSupplier mailSupplier;

  private AuthorFileWriter(String authorFilePath, MailSupplier mailSupplier) {
    this.authorFilePath = authorFilePath;
    this.mailSupplier = mailSupplier;
  }

  static AuthorFileWriter init(String authorFilePath, MailSupplier mailSupplier) {
    return new AuthorFileWriter(authorFilePath, mailSupplier);
  }

  void write(AuthorExtractor.Authors authors) throws IOException {
    try (PrintWriter authorsPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(authorFilePath)))) {
      for (String author : authors) {
        authorsPrintWriter.println(getAuthorLine(author, mailSupplier.getMail(author)));
      }
    }
  }

  private static String getAuthorLine(String author, String authorMail) {
    return Strings.append(author, " = ", author, " <", authorMail, ">");
  }
}
