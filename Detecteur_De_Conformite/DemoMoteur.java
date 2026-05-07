package Detecteur_De_Conformite;

import java.util.ArrayList;
import java.util.Arrays;
import Detecteur_De_Conformite.Comparateur.*;
import Detecteur_De_Conformite.Pretraiteur.*;
import Detecteur_De_Conformite.Generateur.*;
import Detecteur_De_Conformite.Selectionneur.*;

public class DemoMoteur {
    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                DÉMO COMPLÈTE DU MOTEUR DE RECHERCHE                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

        // 1. Création des composants
        Pretraiteur[] chainePretraitement = {
            new PretraiteurMinMaj(),
            new PretraiteurSuppPonct(),
            new PretraiteurSuppAccent()
        };
        
        Comparateur comp = new ComparateurLevenshtein();
        GenerateurCandidat gen = new GenerateurScanComplet();
        Selectionneur sel = new SelectionneurTop(3);
        
        Moteur moteur = new Moteur(gen, chainePretraitement, comp, sel);

        // 2. Création d'une base de données de noms (Candidats)
        GestionListe gestionnaire = GestionListe.getInstance();
        gestionnaire.addName(new Name("ID1", new String[]{"Jean", "Dupont"}));
        gestionnaire.addName(new Name("ID2", new String[]{"Marie", "Curie"}));
        gestionnaire.addName(new Name("ID3", new String[]{"Ahmed", "Kassab"}));
        gestionnaire.addName(new Name("ID4", new String[]{"Pierre", "Martin"}));
        gestionnaire.addName(new Name("ID5", new String[]{"Ahméd", "Kassab"})); // Variante avec accent

        ArrayList<Name> baseDeDonnees = gestionnaire.getAllListe();

        // 3. Test de recherche
        // On simule une entrée utilisateur "sale" (majuscules, ponctuation, accents)
        Name requete = new Name("REQ", new String[]{"ahmed,", "KASSAB!"});
        
        System.out.println("  REQUÊTE UTILISATEUR : \"ahmed, KASSAB!\"");
        System.out.println("  ──────────────────────────────────────");

        long t0 = System.nanoTime();
        ArrayList<Resultat> resultats = moteur.rechercher(baseDeDonnees, requete);
        long dt = System.nanoTime() - t0;

        // 4. Affichage des résultats
        if (resultats.isEmpty()) {
            System.out.println("  ✗ Aucun résultat trouvé.");
        } else {
            System.out.println("  ✓ Résultats trouvés :");
            for (Resultat r : resultats) {
                Name n = r.getCandidat();
                System.out.printf("    - [%s] %-20s (Score: %.4f)%n",
                    n.getId(), String.join(" ", n.getNomBrute()), r.getScore());
            }
        }

        System.out.println("\n  Temps de recherche : " + (dt / 1000.0) + " µs");
        System.out.println("══════════════════════════════════════════════════════════════════════\n");
    }
}
