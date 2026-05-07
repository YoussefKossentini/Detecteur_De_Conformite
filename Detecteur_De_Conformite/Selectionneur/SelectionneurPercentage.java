package Detecteur_De_Conformite.Selectionneur;

import java.util.ArrayList;
import Detecteur_De_Conformite.Resultat;

public class SelectionneurPercentage implements Selectionneur {

    private double seuil;

    public SelectionneurPercentage(double seuil) {
        this.seuil = seuil;
    }

    @Override
    public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {
        ArrayList<Resultat> selection = new ArrayList<>();
        if (res == null) return selection;
        for (Resultat r : res) {
            if (r.getScore() >= seuil) {
                selection.add(r);
            }
        }
        return selection;
    }
}
