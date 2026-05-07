package Detecteur_De_Conformite.Comparateur;

import java.util.ArrayList;

public class ComparateurJaroWinkler implements Comparateur {

    private double jaroWinkler(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;

        int fenetre = Math.max(s1.length(), s2.length()) / 2 - 1;
        if (fenetre < 0) fenetre = 0;

        boolean[] matchS1 = new boolean[s1.length()];
        boolean[] matchS2 = new boolean[s2.length()];
        int matches = 0;

        for (int i = 0; i < s1.length(); i++) {
            int debut = Math.max(0, i - fenetre);
            int fin   = Math.min(i + fenetre + 1, s2.length());
            for (int j = debut; j < fin; j++) {
                if (!matchS2[j] && s1.charAt(i) == s2.charAt(j)) {
                    matchS1[i] = matchS2[j] = true;
                    matches++;
                    break;
                }
            }
        }

        if (matches == 0) return 0.0;

        int t = 0, k = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (!matchS1[i]) continue;
            while (!matchS2[k]) k++;
            if (s1.charAt(i) != s2.charAt(k)) t++;
            k++;
        }

        double jaro = (matches / (double) s1.length()
                + matches / (double) s2.length()
                + (matches - t / 2.0) / matches) / 3.0;

        int prefixe = 0;
        int maxLen = Math.min(s1.length(), s2.length());
        for (int i = 0; i < Math.min(4, maxLen); i++) {
            if (s1.charAt(i) == s2.charAt(i)) prefixe++;
            else break;
        }

        return jaro + prefixe * 0.1 * (1 - jaro);
    }

    @Override
    public double comparer(ArrayList<String> s1, ArrayList<String> s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty())
            return 0.0;

        double total = 0.0;
        for (String t1 : s1) {
            double meilleur = 0.0;
            for (String t2 : s2) {
                double score = jaroWinkler(t1, t2);
                if (score > meilleur) meilleur = score;
            }
            total += meilleur;
        }
        return total / s1.size();
    }
}
