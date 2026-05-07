package Detecteur_De_Conformite.Comparateur;

import java.util.ArrayList;

public class ComparateurSoundex implements Comparateur {

    private String soundex(String s) {
        if (s == null || s.isEmpty())
            return "";

        s = s.toUpperCase();
        StringBuilder code = new StringBuilder();
        code.append(s.charAt(0));

        String map = "01230120022455012623010202";  // Tableau de correspondance Soundex
        char firstChar = s.charAt(0);
        if (firstChar < 'A' || firstChar > 'Z') return "0000";

        char prev = map.charAt(firstChar - 'A');

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

    @Override
    public double comparer(ArrayList<String> s1, ArrayList<String> s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty())
            return 0.0;

        int correspondances = 0;
        for (String t1 : s1) {
            boolean trouve = false;
            String code1 = soundex(t1);
            for (String t2 : s2) {
                if (code1.equals(soundex(t2))) {
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
