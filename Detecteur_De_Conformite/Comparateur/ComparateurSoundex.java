import java.util.ArrayList;

public class ComparateurSoundex implements Comparateur {

    private String soundex(String s) {
        if (s == null || s.isEmpty())
            return "";

        s = s.toUpperCase();
        StringBuilder code = new StringBuilder();
        code.append(s.charAt(0));

        String map = "01230120022455012623010202";  // Tableau de correspondance Soundex
        char prev = map.charAt(s.charAt(0) - 'A');

        for (int i = 1; i < s.length() && code.length() < 4; i++) {
            char c = s.charAt(i);
            if (c < 'A' || c > 'Z')
                continue;
            char digit = map.charAt(c - 'A');
            if (digit != '0' && digit != prev) {
                code.append(digit);
            }
            prev = digit;
        }

        while (code.length() < 4)
            code.append('0');

        return code.toString();
    }

    // Logique : pour chaque token de s1, on calcule son code Soundex et on cherche au moins un token dans s2 avec le même code.
    // On retourne le ratio des tokens de s1 qui possèdent un jumeau phonétique dans s2 (valeur entre 0.0 et 1.0).
    // 1.0 signifie que tous les tokens de s1 ont un correspondant dans s2.
    public double comparer(ArrayList<String> s1, ArrayList<String> s2) {
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
            if (trouve) {
                correspondances++;
            }
        }
        return (double) correspondances / s1.size();
    }
}
