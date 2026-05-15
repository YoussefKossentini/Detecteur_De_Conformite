package selectionneur;
import java.util.ArrayList;
import model.Resultat;
import model.Name;

public class SelectionneurTop implements Selectionneur {
   public ArrayList<Resultat> selectionner(ArrayList<Resultat> res) {
     if (res.isEmpty()) return res;
       ArrayList<Resultat> filtered = new ArrayList<>();
     double max = -1.0;
       for (Resultat r : res) if (r.getScore() > max) max = r.getScore();
       for (Resultat r : res) if (r.getScore() == max) filtered.add(r);
     return filtered;
   }

   public static void main(String[] args) {
     SelectionneurTop s = new SelectionneurTop();
       ArrayList<Resultat> res = new ArrayList<>();
       res.add(new Resultat(new Name("1", new String[]{"A"}), 0.8));
     res.add(new Resultat(new Name("2", new String[]{"B"}), 0.9));
       System.out.println("Top : " + s.selectionner(res).size());
   }
}
