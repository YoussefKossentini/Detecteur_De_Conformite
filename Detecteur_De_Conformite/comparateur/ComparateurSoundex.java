package comparateur;

import java.util.List;

public class ComparateurSoundex implements Comparateur {

  private String soundex(String s) {
    if (s == null || s.isEmpty())
      return "";

    s = s.toUpperCase();
    String map = "01230120022455012623010202";

    int start = 0;
    while (start < s.length() && (s.charAt(start) < 'A' || s.charAt(start) > 'Z'))
      start++;

    if (start == s.length())
      return "";

    StringBuilder code = new StringBuilder();
    code.append(s.charAt(start));
    char prev = map.charAt(s.charAt(start) - 'A');

    for (int i = start + 1; i < s.length() && code.length() < 4; i++) {
      char c = s.charAt(i);
      if (c < 'A' || c > 'Z')
        continue;
      char digit = map.charAt(c - 'A');
      if (digit != '0' && digit != prev)
        code.append(digit);
      prev = digit;
    }

    while (code.length() < 4)
      code.append('0');

    return code.toString();
  }

  public double comparer(List<String> s1, List<String> s2) {
    if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty())
      return 0.0;

    int correspondances = 0;
    for (String t1 : s1) {
      boolean trouve = false;
      for (String t2 : s2) {
        if (soundex(t1).equals(soundex(t2))) {
          trouve = true;
          break;
        }
      }
      if (trouve)
        correspondances++;
    }

    return (double) correspondances / s1.size();
  }
}