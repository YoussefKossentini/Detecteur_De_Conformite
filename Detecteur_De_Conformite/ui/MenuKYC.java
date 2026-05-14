package ui;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.*;
import core.*;
import pretraiteur.*;
import comparateur.*;
import generateur.*;
import selectionneur.*;

public class MenuKYC {
    private KycContext ctx = new KycContext();
  private Scanner sc = new Scanner(System.in);
    private boolean[] cacheListeValide = new boolean[0];

    public static void main(String[] args) {
      new MenuKYC().run();
    }

    public void run() {
        int choix = -1;
      while (choix != 6) {
            afficherMenu();
          System.out.print("Votre choix : ");
            try {
              choix = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
              System.out.println("  Entree invalide.\n");
                continue;
            }
            switch (choix) {
                case 1:
                  verifierUnNom();
                    break;
                case 2:
                  verifierFichierClients();
                    break;
                case 3:
                  ajouterListeControle();
                    break;
                case 4:
                  mettreAJourClients();
                    break;
                case 5:
                  configurerParametres();
                    break;
                case 6:
                    System.out.println("\n  Au revoir.\n");
                    break;
                default:
                  System.out.println("  Choix invalide (1-6).\n");
            }
        }
    }

    private void afficherMenu() {
        System.out.println("\n========================================");
      System.out.println("         OUTIL DE CONFORMITE KYC        ");
        System.out.println("========================================");
        StringBuilder comps = new StringBuilder();
      for (int i = 0; i < ctx.comparateursActifs.length; i++) {
            if (ctx.comparateursActifs[i]) {
              if (comps.length() > 0)
                    comps.append("+");
              comps.append(ctx.nomComparateurs[i]);
            }
        }
        System.out.println("  Comp.       : " + (comps.length() == 0 ? "Aucun" : comps.toString()));
      System.out.println("  Orchestra.  : " + ctx.orchestrationComparateur + "  |  Seuil : " + ctx.seuil);
        System.out.println("  Listes KYC  : " + ctx.listesControle.size() + "  |  Clients : " + ctx.baseClients.size());
        System.out.println("----------------------------------------");
      System.out.println("  1. Verifier un nom");
        System.out.println("  2. Verifier un fichier clients");
        System.out.println("  3. Ajouter une liste de controle");
      System.out.println("  4. Mettre a jour la base clients");
        System.out.println("  5. Configurer les parametres");
        System.out.println("  6. Quitter");
      System.out.println("========================================");
    }

    private void verifierUnNom() {
        if (ctx.listesControle.isEmpty()) {
          System.out.println("  Aucune liste de controle chargee.");
            return;
        }
      System.out.print("  Nom a verifier : ");
        String saisie = sc.nextLine().trim();
        if (saisie.isEmpty()) {
          System.out.println("  Nom vide.");
            return;
        }
      System.out.println("\n  Recherche : \"" + saisie + "\"");
        StringJoiner listes = new StringJoiner(", ");
        for (int i = 0; i < ctx.listesControle.size(); i++)
          if (ctx.listesActives[i])
                listes.add(ctx.nomsListesControle.get(i));
        System.out.println("  Listes : " + listes);
      executerRecherche(Collections.singletonList(new Name("REQUETE", saisie.split("\\s+"))), false, false);
    }

    private void verifierFichierClients() {
        if (ctx.listesControle.isEmpty()) {
          System.out.println("  Aucune liste de controle chargee.");
            return;
        }
      if (ctx.baseClients.isEmpty()) {
            System.out.println("  Base clients vide.");
            return;
        }
      System.out.println("\n  RAPPORT BATCH");
        System.out.println("  Date : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        StringJoiner listes = new StringJoiner(", ");
      for (int i = 0; i < ctx.listesControle.size(); i++)
            if (ctx.listesActives[i])
              listes.add(ctx.nomsListesControle.get(i));
        System.out.println("  Listes : " + listes);
      executerRecherche(ctx.baseClients, true, false);
    }

    private void executerRecherche(List<Name> clients, boolean isBatch, boolean earlyExit) {
        long tailleTotale = 0;
      for (int i = 0; i < ctx.listesControle.size(); i++)
            if (ctx.listesActives[i])
              tailleTotale += ctx.listesControle.get(i).size();

        boolean grandeBase = tailleTotale > 1000;
      boolean grandBatch = clients.size() > 100;
        GenerateurCandidat gen = (grandeBase || grandBatch) ? new GenerateurIndexDouble(1, 2) : new GenerateurScanComplet();

        Moteur moteur = construireMoteur(gen);
      List<Alerte> toutesAlertes = Collections.synchronizedList(new ArrayList<>());

        System.out.println("  Recherche en cours...");
        long t0 = System.currentTimeMillis();

      // redimensionner le cache si on a ajoute des listes
        if (cacheListeValide.length != ctx.listesControle.size()) {
          boolean[] ancien = cacheListeValide;
            cacheListeValide = new boolean[ctx.listesControle.size()];
            System.arraycopy(ancien, 0, cacheListeValide, 0, Math.min(ancien.length, cacheListeValide.length));
        }

      while (ctx.listesPretraitees.size() < ctx.listesControle.size())
            ctx.listesPretraitees.add(null);

        // pretraiter uniquement les listes pas encore en cache
        for (int i = 0; i < ctx.listesControle.size(); i++) {
          if (!cacheListeValide[i]) {
                ctx.listesPretraitees.set(i, moteur.pretraiterBase(ctx.listesControle.get(i)));
              cacheListeValide[i] = true;
            }
        }

      List<Name> clientsPretraites = moteur.pretraiterBase(clients);

        // parallelisation sur les clients
        final Moteur moteurFinal = moteur;
        clientsPretraites.parallelStream().forEach(client -> {
          for (int i = 0; i < ctx.listesPretraitees.size(); i++) {
                if (!ctx.listesActives[i])
                  continue;
                List<Alerte> res = moteurFinal.rechercherPretraite(ctx.listesPretraitees.get(i), ctx.nomsListesControle.get(i), client, earlyExit);
              toutesAlertes.addAll(res);
            }
        });

        double dureeSec = (System.currentTimeMillis() - t0) / 1000.0;

      if (!isBatch) {
            if (toutesAlertes.isEmpty()) {
              System.out.println("\n  Aucune correspondance. Client clean.");
            } else {
              System.out.println();
                for (Alerte a : toutesAlertes) {
                  System.out.println("  ALERTE :");
                    System.out.println("    ID      : " + a.getIdTrouve());
                    System.out.println("    Nom     : " + a.getNomTrouve());
                  System.out.println("    Source  : " + a.getSource());
                    System.out.printf("    Score   : %.0f%%%n", a.getScore() * 100);
                }
              System.out.println("\n  " + toutesAlertes.size() + " correspondance(s)");
            }
            System.out.printf("  Duree : %.3f s%n", dureeSec);
        } else {
          System.out.println();
            if (toutesAlertes.isEmpty()) {
              System.out.println("  Aucune alerte.");
            } else {
              System.out.println("  ALERTES :");
                List<Alerte> tri = new ArrayList<>(toutesAlertes);
                for (int k = 0; k < tri.size(); k++) {
                  Alerte a = tri.get(k);
                    System.out.printf("  %d. [%s] %s <-> [%s] %s (%s) %.0f%%%n", k + 1, a.getIdClient(), a.getNomClient(), a.getIdTrouve(), a.getNomTrouve(), a.getSource(), a.getScore() * 100);
                }
            }
          System.out.println("\n  " + toutesAlertes.size() + " alerte(s) / " + clients.size() + " clients");
            System.out.printf("  Duree : %.1fs%n", dureeSec);
            if (!toutesAlertes.isEmpty()) {
              System.out.print("\n  Exporter CSV ? (chemin ou Entree pour ignorer) : ");
                String chemin = sc.nextLine().trim();
              if (!chemin.isEmpty())
                    ctx.exporteur.exporter(toutesAlertes, chemin);
            }
        }
    }

    private void ajouterListeControle() {
        System.out.println("\n  Ajouter une liste de controle");
      System.out.print("  Chemin du fichier CSV : ");
        String chemin = sc.nextLine().trim();
        if (!validerFormatCSV(chemin)) {
          System.out.println("  Fichier invalide (colonnes 'id' et 'name' requises).");
            return;
        }
      List<Name> liste = CsvManager.chargerCSV(chemin);
        if (liste.isEmpty()) {
          System.out.println("  Fichier introuvable ou vide.");
            return;
        }
        ctx.listesControle.add(liste);
      ctx.nomsListesControle.add(new File(chemin).getName());

        boolean[] ancienActif = ctx.listesActives;
        ctx.listesActives = new boolean[ctx.listesControle.size()];
      if (ancienActif.length > 0)
            System.arraycopy(ancienActif, 0, ctx.listesActives, 0, ancienActif.length);
        ctx.listesActives[ctx.listesActives.length - 1] = true;

      // agrandir le cache, la nouvelle liste sera pretraitee a la prochaine recherche
        boolean[] ancienCache = cacheListeValide;
        cacheListeValide = new boolean[ctx.listesControle.size()];
      System.arraycopy(ancienCache, 0, cacheListeValide, 0, ancienCache.length);

        System.out.println("  Liste ajoutee : " + new File(chemin).getName() + " (" + liste.size() + " enregistrements)");
      System.out.println("  Total listes : " + ctx.listesControle.size());
    }

    private void mettreAJourClients() {
        System.out.println("\n  Mise a jour de la base clients");
      System.out.println("  1. Saisie manuelle");
        System.out.println("  2. Depuis un fichier CSV");
        System.out.print("  Votre choix : ");

      List<Name> nouvelle = new ArrayList<>();
        switch (sc.nextLine().trim()) {
            case "1":
              System.out.print("  Nombre de clients : ");
                int n;
                try {
                  n = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                  System.out.println("  Nombre invalide.");
                    return;
                }
                for (int i = 0; i < n; i++) {
                  System.out.print("  ID client " + (i + 1) + " : ");
                    String id = sc.nextLine().trim();
                    System.out.print("  Nom complet : ");
                  String[] tokens = sc.nextLine().trim().split("\\s+");
                    nouvelle.add(new Name(id, tokens));
                }
              break;
            case "2":
              System.out.print("  Chemin du fichier CSV : ");
                String cheminCsv = sc.nextLine().trim();
                if (!validerFormatCSV(cheminCsv)) {
                  System.out.println("  Fichier invalide.");
                    return;
                }
              nouvelle = CsvManager.chargerCSV(cheminCsv);
                if (nouvelle.isEmpty()) {
                  System.out.println("  Fichier introuvable ou vide.");
                    return;
                }
              System.out.println("  " + nouvelle.size() + " client(s) charges.");
                break;
            default:
              System.out.println("  Choix invalide.");
                return;
        }
      ctx.baseClients = nouvelle;
        System.out.println("  Base clients mise a jour : " + ctx.baseClients.size() + " clients.");
    }

    private void configurerParametres() {
        System.out.println("\n  -- Configuration --");
      System.out.println("  1. Methode de comparaison");
        System.out.println("  2. Seuil de tolerance");
        System.out.println("  3. Pretraitements");
      System.out.println("  4. Listes actives");
        System.out.print("  Votre choix : ");

        switch (sc.nextLine().trim()) {
            case "1":
              for (int i = 0; i < ctx.nomComparateurs.length; i++)
                    System.out.println("  " + (i + 1) + ". " + ctx.nomComparateurs[i] + (ctx.comparateursActifs[i] ? " [X]" : " [ ]"));
                System.out.print("  Numeros a basculer (ex: 1 2) : ");
              for (String s : sc.nextLine().trim().split("\\s+")) {
                    try {
                      int idx = Integer.parseInt(s) - 1;
                        if (idx >= 0 && idx < ctx.comparateursActifs.length)
                          ctx.comparateursActifs[idx] = !ctx.comparateursActifs[idx];
                    } catch (NumberFormatException ignored) {
                    }
                }
              int actifs = 0;
                for (boolean b : ctx.comparateursActifs)
                  if (b)
                        actifs++;
                if (actifs > 1) {
                  System.out.print("  Orchestration (1=MAXIMUM, 2=MOYENNE) : ");
                    ctx.orchestrationComparateur = sc.nextLine().trim().equals("2") ? ComparateurComposite.OrchestrationType.MOYENNE : ComparateurComposite.OrchestrationType.MAXIMUM;
                }
              break;
            case "2":
              System.out.print("  Nouveau seuil [actuel: " + ctx.seuil + "] : ");
                try {
                  double s = Double.parseDouble(sc.nextLine().trim());
                    if (s >= 0 && s <= 1)
                      ctx.seuil = s;
                    else
                      System.out.println("  Valeur hors [0.0 - 1.0].");
                } catch (NumberFormatException e) {
                  System.out.println("  Invalide.");
                }
              break;
            case "3":
              for (int i = 0; i < ctx.nomPretraiteurs.length; i++)
                    System.out.println("  " + (i + 1) + ". " + ctx.nomPretraiteurs[i]);
                System.out.print("  Ordre d'execution (ex: 3 1 2) : ");
              String ordre = sc.nextLine().trim();
                ctx.ordrePretraiteurs.clear();
                if (!ordre.isEmpty())
                  for (String id : ordre.split("\\s+")) {
                        try {
                          int idx = Integer.parseInt(id) - 1;
                            if (idx >= 0 && idx < ctx.nomPretraiteurs.length && !ctx.ordrePretraiteurs.contains(idx))
                              ctx.ordrePretraiteurs.add(idx);
                        } catch (NumberFormatException ignored) {
                        }
                    }
              System.out.println("  Ordre : " + ctx.ordrePretraiteurs);
                break;
            case "4":
              if (ctx.listesControle.isEmpty()) {
                    System.out.println("  Aucune liste.");
                  break;
                }
                for (int i = 0; i < ctx.listesControle.size(); i++)
                  System.out.println("  " + (i + 1) + ". " + ctx.nomsListesControle.get(i) + (ctx.listesActives[i] ? " [X]" : " [ ]"));
                System.out.print("  Numeros a basculer : ");
              for (String s : sc.nextLine().trim().split("\\s+")) {
                    try {
                      int idx = Integer.parseInt(s) - 1;
                        if (idx >= 0 && idx < ctx.listesActives.length)
                          ctx.listesActives[idx] = !ctx.listesActives[idx];
                    } catch (NumberFormatException ignored) {
                    }
                }
              break;
            default:
              System.out.println("  Choix invalide.");
        }
    }

    private boolean validerFormatCSV(String chemin) {
        if (chemin == null || chemin.isEmpty())
          return false;
        if ((chemin.startsWith("'") && chemin.endsWith("'")) || (chemin.startsWith("\"") && chemin.endsWith("\"")))
          chemin = chemin.substring(1, chemin.length() - 1);
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
          String header = br.readLine();
            if (header == null)
              return false;
            String h = header.toLowerCase();
          return h.contains("id") && h.contains("name");
        } catch (IOException e) {
          return false;
        }
    }

    private Moteur construireMoteur(GenerateurCandidat gen) {
        List<Pretraiteur> actifs = new ArrayList<>();
      for (int idx : ctx.ordrePretraiteurs)
            actifs.add(ctx.tousPretraiteurs[idx]);

        List<Comparateur> comps = new ArrayList<>();
      for (int i = 0; i < ctx.comparateursActifs.length; i++)
            if (ctx.comparateursActifs[i])
              comps.add(ctx.tousComparateurs[i]);

        Comparateur finalComp;
        if (comps.size() == 1)
          finalComp = comps.get(0);
        else if (comps.isEmpty())
          finalComp = ctx.tousComparateurs[0];
        else
          finalComp = new ComparateurComposite(comps, ctx.orchestrationComparateur);

        return new Moteur(gen, actifs.toArray(new Pretraiteur[0]), finalComp, new SelectionneurPercentage(ctx.seuil));
    }
}