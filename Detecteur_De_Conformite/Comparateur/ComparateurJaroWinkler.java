import java.util.ArrayList;
public class ComparateurJaroWinkler implements Comparateur {
    private double jaroWinkler(String s1, String s2) {
        if (s1.equals(s2))
            return 1.0; //type de retour double 
        
        int matchWindow = Math.max(s1.length(), s2.length()) / 2 - 1;
        boolean[] matchedS1 = new boolean[s1.length()];
        boolean[] matchedS2 = new boolean[s2.length()];
        int matches = 0;
        for (int i = 0; i < s1.length(); i++) {
            int start = Math.max(0, i - matchWindow);
            int end = Math.min(i + matchWindow + 1, s2.length());
            for (int j = start; j < end; j++) {
                if (!matchedS2[j] && s1.charAt(i) == s2.charAt(j)) {
                    matchedS1[i] = true;
                    matchedS2[j] = true;
                    matches++;
                    break;
                }
            }
        }
        if (matches == 0)
            return 0.0;
        // transpositions
        int t = 0;
        int k = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (!matchedS1[i])
                continue;
            while (!matchedS2[k])
                k++;
            if (s1.charAt(i) != s2.charAt(k))
                t++;
            k++;
        }
        double jaro = (matches / (double) s1.length()
                + matches / (double) s2.length()
                + (matches - t / 2.0) / matches) / 3.0;
        // préfixe commun (max 4)
        int prefix = 0;
        for (int i = 0; i < Math.min(4, Math.min(s1.length(), s2.length())); i++) {
            if (s1.charAt(i) == s2.charAt(i))
                prefix++;
            else
                break;
        }
        return jaro + prefix * 0.1 * (1 - jaro);
    }

    /*donc la logique ici est de Pour chaque token de s1, on cherche son meilleur 'jumeau' dans s2 via JaroWinkler. 
    On fait la moyenne de ces meilleurs scores, et si elle dépasse 0.85 alors acceptée*/
    public boolean comparer(ArrayList<String> s1, ArrayList<String> s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) //vérification que kes listes comparées ne sont pas ni null (ils existent dans la mémoire) ni empty (vide) 
            return false;
        double total = 0.0;
        for (String t1 : s1) {
            double meilleur = 0.0;
            for (String t2 : s2) {
                double score = jaroWinkler(t1, t2);
                if (score > meilleur) meilleur = score;
            }
            total += meilleur;
        }
        return (total / s1.size()) >= 0.85;
    }
}
