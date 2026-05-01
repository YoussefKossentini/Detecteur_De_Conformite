package pretraiteur.MinMaj;
import java.util.ArrayList;
import pretraiteur.Pretraiteur;
public class PretraiteurMinMaj implements Pretraiteur {
    public ArrayList<String> pretraiter(ArrayList<String> nomPretraite) {
        ArrayList<String> resultat = new ArrayList<>();
        for (String mot : nomPretraite) {
            if (mot != null && !mot.isEmpty()) {
                resultat.add(mot.toLowerCase().trim());
            }
        }
        return resultat;
    }

//TOUT LES MAIN SONT GENEREE PAR IA
    // ── Utilitaires chrono ─────────────────────────────────────────────────
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

    // ── Génère une liste de N mots aléatoires en majuscules ───────────────
    static ArrayList<String> genererListe(int n) {
        String[] pool = {
            "AHMED", "KASSAB", "MONSIEUR", "JEAN", "PIERRE",
            "MARIE", "DUPONT", "BERNARD", "MARTIN", "LECLERC"
        };
        ArrayList<String> liste = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            liste.add(pool[i % pool.length]);
        }
        return liste;
    }

    public static void main(String[] args) {
        PretraiteurMinMaj pretraitement = new PretraiteurMinMaj();

        // ── Tests fonctionnels ─────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         TEST PRETRAITEUR MIN/MAJ             ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        ArrayList<String> test1 = new ArrayList<>();
        test1.add("Ahmed"); test1.add("KASSAB"); test1.add("Ali");
        System.out.println("Test 1 - Majuscules :");
        System.out.println("  Avant : " + test1);
        System.out.println("  Après : " + pretraitement.pretraiter(test1));

        ArrayList<String> test2 = new ArrayList<>();
        test2.add("MonSIEUR"); test2.add("Jean-PiErre");
        System.out.println("\nTest 2 - Mixte :");
        System.out.println("  Avant : " + test2);
        System.out.println("  Après : " + pretraitement.pretraiter(test2));

        ArrayList<String> test3 = new ArrayList<>();
        test3.add("  AHMED  "); test3.add(null); test3.add("Kssaj");
        System.out.println("\nTest 3 - Espaces et null :");
        System.out.println("  Avant : " + test3);
        System.out.println("  Après : " + pretraitement.pretraiter(test3));

        // ── Test de complexité ─────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("  TEST DE COMPLEXITÉ — O(n)");
        System.out.println("══════════════════════════════════════════════\n");
        System.out.println("  La complexité attendue est O(n) :");
        System.out.println("  doubler n doit doubler le temps.\n");

        int[] tailles = {1_000, 10_000, 100_000, 500_000, 1_000_000};
        final int ITER = 10; // moyennage sur 10 runs

        System.out.println("  ┌────────────┬────────────┬──────────────┬──────────┐");
        System.out.println("  │ Taille (n) │ Temps moy  │ ns/élément   │ Ratio    │");
        System.out.println("  ├────────────┼────────────┼──────────────┼──────────┤");

        long tempsPrec = -1;
        int  taillePrec = -1;

        for (int taille : tailles) {
            ArrayList<String> liste = genererListe(taille);

            // Warm-up
            for (int w = 0; w < 3; w++) pretraitement.pretraiter(liste);

            // Moyenne sur ITER runs
            long total = 0;
            for (int r = 0; r < ITER; r++) {
                total += chrono(() -> pretraitement.pretraiter(liste));
            }
            long moyNs = total / ITER;
            double nsParElem = moyNs / (double) taille;

            // Ratio par rapport à la taille précédente
            String ratio = "  —";
            if (tempsPrec > 0) {
                double facteurTaille = (double) taille / taillePrec;
                double facteurTemps  = (double) moyNs  / tempsPrec;
                ratio = String.format("x%.2f (taille x%.0f)", facteurTemps, facteurTaille);
            }

            System.out.printf("  │ %10d │ %10s │ %10.2f ns │ %-8s │%n",
                    taille, formatTemps(moyNs), nsParElem, ratio);

            tempsPrec  = moyNs;
            taillePrec = taille;
        }

        System.out.println("  └────────────┴────────────┴──────────────┴──────────┘");
        System.out.println();
        System.out.println("  Interprétation :");
        System.out.println("  → Ratio ≈ x10 quand taille x10  =  O(n) confirmé");
        System.out.println("  → Ratio << x10                  =  optimisations JIT");
    }
}