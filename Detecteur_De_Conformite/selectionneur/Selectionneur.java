package selectionneur;
import java.util.ArrayList;
import model.Resultat;

public interface Selectionneur {
    ArrayList<Resultat> selectionner(ArrayList<Resultat> res);
}
