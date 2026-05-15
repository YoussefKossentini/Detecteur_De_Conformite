package generateur;

import java.util.ArrayList;
import model.Name;

public class GenerateurScanComplet implements GenerateurCandidat {

  public ArrayList<ArrayList<Name>> genererCandidats(ArrayList<Name> listeA, ArrayList<Name> listeB) {
    ArrayList<ArrayList<Name>> couples = new ArrayList<>(listeA.size() * listeB.size());
    for (Name a : listeA) {
      for (Name b : listeB) {
        ArrayList<Name> paire = new ArrayList<>(2);
        paire.add(a);
        paire.add(b);
        couples.add(paire);
      }
    }
    return couples;
  }
}