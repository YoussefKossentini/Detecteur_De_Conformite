package Detecteur_De_Conformite.Pretraiteur.titres;

public class DemoDetecteurTitres {

    // ── Exemples : un nom complet par longueur de titre (1 → 8 mots) ───────
    // Le titre embarqué dans chaque entrée DOIT exister dans titres.txt
    private static final String[][] EXEMPLES = {
        { "monsieur jean dupont",
          "1 mot  — civil simple" },
        { "l'honorable ahmed kassaj",
          "2 mots — honorifique" },
        { "capitaine de frégate marie bernard",
          "3 mots — grade naval" },
        { "général de brigade de terre pierre martin",
          "5 mots — grade armée de terre" },
        { "général de division de terre claude duval",
          "5 mots — division de terre" },
        { "colonel de l'armée de terre jean dupont",
          "6 mots — armée de terre" },
        { "lieutenant colonel de l'armée de terre sophie leclerc",
          "7 mots — grade complet terre" },
        { "général de corps d'armée de terre thomas blanc",
          "7 mots — corps armée de terre" }
    };

    public static void main(String[] args) {
        String fichier = args.length > 0 ? args[0] : "Detecteur_De_Conformite/Pretraiteur/titres/titres.txt";

        long t0 = System.nanoTime();
        DictTitres dict = new DictTitres(fichier);
        long chargementNs = System.nanoTime() - t0;

        ExtracteurTitre extracteur = new ExtracteurTitre(dict);

        printHeader(dict, fichier, chargementNs);
        sectionExemples(extracteur, dict);
        sectionBenchmarkParLongueur(dict);
        sectionBenchmarkGlobal(dict);
    }

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

    // ── En-tête ────────────────────────────────────────────────────────────
    static void printHeader(DictTitres dict, String fichier, long chargementNs) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║        ALGORITHME DÉTECTEUR DE TITRES — LONGEST MATCH FIRST         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        System.out.printf("  Fichier      : %s%n",        fichier);
        System.out.printf("  Dictionnaire : %d titres%n", dict.getTailleDictionnaire());
        System.out.printf("  Fenêtre max  : %d mots%n",   dict.getMaxMotsParTitre());
        System.out.printf("  Chargement   : %s%n",        formatTemps(chargementNs));
        System.out.println();
    }

    // ── Exemples 1 → 8 mots ───────────────────────────────────────────────
    static void sectionExemples(ExtracteurTitre extracteur, DictTitres dict) {
        System.out.println("══════════════════════════════════════════════════════════════════════");
        System.out.println("  EXEMPLES PAR LONGUEUR DE TITRE (1 → 8 MOTS)");
        System.out.println("  Vérification : titres détectés via titres.txt");
        System.out.println("══════════════════════════════════════════════════════════════════════\n");

        long totalNs = 0;
        int  ok      = 0;
        int  ko      = 0;

        for (int idx = 0; idx < EXEMPLES.length; idx++) {
            String entree      = EXEMPLES[idx][0];
            String description = EXEMPLES[idx][1];
            int    longueurAttendue = Integer.parseInt(description.split(" ")[0]);

            // Mesure à froid (un seul appel réel)
            long ns = chrono(() -> extracteur.extraireTitreSeul(entree));
            totalNs += ns;

            // Résultats réels issus du dictionnaire chargé
            String titre      = extracteur.extraireTitreSeul(entree);
            String nomRestant = extracteur.supprimerTitre(entree);
            int    nbMotsDet  = dict.getNbMotsTitre(entree);

            // Vérification : le titre détecté doit exister dans titres.txt
            // ET avoir la longueur attendue pour cet exemple
            boolean titreExisteDansDict = titre != null && dict.contient(titre);
            boolean longueurCorrecte    = nbMotsDet == longueurAttendue;
            boolean valide              = titreExisteDansDict && longueurCorrecte;

            if (valide) ok++; else ko++;

            System.out.printf("  ┌─ Exemple %d  [%s]%n", idx + 1, description);
            System.out.printf("  │  Entrée           : \"%s\"%n", entree);
            System.out.printf("  │  Titre détecté    : \"%s\"%n",
                    titre != null ? titre : "— (aucun)");
            System.out.printf("  │  Dans titres.txt  : %s%n",
                    titreExisteDansDict ? "✓ OUI" : "✗ NON — titre absent du dictionnaire");
            System.out.printf("  │  Longueur         : %d mot%s détecté%s  (attendu : %d)  %s%n",
                    nbMotsDet, nbMotsDet > 1 ? "s" : "", nbMotsDet > 1 ? "s" : "",
                    longueurAttendue,
                    longueurCorrecte ? "✓" : "✗");
            System.out.printf("  │  Nom restant      : \"%s\"%n", nomRestant);
            System.out.printf("  │  Temps appel      : %s  (à froid)%n", formatTemps(ns));
            System.out.printf("  │  Statut           : %s%n", valide ? "✓ VALIDE" : "✗ ÉCHEC");
            System.out.println("  └──────────────────────────────────────────────────────────────────\n");
        }

        System.out.printf("  Bilan    : %d/%d valides  |  %d échec%s%n",
                ok, EXEMPLES.length, ko, ko > 1 ? "s" : "");
        System.out.printf("  Total    : %s pour %d appels%n%n",
                formatTemps(totalNs), EXEMPLES.length);
    }

    // ── Benchmark par longueur (1 → 8 mots) ───────────────────────────────
    static void sectionBenchmarkParLongueur(DictTitres dict) {
        final int ITER = 200_000;

        System.out.println("══════════════════════════════════════════════════════════════════════");
        System.out.printf("  BENCHMARK PAR LONGUEUR  (%,d itérations par longueur)%n", ITER);
        System.out.println("══════════════════════════════════════════════════════════════════════\n");
        System.out.println("  Longueur │ Exemple                                    │   Total   │ µs/appel │ Mops/s");
        System.out.println("  ─────────┼────────────────────────────────────────────┼───────────┼──────────┼───────");

        for (int idx = 0; idx < EXEMPLES.length; idx++) {
            String entree   = EXEMPLES[idx][0];
            int    longueur = idx + 1;

            // Warm-up JIT
            for (int w = 0; w < 1000; w++) dict.extraireTitre(entree);

            long elapsed = chrono(() -> {
                for (int i = 0; i < ITER; i++) dict.extraireTitre(entree);
            });

            double usPerCall = elapsed / (double) ITER / 1_000.0;
            double mops      = ITER / (elapsed / 1_000.0);

            String label = entree.length() > 42 ? entree.substring(0, 39) + "..." : entree;
            System.out.printf("  %d mot%s    │ %-42s │ %9s │  %6.4f  │ %6.2f%n",
                    longueur, longueur > 1 ? "s" : " ",
                    label,
                    formatTemps(elapsed),
                    usPerCall,
                    mops);
        }
        System.out.println();
    }

    // ── Benchmark global 1M appels (mix 1–8 mots) ─────────────────────────
    static void sectionBenchmarkGlobal(DictTitres dict) {
        final int ITER = 1_000_000;

        String[] pool = new String[EXEMPLES.length];
        for (int i = 0; i < EXEMPLES.length; i++) pool[i] = EXEMPLES[i][0];
        int poolLen = pool.length;

        System.out.println("══════════════════════════════════════════════════════════════════════");
        System.out.printf("  BENCHMARK GLOBAL — %,d appels (mix 1–8 mots)%n", ITER);
        System.out.println("══════════════════════════════════════════════════════════════════════\n");

        // Warm-up JIT
        for (int w = 0; w < 5000; w++)
            for (String s : pool) dict.extraireTitre(s);

        int[] detected = {0};
        long elapsed = chrono(() -> {
            for (int i = 0; i < ITER; i++) {
                if (dict.extraireTitre(pool[i % poolLen]) != null) detected[0]++;
            }
        });

        double totalMs   = elapsed / 1_000_000.0;
        double usPerCall = elapsed / (double) ITER / 1_000.0;
        double opsPerSec = ITER / (totalMs / 1_000.0);
        double mNomMin   = opsPerSec * 60.0 / 1_000_000.0;

        System.out.printf("  Temps total  : %s%n",           formatTemps(elapsed));
        System.out.printf("  Par appel    : %8.4f µs%n",     usPerCall);
        System.out.printf("  Titres       : %,d / %,d  (%.0f %%)%n",
                detected[0], ITER, detected[0] * 100.0 / ITER);
        System.out.printf("  Débit        : %,.0f appels/s%n", opsPerSec);
        System.out.printf("  Débit        : %.1f M noms/min%n", mNomMin);
        System.out.println("\n══════════════════════════════════════════════════════════════════════");
    }
}
