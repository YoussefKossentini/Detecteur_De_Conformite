import java.util.*;
import model.*;
import core.*;
import comparateur.*;
import generateur.*;
import pretraiteur.*;
import selectionneur.*;

public class TestPerf {

  // Utilitaire : prend les N premiers elements (ou tous si moins)
  private static List<Name> limiter(List<Name> liste, int n) {
    if (liste.size() <= n)
      return liste;
    return new ArrayList<>(liste.subList(0, n));
  }

  // Benchmark d'un moteur sur une base KYC et un lot de clients
  // Retourne la duree en ms
  private static long benchmarker(Moteur moteur, List<Name> kycPre, List<Name> clientsPre, String tag) {
    List<Alerte> alertes = Collections.synchronizedList(new ArrayList<>());
    final Moteur m = moteur;
    long debut = System.currentTimeMillis();
    clientsPre.parallelStream().forEach(c -> alertes.addAll(m.rechercherPretraite(kycPre, tag, c)));
    long duree = System.currentTimeMillis() - debut;
    System.out.printf("    Alertes : %-6d | Duree totale : %6d ms | Temps/client : %.3f ms%n", alertes.size(), duree,
        (double) duree / clientsPre.size());
    return duree;
  }

  public static void main(String[] args) throws Exception {

    final int NB_CLIENTS = 1000; // nombre d'entrees clients a mesurer

    Pretraiteur[] pretraiteurs = {
        new PretraiteurMinMaj(),
        new PretraiteurSuppAccent(),
        new PretraiteurSuppPonct()
    };

    // Fichiers CSV de reference (bases KYC) a tester
    String[] fichiersCsv = {
        "../peps_names_800.csv",
        "../peps_names_1k.csv",
        "../peps_names_2k.csv",
        "../peps_names_64k.csv",
        "../peps_names_658k.csv"
    };
    String[] nomsCSV = { "800", "1k", "2k", "64k", "658k" };

    // Chargement du fichier clients (limité a NB_CLIENTS entrees)
    List<Name> clientsRaw = CsvManager.chargerCSV("test_clients.csv");
    List<Name> clients = limiter(clientsRaw, NB_CLIENTS);
    System.out.println("=".repeat(70));
    System.out.printf("  BENCHMARK TEMPS D'EXECUTION PAR FICHIER CSV  (%d entrees clients)%n", clients.size());
    System.out.println("=".repeat(70));
    System.out.printf("  Clients charges : %d  (demande : %d)%n", clients.size(), NB_CLIENTS);
    System.out.println();

    // Boucle principale : un bloc par fichier CSV
    for (int fi = 0; fi < fichiersCsv.length; fi++) {
      String fichier = fichiersCsv[fi];
      String nomF = nomsCSV[fi];

      System.out.println("-".repeat(70));
      System.out.printf("  FICHIER KYC : %s  (%s)%n", fichier, nomF);
      System.out.println("-".repeat(70));

      List<Name> kycFull = CsvManager.chargerCSV(fichier);
      if (kycFull.isEmpty()) {
        System.out.println("  [!] Fichier introuvable ou vide -- passe.\n");
        continue;
      }
      System.out.printf("  Entrees KYC chargees : %d%n%n", kycFull.size());

      // Test A : ScanComplet + JaroWinkler
      System.out.println("  [A] ScanComplet + JaroWinkler");
      {
        Moteur moteur = new Moteur(new GenerateurScanComplet(), pretraiteurs, new ComparateurJaroWinkler(),
            new SelectionneurPercentage(0.85));

        List<Name> kycPre = moteur.pretraiterBase(kycFull);
        List<Name> clientsPre = moteur.pretraiterBase(clients);

        // Chauffe JIT
        final Moteur mw = moteur;
        clientsPre.subList(0, Math.min(10, clientsPre.size())).parallelStream()
            .forEach(c -> mw.rechercherPretraite(kycPre, "warmup", c));

        long debut = System.nanoTime();
        benchmarker(moteur, kycPre, clientsPre, "ScanComplet-" + nomF);
        long dureeNs = System.nanoTime() - debut;
        System.out.printf("    Duree precise : %.6f s%n%n", dureeNs / 1_000_000_000.0);
      }

      // Test B : IndexDouble(1,2) + JaroWinkler
      System.out.println("  [B] IndexDouble(1,2) + JaroWinkler");
      {
        Moteur moteur = new Moteur(new GenerateurIndexDouble(1, 2), pretraiteurs, new ComparateurJaroWinkler(),
            new SelectionneurPercentage(0.85));

        List<Name> kycPre = moteur.pretraiterBase(kycFull);
        List<Name> clientsPre = moteur.pretraiterBase(clients);

        final Moteur mw = moteur;
        clientsPre.subList(0, Math.min(10, clientsPre.size())).parallelStream()
            .forEach(c -> mw.rechercherPretraite(kycPre, "warmup", c));

        long debut = System.nanoTime();
        benchmarker(moteur, kycPre, clientsPre, "IndexDouble-" + nomF);
        long dureeNs = System.nanoTime() - debut;
        System.out.printf("    Duree precise : %.6f s%n%n", dureeNs / 1_000_000_000.0);
      }

      // Test C : IndexTokens + JaroWinkler
      System.out.println("  [C] IndexTokens + JaroWinkler");
      {
        Moteur moteur = new Moteur(new GenerateurIndexTokens(1), pretraiteurs, new ComparateurJaroWinkler(),
            new SelectionneurPercentage(0.85));

        List<Name> kycPre = moteur.pretraiterBase(kycFull);
        List<Name> clientsPre = moteur.pretraiterBase(clients);

        final Moteur mw = moteur;
        clientsPre.subList(0, Math.min(10, clientsPre.size())).parallelStream()
            .forEach(c -> mw.rechercherPretraite(kycPre, "warmup", c));

        long debut = System.nanoTime();
        benchmarker(moteur, kycPre, clientsPre, "IndexTokens-" + nomF);
        long dureeNs = System.nanoTime() - debut;
        System.out.printf("    Duree precise : %.6f s%n%n", dureeNs / 1_000_000_000.0);
      }
    }

    // Tableau recapitulatif (anciens tests conserves)
    System.out.println("=".repeat(70));
    System.out.println("  TESTS ORIGINAUX (conserves a titre de reference)");
    System.out.println("=".repeat(70));

    // Test 1 : ScanComplet sur petits fichiers
    System.out.println("\n=== TEST 1 : ScanComplet (test_clients vs peps_names_800) ===");
    List<Name> kyc800 = CsvManager.chargerCSV("../peps_names_800.csv");
    List<Name> clientsOrig = CsvManager.chargerCSV("test_clients.csv");
    System.out.println("KYC : " + kyc800.size() + " entrees | Clients : " + clientsOrig.size());

    Moteur moteurScan = new Moteur(new GenerateurScanComplet(), pretraiteurs, new ComparateurJaroWinkler(),
        new SelectionneurPercentage(0.85));

    List<Name> kycPre = moteurScan.pretraiterBase(kyc800);
    List<Name> clientsPre = moteurScan.pretraiterBase(clientsOrig);

    long t0 = System.currentTimeMillis();
    List<Alerte> alertesScan = Collections.synchronizedList(new ArrayList<>());
    final Moteur ms = moteurScan;
    clientsPre.parallelStream().forEach(c -> alertesScan.addAll(ms.rechercherPretraite(kycPre, "ScanComplet", c)));
    long dureeScan = System.currentTimeMillis() - t0;
    System.out.println("Alertes : " + alertesScan.size() + " | Duree : " + dureeScan + " ms");
    for (Alerte a : alertesScan)
      System.out.printf("  [%s] %s <-> [%s] %s (%.0f%%)%n", a.getIdClient(), a.getNomClient(), a.getIdTrouve(),
          a.getNomTrouve(), a.getScore() * 100);

    // Test 2 : IndexDouble
    System.out.println("\n=== TEST 2 : IndexDouble(1,2) sur meme donnees ===");
    Moteur moteurIdx = new Moteur(new GenerateurIndexDouble(1, 2), pretraiteurs, new ComparateurJaroWinkler(),
        new SelectionneurPercentage(0.85));

    List<Name> kycIdxPre = moteurIdx.pretraiterBase(kyc800);
    List<Name> clientsIdxPre = moteurIdx.pretraiterBase(clientsOrig);

    long t1 = System.currentTimeMillis();
    List<Alerte> alertesIdx = Collections.synchronizedList(new ArrayList<>());
    final Moteur mi = moteurIdx;
    clientsIdxPre.parallelStream().forEach(c -> alertesIdx.addAll(mi.rechercherPretraite(kycIdxPre, "IndexDouble", c)));
    long dureeIdx = System.currentTimeMillis() - t1;
    System.out.println("Alertes : " + alertesIdx.size() + " | Duree : " + dureeIdx + " ms");
    for (Alerte a : alertesIdx)
      System.out.printf("  [%s] %s <-> [%s] %s (%.0f%%)%n", a.getIdClient(), a.getNomClient(), a.getIdTrouve(),
          a.getNomTrouve(), a.getScore() * 100);

    // Test 3 : IndexDouble sur peps_names_2k
    System.out.println("\n=== TEST 3 : IndexDouble(1,2) sur peps_names_2k (grand dataset) ===");
    List<Name> kyc2k = CsvManager.chargerCSV("../peps_names_2k.csv");
    System.out.println("KYC : " + kyc2k.size() + " entrees | Clients : " + clientsOrig.size());

    Moteur moteur2k = new Moteur(new GenerateurIndexDouble(1, 2), pretraiteurs, new ComparateurJaroWinkler(),
        new SelectionneurPercentage(0.85));

    List<Name> kyc2kPre = moteur2k.pretraiterBase(kyc2k);
    List<Name> clients2kPre = moteur2k.pretraiterBase(clientsOrig);

    long t2 = System.currentTimeMillis();
    List<Alerte> alertes2k = Collections.synchronizedList(new ArrayList<>());
    final Moteur m2k = moteur2k;
    clients2kPre.parallelStream().forEach(c -> alertes2k.addAll(m2k.rechercherPretraite(kyc2kPre, "peps_2k", c)));
    long duree2k = System.currentTimeMillis() - t2;
    System.out.println("Alertes : " + alertes2k.size() + " | Duree : " + duree2k + " ms");
    System.out.printf("Temps/client : %.2f ms%n", (double) duree2k / clientsOrig.size());

    System.out.println("\n========== TOUS LES TESTS DE PERFORMANCE TERMINES ==========");
  }
}
