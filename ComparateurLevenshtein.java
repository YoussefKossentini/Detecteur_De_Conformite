package comparateur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComparateurLevenshtein implements Comparateur {
    private double seuilSimilarite = 0.0;

    public ComparateurLevenshtein() {
    }

    public ComparateurLevenshtein(double seuilSimilarite) {
      this.seuilSimilarite = seuilSimilarite;
    }

    private int distance(String s1, String s2, int maxDist) {
        int m = s1.length();
        int n = s2.length();

        if (Math.abs(m - n) > maxDist)
          return maxDist + 1;

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++)
            prev[j] = j;

        for (int i = 1; i <= m; i++) {
          curr[0] = i;
          int minLigne = Integer.MAX_VALUE;
          for (int j = 1; j <= n; j++) {
              if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                curr[j] = prev[j - 1];
              } else {
                int a = prev[j - 1];
                int b = prev[j];
                int c = curr[j - 1];
                int min = a;
                if (b < min) min = b;
                if (c < min) min = c;
                curr[j] = 1 + min;
              }
              if (curr[j] < minLigne)
                  minLigne = curr[j];
          }
          if (minLigne > maxDist)
              return maxDist + 1;

          int[] tmp = prev;
          prev = curr;
          curr = tmp;
        }
        return prev[n];
    }

    public double comparer(List<String> s1, List<String> s2) {
        double scoreNormal = calculerScore(s1, s2);
        List<String> liste1 = new ArrayList<>(s1);
        Collections.reverse(liste1);
        double scoreInverse = calculerScore(liste1, s2);

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
            int maxDistGlobal;
            if (seuilSimilarite > 0) {
              maxDistGlobal = (int) ((1.0 - seuilSimilarite) * t1.length()) + 1;
            } else {
              maxDistGlobal = Integer.MAX_VALUE;
            }

            for (String t2 : s2) {
              int maxLen = Math.max(t1.length(), t2.length());
              if (maxLen == 0)
                  continue;
              if (Math.abs(t1.length() - t2.length()) > maxDistGlobal)
                  continue;

              int maxDist;
              if (seuilSimilarite > 0) {
                  maxDist = (int) ((1.0 - seuilSimilarite) * maxLen);
              } else {
                  maxDist = Integer.MAX_VALUE;
              }

              int dist = distance(t1, t2, maxDist);
              double score = 1.0 - (double) dist / maxLen;
              if (score > meilleur)
                meilleur = score;
            }
            total += meilleur;
        }
        return total / s1.size();
    }
}