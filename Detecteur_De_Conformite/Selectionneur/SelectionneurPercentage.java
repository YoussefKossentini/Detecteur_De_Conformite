import java.util.*;

public class SelectionneurPourcentage implements Selectionneur {

    private double seuil; 

    public SelectionneurPourcentage(double seuil) {
        this.seuil = seuil;
    }

    public List<Resultat> selectionner(List<Resultat> resultats) {

        List<Resultat> selection = new ArrayList<>();

        for (Resultat r : resultats) {
            if (r.getScore() >= seuil) {
                selection.add(r);
            }
        }

        return selection;
    }
}