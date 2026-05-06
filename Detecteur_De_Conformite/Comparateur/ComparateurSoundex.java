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

    public boolean comparer(ArrayList<String> s1, ArrayList<String> s2) {
    }
}