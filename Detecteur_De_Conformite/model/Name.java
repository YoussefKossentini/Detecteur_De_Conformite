package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Name {
  private final String id;
  private final String[] nomBrute;

  private final int numTokens;
  private final int totalLength;
  private final List<String> tokens;

  public Name(String id, String[] nomBrute) {
    this.id = id;
    this.nomBrute = nomBrute;

    int count = 0;
    int len = 0;
    for (String s : nomBrute) {
      String t = (s == null) ? "" : s.trim();
      if (!t.isEmpty()) {
        count++;
        len += t.length();
      }
    }
    this.numTokens = count;
    this.totalLength = len;
    this.tokens = Collections.unmodifiableList(Arrays.asList(nomBrute));
  }

  public String[] getNomBrute() {
    return this.nomBrute;
  }

  public String getId() {
    return this.id;
  }

  public int getNumTokens() {
    return this.numTokens;
  }

  public int getTotalLength() {
    return this.totalLength;
  }

  public List<String> getTokens() {
    return this.tokens;
  }
}
