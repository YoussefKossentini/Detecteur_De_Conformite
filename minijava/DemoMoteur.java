import java.util.ArrayList;
import java.util.Arrays;
import Comparateur.*;
import pretraiteur.Pretraiteur;
import pretraiteur.MinMaj.PretraiteurMinMaj;
import Pretraiteur.SuppPonct.PretraiteurSuppPonct;

public class DemoMoteur {
    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                DÉMO COMPLÈTE DU MOTEUR DE RECHERCHE                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

        // 1. Création des composants
        Pretraiteur[] chainePretraitement = {
            new PretraiteurMinMaj(),
            new PretraiteurSuppPonct()
        };
        
        Comparateur comp = new ComparateurLevenshtein();
        
        // On crée le moteur (avec un générateur et sélectionneur null pour cette démo simplifiée)
        Moteur moteur = new Moteur(null, chainePretraitement, comp, null);

        // 2. Création d'une base de données de noms (Candidats)
        ArrayList<Name> baseDeDonnees = new ArrayList<>();
        baseDeDonnees.add(new Name("ID1", new String[]{"Jean", "Dupont"}));
        baseDeDonnees.add(new Name("ID2", new String[]{"Marie", "Curie"}));
        baseDeDonnees.add(new Name("ID3", new String[]{"Ahmed", "Kassab"}));
        baseDeDonnees.add(new Name("ID4", new String[]{"Pierre", "Martin"}));

        // 3. Test de recherche
        // On simule une entrée utilisateur "sale" (majuscules, ponctuation)
        Name requete = new Name("REQ", new String[]{"ahmed,", "KASSAB!"});
        
        System.out.println("  REQUÊTE UTILISATEUR : \"ahmed, KASSAB!\"");
        System.out.println("  ──────────────────────────────────────");

        long t0 = System.nanoTime();
        ArrayList<Name> resultats = moteur.rechercher(baseDeDonnees, requete);
        long dt = System.nanoTime() - t0;

        // 4. Affichage des résultats
        if (resultats.isEmpty()) {
            System.out.println("  ✗ Aucun résultat trouvé.");
        } else {
            System.out.println("  ✓ Résultats trouvés :");
            for (Name n : resultats) {
                System.out.println("    - [" + n.getId() + "] " + String.join(" ", n.getNomBrute()));
            }
        }

        System.out.println("\n  Temps de recherche : " + (dt / 1000.0) + " µs");
        System.out.println("══════════════════════════════════════════════════════════════════════\n");
    }
}
