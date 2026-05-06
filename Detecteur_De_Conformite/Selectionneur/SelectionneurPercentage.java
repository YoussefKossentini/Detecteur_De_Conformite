import java.util.ArrayList;

public class SelectionneurPourcentage implements Selectionneur {

    private double seuil;

    public SelectionneurPourcentage(double seuil) {
        this.seuil = seuil;
    }

    public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {

        ArrayList<Resultat> selection = new ArrayList<>();

        for (Resultat r : res) {
            if (r.getScore() >= seuil) {
                selection.add(r);
            }
        }

        return selection;
    }
}