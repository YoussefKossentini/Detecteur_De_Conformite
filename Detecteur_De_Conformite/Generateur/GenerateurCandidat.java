package generateur;
import java.util.ArrayList;
import model.Name;

public interface GenerateurCandidat {
    ArrayList<ArrayList<Name>> genererCandidats(ArrayList<Name> listeA, ArrayList<Name> listeB);
}