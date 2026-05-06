package pretraiteur.SuppAccent;
import java.text.Normalizer;
import java.util.ArrayList;
import pretraiteur.Pretraiteur;
public class PretraiteurSuppAccent implements Pretraiteur {
    //traitement d'un seul mot
    public String preatraiter(String s) {
        if (s == null) return null;
        String normalisee = Normalizer.normalize(s, Normalizer.Form.NFD);
        String sansAccent = normalisee.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sansAccent;
    }

    //Redéfinition pour traiter une liste de mot 
    public ArrayList<String> pretraiter(ArrayList<String> nomPretraite) {
        ArrayList<String> resultat = new ArrayList<String>();
        for (String mot : nomPretraite) {
            resultat.add(preatraiter(mot));
        }
        return resultat;
    }



    // MAIN GENEREE PAR IA
    static long chrono(Runnable bloc) {
        long t0 = System.nanoTime();
        bloc.run();
        return System.nanoTime() - t0;
    }

    static String formatTemps(long ns) {
        if (ns < 1_000)          return ns + " ns";
        if (ns < 1_000_000)      return String.format("%.2f µs", ns / 1_000.0);
        if (ns < 1_000_000_000L) return String.format("%.2f ms", ns / 1_000_000.0);
        return                          String.format("%.3f s",  ns / 1_000_000_000.0);
    }

    static ArrayList<String> genererListe(int n) {
        String[] pool = {
            "éléphant", "àpreté", "çà", "ïle", "ûtre",
            "crème", "naïf", "forêt", "château", "résumé"
        };
        ArrayList<String> liste = new ArrayList<>(n);
        for (int i = 0; i < n; i++) liste.add(pool[i % pool.length]);
        return liste;
    }

    public static void main(String[] args) {
        PretraiteurSuppAccent pretraitement = new PretraiteurSuppAccent();

        // ── Tests fonctionnels ─────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       TEST PRETRAITEUR SUPP ACCENT           ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        ArrayList<String> test1 = new ArrayList<>();
        test1.add("éléphant"); test1.add("château"); test1.add("résumé");
        System.out.println("Test 1 - Accents courants :");
        System.out.println("  Avant : " + test1);
        System.out.println("  Après : " + pretraitement.pretraiter(test1));

        ArrayList<String> test2 = new ArrayList<>();
        test2.add("naïf"); test2.add("forêt"); test2.add("çà");
        System.out.println("\nTest 2 - Tréma, circonflexe, cédille :");
        System.out.println("  Avant : " + test2);
        System.out.println("  Après : " + pretraitement.pretraiter(test2));

        ArrayList<String> test3 = new ArrayList<>();
        test3.add("Ahmed"); test3.add(null); test3.add("Kssaj");
        System.out.println("\nTest 3 - Sans accents et null :");
        System.out.println("  Avant : " + test3);
        System.out.println("  Après : " + pretraitement.pretraiter(test3));

        // ── Test de complexité ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("  TEST DE COMPLEXITÉ — O(n)");
        System.out.println("══════════════════════════════════════════════\n");

        int[] tailles  = {1_000, 10_000, 100_000, 500_000, 1_000_000};
        final int ITER = 10;

        System.out.println("  ┌────────────┬────────────┬──────────────┬──────────────────────┐");
        System.out.println("  │ Taille (n) │ Temps moy  │ ns/élément   │ Ratio                │");
        System.out.println("  ├────────────┼────────────┼──────────────┼──────────────────────┤");

        long tempsPrec  = -1;
        int  taillePrec = -1;

        for (int taille : tailles) {
            ArrayList<String> liste = genererListe(taille);

            for (int w = 0; w < 3; w++) pretraitement.pretraiter(liste);

            long total = 0;
            for (int r = 0; r < ITER; r++)
                total += chrono(() -> pretraitement.pretraiter(liste));
            long   moyNs     = total / ITER;
            double nsParElem = moyNs / (double) taille;

            String ratio = "  —";
            if (tempsPrec > 0) {
                double fT = (double) taille / taillePrec;
                double fN = (double) moyNs  / tempsPrec;
                ratio = String.format("x%.2f (taille x%.0f)", fN, fT);
            }

            System.out.printf("  │ %10d │ %10s │ %10.2f ns │ %-20s │%n",
                    taille, formatTemps(moyNs), nsParElem, ratio);

            tempsPrec  = moyNs;
            taillePrec = taille;
        }

        System.out.println("  └────────────┴────────────┴──────────────┴──────────────────────┘");
        System.out.println();
        System.out.println("  Interprétation :");
        System.out.println("  → Ratio ≈ x10 quand taille x10  =  O(n) confirmé");
        System.out.println("  → Ratio << x10                  =  optimisations JIT");
    }
}