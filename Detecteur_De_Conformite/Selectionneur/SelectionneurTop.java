package Detecteur_De_Conformite.Selectionneur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import Detecteur_De_Conformite.Resultat;

public class SelectionneurTop implements Selectionneur {

    private int topN;

    public SelectionneurTop(int topN) {
        this.topN = topN;
    }

    @Override
    public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {
        if (res == null) return new ArrayList<>();

        Collections.sort(res, new Comparator<Resultat>() {
            @Override
            public int compare(Resultat r1, Resultat r2) {
                return Double.compare(r2.getScore(), r1.getScore());
            }
        });

        ArrayList<Resultat> selection = new ArrayList<>();
        for (int i = 0; i < res.size() && i < topN; i++) {
            selection.add(res.get(i));
        }

        return selection;
    }
}
