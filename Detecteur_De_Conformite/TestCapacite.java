import java.util.*;
import model.*;
import core.*;
import comparateur.*;
import generateur.*;
import pretraiteur.*;
import selectionneur.*;

public class TestCapacite {
    public static void main(String[] args) throws Exception {

        Pretraiteur[] pretraiteurs = {
          new PretraiteurMinMaj(),
            new PretraiteurSuppAccent(),
          new PretraiteurSuppPonct()
        };

      long CINQ_MIN_MS = 5L * 60 * 1000;

        System.out.println("============================================================");
        System.out.println("   CAPACITE : Nombre de noms verifiables en 5 minutes");
      System.out.println("============================================================");
        System.out.println("Coeurs disponibles : " + Runtime.getRuntime().availableProcessors());

        // Configurations a tester
        int[][] configs = {
          {1, 2},   // IndexDouble(1,2)  -- le plus permissif
            {1, 1},   // IndexDouble(1,1)
          {0, 1},   // IndexDouble(0,1)
            {0, 0}    // IndexDouble(0,0)  -- le plus strict
        };

        String[] fichiers = {
          "../peps_names_2k.csv",
            "../peps_names_64k.csv"
        };
      String[] nomsF = { "2k", "64k" };

        for (int fi = 0; fi < fichiers.length; fi++) {
          List<Name> kycFull = CsvManager.chargerCSV(fichiers[fi]);
            if (kycFull.isEmpty()) {
              System.out.println("Fichier introuvable : " + fichiers[fi]);
                continue;
            }
          System.out.println("\n--- Liste KYC : " + nomsF[fi] + " (" + kycFull.size() + " entrees) ---");
            System.out.printf("%-20s %-15s %-15s %-20s%n", "Generateur", "Temps/client", "Alertes", "Max en 5 min");
          System.out.println("------------------------------------------------------------");

            // Echantillon de 200 clients pour mesurer le temps/client
            List<Name> clients = CsvManager.chargerCSV("test_clients.csv");
          if (clients.isEmpty()) clients = kycFull.subList(0, Math.min(100, kycFull.size()));

            for (int[] cfg : configs) {
              Moteur moteur = new Moteur(new GenerateurIndexDouble(cfg[0], cfg[1]), pretraiteurs, new ComparateurJaroWinkler(), new SelectionneurPercentage(0.85));

                List<Name> kycPre = moteur.pretraiterBase(kycFull);
              List<Name> clientsPre = moteur.pretraiterBase(clients);

                // Chauffe JIT
                final Moteur mf = moteur;
              clientsPre.subList(0, Math.min(10, clientsPre.size())).parallelStream().forEach(c -> mf.rechercherPretraite(kycPre, "warmup", c));

                // Mesure reelle
                List<Alerte> alertes = Collections.synchronizedList(new ArrayList<>());
              long debut = System.currentTimeMillis();
                clientsPre.parallelStream().forEach(c -> alertes.addAll(mf.rechercherPretraite(kycPre, "bench", c)));
              long duree = System.currentTimeMillis() - debut;

                double parClient = (double) duree / clientsPre.size();
              String maxStr;
                if (parClient < 0.001) {
                  maxStr = "> 10 000 000 noms";
                } else {
                  long maxEn5min = (long) (CINQ_MIN_MS / parClient);
                    maxStr = String.format("%,d noms", maxEn5min);
                }

              System.out.printf("IndexDouble(%d,%d)      %-15s %-15s %-20s%n", cfg[0], cfg[1], String.format("%.3f ms", parClient), alertes.size() + " alertes", maxStr);
            }

          // ScanComplet seulement sur 2k (trop long sur 64k)
            if (fi == 0) {
              Moteur moteurScan = new Moteur(new GenerateurScanComplet(), pretraiteurs, new ComparateurJaroWinkler(), new SelectionneurPercentage(0.85));
                List<Name> kycPre = moteurScan.pretraiterBase(kycFull);
                List<Name> clientsPre = moteurScan.pretraiterBase(clients);
              List<Alerte> alertes = Collections.synchronizedList(new ArrayList<>());
                long debut = System.currentTimeMillis();
                final Moteur ms = moteurScan;
              clientsPre.parallelStream().forEach(c -> alertes.addAll(ms.rechercherPretraite(kycPre, "scan", c)));
                long duree = System.currentTimeMillis() - debut;
                double parClient = (double) duree / clientsPre.size();
              String maxStrScan;
                if (parClient < 0.001) {
                  maxStrScan = "> 10 000 000 noms";
                } else {
                  maxStrScan = String.format("%,d noms", (long)(CINQ_MIN_MS / parClient));
                }
              System.out.printf("ScanComplet          %-15s %-15s %-20s%n", String.format("%.3f ms", parClient), alertes.size() + " alertes", maxStrScan);
            }
        }

      System.out.println("\n============================================================");
        System.out.println("   CONCLUSION");
        System.out.println("   IndexDouble = recommande pour grandes bases KYC");
      System.out.println("   ScanComplet = exact mais lent sur > 50k entrees");
        System.out.println("============================================================");
    }
}
