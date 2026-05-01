package Comparateur;
import java.util.ArrayList;



public class ComparateurSoundex implements Comparateur {

    private String soundex(String s) {
        if (s == null || s.isEmpty())
            return "";

        s = s.toUpperCase();
        StringBuilder code = new StringBuilder();
        code.append(s.charAt(0));

        String map = "01230120022455012623010202";
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



//MAIN EXPle
    public boolean comparer(ArrayList<String> s1, ArrayList<String> s2) {
        if (s1.size() == s2.size()) {
            for (int i = 0; i < s1.size(); i++) {
                if (!soundex(s1.get(i)).equals(soundex(s2.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("Smith");
        s1.add("John");

        ArrayList<String> s2 = new ArrayList<>();
        s2.add("Smyth");
        s2.add("Jahn");

        ComparateurSoundex c = new ComparateurSoundex();
        System.out.println(c.comparer(s1, s2));
    }
}