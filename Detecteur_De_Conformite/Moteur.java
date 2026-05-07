package Detecteur_De_Conformite;

import java.util.ArrayList;
import java.util.Arrays;
import Detecteur_De_Conformite.Comparateur.Comparateur;
import Detecteur_De_Conformite.Pretraiteur.Pretraiteur;
import Detecteur_De_Conformite.Generateur.GenerateurCandidat;
import Detecteur_De_Conformite.Selectionneur.Selectionneur;

public class Moteur {
    private GenerateurCandidat generateur;
    private Pretraiteur[] pretraiteurs;
    private Comparateur comparateur;
    private Selectionneur selectionneur;

    public Moteur(GenerateurCandidat generateur, Pretraiteur[] pretraiteurs, Comparateur comparateur,
            Selectionneur selectionneur) {
        this.generateur = generateur;
        this.pretraiteurs = pretraiteurs;
        this.comparateur = comparateur;
        this.selectionneur = selectionneur;
    }

    private ArrayList<String> appliquerPretraitement(Name nom) {
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(nom.getNomBrute()));
        if (pretraiteurs != null) {
            for (Pretraiteur p : pretraiteurs) {
                tokens = p.pretraiter(tokens);
            }
        }
        return tokens;
    }

    public ArrayList<Resultat> rechercher(ArrayList<Name> listeCandidat, Name nomRequete) {
        ArrayList<Resultat> resultatsBruts = new ArrayList<>();

        // 1. Prétraitement du nom de requête
        ArrayList<String> tokensRequete = appliquerPretraitement(nomRequete);

        // 2. Génération des candidats (paires d'indices)
        ArrayList<Name> listeRequete = new ArrayList<>();
        listeRequete.add(nomRequete);

        ArrayList<int[]> pairesIndices;
        if (generateur != null) {
            pairesIndices = generateur.genererIndices(listeRequete, listeCandidat);
        } else {
            // Fallback si pas de générateur : scan complet manuel
            pairesIndices = new ArrayList<>();
            for (int j = 0; j < listeCandidat.size(); j++) {
                pairesIndices.add(new int[]{0, j});
            }
        }

        // 3. Comparaison
        for (int[] paire : pairesIndices) {
            // paire[0] est l'index dans listeRequete (toujours 0 ici)
            // paire[1] est l'index dans listeCandidat
            Name candidat = listeCandidat.get(paire[1]);
            
            ArrayList<String> tokensCandidat = appliquerPretraitement(candidat);

            double score = comparateur.comparer(tokensRequete, tokensCandidat);
            resultatsBruts.add(new Resultat(candidat, score));
        }
        
        // 4. Sélection
        if (selectionneur != null) {
            return selectionneur.selectionner(resultatsBruts);
        } else {
            return resultatsBruts;
        }
    }
}
