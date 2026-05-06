import java.util.*;

public class SelectionneurTop implements Selectionneur {

    private int topN;

    public SelectionneurTop(int topN) {
        this.topN = topN;
    }

    public List<Resultat> selectionner(List<Resultat> resultats) {

        resultats.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));

        if (resultats.size() <= topN) {
            return resultats;
        }

        return resultats.subList(0, topN);
    }
}