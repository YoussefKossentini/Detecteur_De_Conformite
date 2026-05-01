import java.util.ArrayList;
<<<<<<< HEAD
=======
import Comparateur.Comparateur;
import pretraiteur.Pretraiteur;
>>>>>>> 628594f (feat: ajout projet minijava)

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

    public ArrayList<Name> rechercher(ArrayList<Name> listeCandidat, Name nom) {
<<<<<<< HEAD
    }

=======
        ArrayList<Name> resultats = new ArrayList<>();
        
        // Prétraitement du nom recherché
        ArrayList<String> nomRecherche = new ArrayList<>();
        for (String s : nom.getNomBrute()) {
            nomRecherche.add(s);
        }
        
        if (pretraiteurs != null) {
            for (Pretraiteur p : pretraiteurs) {
                nomRecherche = p.pretraiter(nomRecherche);
            }
        }

        // Comparaison avec chaque candidat
        for (Name candidat : listeCandidat) {
            ArrayList<String> nomCandidat = new ArrayList<>();
            for (String s : candidat.getNomBrute()) {
                nomCandidat.add(s);
            }
            
            if (pretraiteurs != null) {
                for (Pretraiteur p : pretraiteurs) {
                    nomCandidat = p.pretraiter(nomCandidat);
                }
            }

            if (comparateur.comparer(nomRecherche, nomCandidat)) {
                resultats.add(candidat);
            }
        }
        
        return resultats;
    }
>>>>>>> 628594f (feat: ajout projet minijava)
}