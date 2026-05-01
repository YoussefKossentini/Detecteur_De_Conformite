import java.util.*;

<<<<<<< HEAD
public class SelectionneurTop implements SelectionneurTop {

}
=======
public class SelectionneurTop implements Selectionneur {

    @Override
    public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {
        // Trier par score décroissant
        Collections.sort(res, new Comparator<Resultat>() {
            @Override
            public int compare(Resultat r1, Resultat r2) {
                return Double.compare(r2.getScore(), r1.getScore());
            }
        });
        return res;
    }
}
>>>>>>> 628594f (feat: ajout projet minijava)
