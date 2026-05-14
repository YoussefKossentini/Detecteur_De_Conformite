package selectionneur;
import java.util.ArrayList;
import model.Resultat;
import model.Name;

public class SelectionneurPercentage implements Selectionneur {
   private double threshold;

   public SelectionneurPercentage(double threshold) { this.threshold = threshold; }
//generee par ia
   public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {
     ArrayList<Resultat> filtered = new ArrayList<>();
       for (Resultat r : res) if (r.getScore() >= threshold) filtered.add(r);
       return filtered;
   }

   public static void main(String[] args) {
     SelectionneurPercentage s = new SelectionneurPercentage(0.85);
       ArrayList<Resultat> res = new ArrayList<>();
       res.add(new Resultat(new Name("1", new String[]{"A"}), 0.8));
     res.add(new Resultat(new Name("2", new String[]{"B"}), 0.9));
       System.out.println("Filtered : " + s.selectionner(res).size());
   }
}
