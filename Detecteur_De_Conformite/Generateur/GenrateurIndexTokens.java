import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenrateurIndexTokens implements GenerateurCandidat {
    private final int nbPermisTokens;

    public GenrateurIndexTokens(int nbPermisTokens) {
        this.nbPermisTokens = Math.max(0, nbPermisTokens);
    }

    public ArrayList<int[]> genererIndices(ArrayList<Name> listeA, ArrayList<Name> listeB) {
        Map<Integer, List<Integer>> indexB = new HashMap<>();
        for (int j = 0; j < listeB.size(); j++) {
            int nb = compterTokens(listeB.get(j));
            ArrayList<Integer> liste = indexB.get(nb);
            if (liste == null) {
                liste = new ArrayList<>();
                indexB.put(nb, liste);
            }
            liste.add(j);
        }
        ArrayList<int[]> couples = new ArrayList<>();
        for (int i = 0; i < listeA.size(); i++) {
            int nbA = compterTokens(listeA.get(i));
            for (int d = -nbPermisTokens; d <= nbPermisTokens; d++) {
                List<Integer> js = indexB.get(nbA + d);
                if (js == null) continue;
                for (int j : js) {
                    couples.add(new int[]{i, j});
                }
            }
        }
        return couples;
    }

    private int compterTokens(Name n) {
        if (n == null || n.getNomBrute == null) return 0;
        int c = 0;
        for (String s : n.getNomBrute()) {
            if (s != null && !s.trim().isEmpty()) c++;
        }
        return c;
    }
}

