import java.util.ArrayList;

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
    }

}