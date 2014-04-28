package com.bisam.svntogit.clone;

public interface MailSupplier {
  /**
   * @param author SVN author as stored in repo logs
   * @return e-mail associated to author
   */
  String getMail(String author);
}
