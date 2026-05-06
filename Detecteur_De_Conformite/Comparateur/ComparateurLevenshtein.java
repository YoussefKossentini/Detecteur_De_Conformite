import java.util.ArrayList;

public class ComparateurLevenshtein implements Comparateur {

    private int distance(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++)
            dp[i][0] = i;
        for (int j = 0; j <= n; j++)
            dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[m][n];
    }
//la logique ici est la suivante: Tailles égales obligatoire, puis chaque token à la même position doit avoir une distance ≤ 2

    public boolean comparer(ArrayList<String> s1, ArrayList<String> s2) {
        if (s1.size() == s2.size()) { 
            for (int i=0; i<s1.size(); i++){
                if (distance(s1.get(i), s2.get(i)) > 2) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

//MAIN DE TEST

    public static void main(String[] args) {
        ComparateurLevenshtein comparateur = new ComparateurLevenshtein();
        
        // Test 1 : Noms similaires
        ArrayList<String> nom1 = new ArrayList<>();
        nom1.add("Jean");
        nom1.add("Dupont");
        
        ArrayList<String> nom2 = new ArrayList<>();
        nom2.add("Jean");
        nom2.add("Dupoint");
        
        System.out.println("Test 1 (similaires) : " + comparateur.comparer(nom1, nom2)); // true
        
        // Test 2 : Noms très différents
        ArrayList<String> nom3 = new ArrayList<>();
        nom3.add("Pierre");
        nom3.add("Martin");
        
        ArrayList<String> nom4 = new ArrayList<>();
        nom4.add("Jean");
        nom4.add("Dupont");
        
        System.out.println("Test 2 (différents) : " + comparateur.comparer(nom3, nom4)); // false
        
        // Test 3 : Tailles différentes
        ArrayList<String> nom5 = new ArrayList<>();
        nom5.add("Jean");
        
        ArrayList<String> nom6 = new ArrayList<>();
        nom6.add("Jean");
        nom6.add("Dupont");
        
        System.out.println("Test 3 (tailles différentes) : " + comparateur.comparer(nom5, nom6)); // false
    }
}
