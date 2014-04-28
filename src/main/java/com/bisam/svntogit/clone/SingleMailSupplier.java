package com.bisam.svntogit.clone;

public class SingleMailSupplier implements MailSupplier {
  private final String uniqueMail;

  public SingleMailSupplier(String uniqueMail) {
    this.uniqueMail = uniqueMail;
  }

  @Override
  public String getMail(String author) {
    return uniqueMail;
  }
}
