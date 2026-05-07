package Detecteur_De_Conformite.Generateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Detecteur_De_Conformite.Name;

public class GenerateurIndexDouble implements GenerateurCandidat {
    private final int nbPermisTokens;
    private final int longPermis;

    public GenerateurIndexDouble(int nbPermisTokens, int longPermis) {
        this.nbPermisTokens = Math.max(0, nbPermisTokens);
        this.longPermis = Math.max(0, longPermis);
    }

    @Override
    public ArrayList<int[]> genererIndices(ArrayList<Name> listeA, ArrayList<Name> listeB) {
        ArrayList<int[]> couples = new ArrayList<>();
        if (listeA == null || listeB == null) return couples;

        Map<Integer, Map<Integer, List<Integer>>> indexB = new HashMap<>();
        for (int j = 0; j < listeB.size(); j++) {
            Name b = listeB.get(j);
            int nbTokensB = compterTokens(b);
            int longueurB = sommeLongueurs(b);

            Map<Integer, List<Integer>> mapLong = indexB.get(nbTokensB);
            if (mapLong == null) {
                mapLong = new HashMap<>();
                indexB.put(nbTokensB, mapLong);
            }
            List<Integer> js = mapLong.get(longueurB);
            if (js == null) {
                js = new ArrayList<>();
                mapLong.put(longueurB, js);
            }
            js.add(j);
        }

        for (int i = 0; i < listeA.size(); i++) {
            Name a = listeA.get(i);
            int nbTokensA = compterTokens(a);
            int longueurA = sommeLongueurs(a);

            for (int dt = -nbPermisTokens; dt <= nbPermisTokens; dt++) {
                int nbT = nbTokensA + dt;
                Map<Integer, List<Integer>> mapLong = indexB.get(nbT);
                if (mapLong == null) continue;
                for (int dl = -longPermis; dl <= longPermis; dl++) {
                    int len = longueurA + dl;
                    List<Integer> js = mapLong.get(len);
                    if (js == null) continue;
                    for (int j : js) {
                        couples.add(new int[]{i, j});
                    }
                }
            }
        }
        return couples;
    }

    private int compterTokens(Name n) {
        if (n == null || n.getNomBrute() == null) return 0;
        int nb = 0;
        for (String s : n.getNomBrute()) {
            if (s != null && !s.trim().isEmpty()) nb++;
        }
        return nb;
    }

    private int sommeLongueurs(Name n) {
        if (n == null || n.getNomBrute() == null) return 0;
        int len = 0;
        for (String s : n.getNomBrute()) {
            if (s == null) continue;
            String t = s.trim();
            if (t.isEmpty()) continue;
            len += t.length();
        }
        return len;
    }
}
