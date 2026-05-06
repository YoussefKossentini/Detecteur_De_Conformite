import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SelectionneurTop implements Selectionneur {

    private int topN;

    public SelectionneurTop(int topN) {
        this.topN = topN;
    }

    
    public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {

        
        Collections.sort(res, new Comparator<Resultat>() {
            
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