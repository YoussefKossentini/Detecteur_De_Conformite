package comparateur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparateurJaroWinkler implements Comparateur {

  private double jaroWinkler(String s1, String s2) {
    if (s1.equals(s2))
      return 1.0;

    int fenetre = Math.max(s1.length(), s2.length()) / 2 - 1;
    if (fenetre < 0)
      fenetre = 0;

    boolean[] m1 = new boolean[s1.length()];
    boolean[] m2 = new boolean[s2.length()];
    int matches = 0;

    for (int i = 0; i < s1.length(); i++) {
      int debut = Math.max(0, i - fenetre);
      int fin = Math.min(i + fenetre + 1, s2.length());
      for (int j = debut; j < fin; j++) {
        if (!m2[j] && s1.charAt(i) == s2.charAt(j)) {
          m1[i] = true;
          m2[j] = true;
          matches++;
          break;
        }
      }
    }

    if (matches == 0)
      return 0.0;

    int transpositions = 0;
    int k = 0;
    for (int i = 0; i < s1.length(); i++) {
      if (!m1[i])
        continue;
      while (!m2[k])
        k++;
      if (s1.charAt(i) != s2.charAt(k))
        transpositions++;
      k++;
    }

    double jaro = (matches / (double) s1.length() + matches / (double) s2.length()
        + (matches - transpositions / 2.0) / matches) / 3.0;

    int prefixe = 0;
    int minLen = Math.min(4, Math.min(s1.length(), s2.length()));
    for (int i = 0; i < minLen; i++) {
      if (s1.charAt(i) == s2.charAt(i))
        prefixe++;
      else
        break;
    }

    return jaro + prefixe * 0.1 * (1 - jaro);
  }

  public double comparer(List<String> s1, List<String> s2) {
    double scoreNormal = calculerScore(s1, s2);
    List<String> s1Inv = new ArrayList<>(s1);
    Collections.reverse(s1Inv);
    double scoreInverse = calculerScore(s1Inv, s2);

    if (scoreNormal >= scoreInverse)
      return scoreNormal;
    return scoreInverse;
  }

  private double calculerScore(List<String> s1, List<String> s2) {
    if (s1.isEmpty() || s2.isEmpty())
      return 0.0;

    double total = 0.0;
    for (String t1 : s1) {
      double meilleur = 0.0;
      for (String t2 : s2) {
        double score = jaroWinkler(t1, t2);
        if (score > meilleur)
          meilleur = score;
      }
      total += meilleur;
    }
    return total / s1.size();
  }
}