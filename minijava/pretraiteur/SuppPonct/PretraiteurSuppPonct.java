package Pretraiteur.SuppPonct;

import java.util.ArrayList;
import pretraiteur.Pretraiteur;

public class PretraiteurSuppPonct implements Pretraiteur {


    public ArrayList<String> pretraiter(ArrayList<String> nomPretraite) {
        ArrayList<String> result = new ArrayList<>();
        for (String s : nomPretraite) {
            if (s == null) result.add(null);
            else result.add(s.replaceAll("\\p{Punct}", ""));
        }
        return result;
    }

    // ── Utilitaires de démo ─────────────────────────────────────────────────
    
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

    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                DÉMO : PRÉTRAITEUR SUPPRESSION PONCTUATION            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

        ArrayList<String> liste = new ArrayList<>();
        liste.add("Bonjour, monde!");
        liste.add("L'algorithme... est-il efficace?");
        liste.add("Java 17: c'est top.");
        liste.add(null);

        PretraiteurSuppPonct p = new PretraiteurSuppPonct();
        
        System.out.println("  EXEMPLES DE TRAITEMENT :");
        System.out.println("  ────────────────────────");
        
        final ArrayList<String>[] resWrapper = new ArrayList[1];
        long totalNs = chrono(() -> {
            resWrapper[0] = p.pretraiter(liste);
        });
        
        ArrayList<String> resultat = resWrapper[0];
        for (int i = 0; i < liste.size(); i++) {
            System.out.printf("  Entrée  : [%s]%n", liste.get(i));
            System.out.printf("  Sortie  : [%s]%n", resultat.get(i));
            System.out.println("  ───");
        }

        System.out.println("\n  ANALYSE DE PERFORMANCE :");
        System.out.println("  ────────────────────────");
        System.out.printf("  Temps (4 exemples) : %s%n", formatTemps(totalNs));
        
        // Petit Benchmark
        final int ITER = 100_000;
        long benchNs = chrono(() -> {
            for (int i = 0; i < ITER; i++) p.pretraiter(liste);
        });
        
        System.out.printf("  Benchmark (%d itérations) : %s%n", ITER, formatTemps(benchNs));
        System.out.printf("  Moyenne par appel : %s%n", formatTemps(benchNs / ITER));
        System.out.println("══════════════════════════════════════════════════════════════════════\n");
    }
}
