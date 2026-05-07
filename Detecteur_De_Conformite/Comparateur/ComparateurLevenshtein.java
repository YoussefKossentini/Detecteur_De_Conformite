package Detecteur_De_Conformite.Comparateur;

import java.util.ArrayList;

public class ComparateurLevenshtein implements Comparateur {

    private int distance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    @Override
    public double comparer(ArrayList<String> s1, ArrayList<String> s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty())
            return 0.0;

        double total = 0.0;

        for (String token1 : s1) {
            double meilleurScore = 0.0;

            for (String token2 : s2) {
                int longueurMax = Math.max(token1.length(), token2.length());
                if (longueurMax == 0) continue;

                // Plus la distance est petite, plus les chaînes sont proches.
                // On divise par la longueur max pour normaliser la distance entre 0 et 1.
                // On soustrait de 1 pour obtenir une similarité (1 = identique, 0 = très différent).
                double score = 1.0 - (double) distance(token1, token2) / longueurMax;

                if (score > meilleurScore) {
                    meilleurScore = score;
                }
            }
            total += meilleurScore;
        }
        return total / s1.size();
    }

    public static void main(String[] args) {
        ComparateurLevenshtein comparateur = new ComparateurLevenshtein();

        ArrayList<String> nom1 = new ArrayList<>();
        nom1.add("Jean");
        nom1.add("Dupont");

        ArrayList<String> nom2 = new ArrayList<>();
        nom2.add("Jean");
        nom2.add("Dupoint");

        System.out.println("Test 1 (similaires) : " + comparateur.comparer(nom1, nom2));

        ArrayList<String> nom3 = new ArrayList<>();
        nom3.add("Pierre");
        nom3.add("Martin");

        ArrayList<String> nom4 = new ArrayList<>();
        nom4.add("Jean");
        nom4.add("Dupont");

        System.out.println("Test 2 (différents) : " + comparateur.comparer(nom3, nom4));

        ArrayList<String> nom5 = new ArrayList<>();
        nom5.add("Jean");

        ArrayList<String> nom6 = new ArrayList<>();
        nom6.add("Jean");
        nom6.add("Dupont");

        System.out.println("Test 3 (tailles différentes) : " + comparateur.comparer(nom5, nom6));
    }
}
