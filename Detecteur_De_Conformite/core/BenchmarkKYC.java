/*
 * ackage core;
 * 
 * import java.util.*;
 * import model.*;
 * import pretraiteur.*;
 * import comparateur.*;
 * import generateur.*;
 * import selectionneur.*;
 * 
 * public class BenchmarkKYC {
 * 
 * static Pretraiteur[] pretraiteurs = {
 * new PretraiteurMinMaj(),
 * new PretraiteurSuppAccent(),
 * new PretraiteurSuppPonct()
 * };
 * 
 * public static void main(String[] args) {
 * 
 * // CHEMINS DES FICHIERS
 * // Modifie ces deux chemins selon ton projet
 * String kycFile = "../peps_names_658k.csv";
 * String clientFile = "../peps_names_2k.csv";
 *
 * 
 * System.out.println(
 * "╔══════════════════════════════════════════════════════════════╗");
 * System.out.
 * println("║           BENCHMARK PRESENTATION - RESULTATS REELS           ║");
 * System.out.println(
 * "╚══════════════════════════════════════════════════════════════╝");
 * System.out.println("Coeurs disponibles : " +
 * Runtime.getRuntime().availableProcessors());
 * System.out.println();
 * 
 * // CHARGEMENT
 * long t0 = System.currentTimeMillis();
 * List<Name> kycFull = CsvManager.chargerCSV(kycFile);
 * List<Name> clientFull = CsvManager.chargerCSV(clientFile);
 * long chargement = System.currentTimeMillis() - t0;
 * 
 * System.out.println("Chargement     : " + chargement + " ms");
 * System.out.println("KYC total      : " + kycFull.size() + " entrees");
 * System.out.println("Clients total  : " + clientFull.size() + " entrees");
 * System.out.println();
 * 
 * if (kycFull.isEmpty() || clientFull.isEmpty()) {
 * System.out.
 * println("ERREUR : fichiers non trouves ou vides. Verifie les chemins.");
 * return;
 * }
 * 
 * // SECTION 1 : BENCHMARK CONFIGS (SLIDE 7 - tableau)
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.
 * println("  SECTION 1 — CONFIGS GENERATEUR  (a coller dans slide 7)");
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.printf("%-22s %-6s %-16s %-16s %-22s%n",
 * "Generateur", "Seuil", "Temps total (ms)", "Temps/client (ms)",
 * "Max clients en 5min");
 * System.out.println(
 * "─────────────────────────────────────────────────────────────");
 * 
 * int[][] configs = {
 * { 1, 2, 85 },
 * { 1, 1, 90 },
 * { 0, 1, 90 },
 * { 0, 1, 95 },
 * { 0, 0, 95 }
 * };
 * 
 * Comparateur compJaro = new ComparateurJaroWinkler();
 * 
 * for (int[] cfg : configs) {
 * int nbTok = cfg[0];
 * int nbLong = cfg[1];
 * double seuil = cfg[2] / 100.0;
 * 
 * GenerateurCandidat gen = new GenerateurIndexDouble(nbTok, nbLong);
 * Moteur moteur = new Moteur(gen, pretraiteurs, compJaro, new
 * SelectionneurPercentage(seuil));
 * 
 * List<Name> kycPre = moteur.pretraiterBase(kycFull);
 * List<Name> clientPre = moteur.pretraiterBase(clientFull);
 * 
 * // chauffe JIT
 * final Moteur mf = moteur;
 * clientPre.subList(0, Math.min(30, clientPre.size()))
 * .parallelStream()
 * .forEach(c -> mf.rechercherPretraite(kycPre, "warmup", c));
 * 
 * List<Alerte> alertes = Collections.synchronizedList(new ArrayList<>());
 * long debut = System.currentTimeMillis();
 * clientPre.parallelStream()
 * .forEach(client -> alertes
 * .addAll(mf.rechercherPretraite(kycPre, "bench", client)));
 * long duree = System.currentTimeMillis() - debut;
 * 
 * double parClient = (double) duree / clientFull.size();
 * long maxEn5min = (long) ((5.0 * 60 * 1000) / parClient);
 * 
 * System.out.printf("IndexDouble(%-4s %-2s) %-6s %-16s %-16s %-22s%n",
 * nbTok + ",", nbLong + ")",
 * seuil,
 * duree + " ms",
 * String.format("%.2f ms", parClient),
 * String.format("%,d", maxEn5min));
 * }
 * 
 * // SECTION 2 : IMPACT TAILLE KYC (SLIDE 7 - courbe)
 * System.out.println();
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.
 * println("  SECTION 2 — IMPACT TAILLE KYC (a coller dans slide 7 courbe)");
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.printf("%-12s %-20s %-20s%n",
 * "Taille KYC", "Avec IndexDouble(1,2)", "Sans index (ScanComplet)");
 * System.out.println(
 * "─────────────────────────────────────────────────────────────");
 * 
 * int nbClients = Math.min(200, clientFull.size());
 * List<Name> clientSample = clientFull.subList(0, nbClients);
 * 
 * int[] tailles = {
 * (int) (kycFull.size() * 0.1),
 * (int) (kycFull.size() * 0.25),
 * (int) (kycFull.size() * 0.50),
 * (int) (kycFull.size() * 0.75),
 * kycFull.size()
 * };
 * 
 * Comparateur compLev = new ComparateurLevenshtein();
 * 
 * for (int taille : tailles) {
 * if (taille <= 0)
 * continue;
 * List<Name> kycSlice = kycFull.subList(0, taille);
 * 
 * // avec index
 * Moteur moteurIdx = new Moteur(
 * new GenerateurIndexDouble(1, 2), pretraiteurs, compLev,
 * new SelectionneurPercentage(0.85));
 * List<Name> kycIdxPre = moteurIdx.pretraiterBase(kycSlice);
 * List<Name> clientIdxPre = moteurIdx.pretraiterBase(clientSample);
 * final Moteur mi = moteurIdx;
 * long t1 = System.currentTimeMillis();
 * clientIdxPre.parallelStream().forEach(c -> mi.rechercherPretraite(kycIdxPre,
 * "x", c));
 * long dureeIdx = System.currentTimeMillis() - t1;
 * 
 * // sans index (seulement si taille raisonnable, sinon trop long)
 * String dureeScan = "trop long";
 * if (taille <= 50000) {
 * Moteur moteurScan = new Moteur(
 * new GenerateurScanComplet(), pretraiteurs, compLev,
 * new SelectionneurPercentage(0.85));
 * List<Name> kycScanPre = moteurScan.pretraiterBase(kycSlice);
 * List<Name> clientScanPre = moteurScan.pretraiterBase(clientSample);
 * final Moteur ms = moteurScan;
 * long t2 = System.currentTimeMillis();
 * clientScanPre.parallelStream().forEach(c ->
 * ms.rechercherPretraite(kycScanPre, "x", c));
 * dureeScan = (System.currentTimeMillis() - t2) + " ms";
 * }
 * 
 * System.out.printf("%-12s %-20s %-20s%n",
 * String.format("%,d", taille),
 * dureeIdx + " ms (" + nbClients + " clients)",
 * dureeScan + " (" + nbClients + " clients)");
 * }
 * 
 * // SECTION 3 : COMPARAISON DES COMPARATEURS (SLIDE 5)
 * System.out.println();
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.
 * println("  SECTION 3 — VITESSE PAR COMPARATEUR (a coller dans slide 5)");
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.printf("%-20s %-16s %-16s %-12s%n",
 * "Comparateur", "Temps total (ms)", "Temps/client (ms)", "Alertes");
 * System.out.println(
 * "─────────────────────────────────────────────────────────────");
 * 
 * List<Name> kycPreCache;
 * {
 * Moteur m = new Moteur(new GenerateurIndexDouble(1, 2), pretraiteurs,
 * new ComparateurJaroWinkler(), new SelectionneurPercentage(0.85));
 * kycPreCache = m.pretraiterBase(kycFull);
 * }
 * 
 * Comparateur[] comps = {
 * new ComparateurLevenshtein(),
 * new ComparateurJaroWinkler(),
 * new ComparateurSoundex()
 * };
 * String[] nomComps = { "Levenshtein", "Jaro-Winkler", "Soundex" };
 * 
 * for (int i = 0; i < comps.length; i++) {
 * Moteur m = new Moteur(new GenerateurIndexDouble(1, 2), pretraiteurs,
 * comps[i], new SelectionneurPercentage(0.85));
 * List<Name> clientPre = m.pretraiterBase(clientFull);
 * final Moteur mf = m;
 * final List<Name> kycRef = kycPreCache;
 * 
 * // chauffe
 * clientPre.subList(0, Math.min(20, clientPre.size()))
 * .parallelStream()
 * .forEach(c -> mf.rechercherPretraite(kycRef, "warmup", c));
 * 
 * List<Alerte> alertes = Collections.synchronizedList(new ArrayList<>());
 * long debut = System.currentTimeMillis();
 * clientPre.parallelStream()
 * .forEach(c -> alertes.addAll(mf.rechercherPretraite(kycRef, "bench", c)));
 * long duree = System.currentTimeMillis() - debut;
 * double parClient = (double) duree / clientFull.size();
 * 
 * System.out.printf("%-20s %-16s %-16s %-12s%n",
 * nomComps[i],
 * duree + " ms",
 * String.format("%.2f ms", parClient),
 * alertes.size());
 * }
 * 
 * // RECAP FINAL
 * System.out.println();
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.println("  RECAP A COPIER DANS LE PROMPT DOKIE");
 * System.out.println(
 * "══════════════════════════════════════════════════════════════");
 * System.out.println("Machine    : " +
 * Runtime.getRuntime().availableProcessors() + " coeurs");
 * System.out.println("Liste KYC  : " + String.format("%,d", kycFull.size()) +
 * " entrees");
 * System.out.println("Clients    : " + String.format("%,d", clientFull.size())
 * + " entrees");
 * System.out.println("Chargement : " + chargement + " ms");
 * System.out.println();
 * System.out.
 * println("→ Remplace les valeurs approximatives dans le prompt Dokie");
 * System.out.
 * println("  par les chiffres de la Section 1 et Section 2 ci-dessus.");
 * System.out.println(
 * "╚══════════════════════════════════════════════════════════════╝");
 * }
 * }
 */