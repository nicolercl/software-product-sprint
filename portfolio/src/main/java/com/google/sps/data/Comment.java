package com.google.sps.data;

/** A comment in the portfolio page. */
public final class Comment {

  private final long id;
  private final String user;
  private final long timestamp;
  private final String content;

  public Comment(long id, String user, long timestamp, String content) {
    this.id = id;
    this.user = user;
    this.timestamp = timestamp;
    this.content = content;
  }
}