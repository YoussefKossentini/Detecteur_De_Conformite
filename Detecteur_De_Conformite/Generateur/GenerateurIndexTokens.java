package generateur;

import java.util.*;
import model.Name;

public class GenerateurIndexTokens implements GenerateurCandidat {
  private final int nb;

  public GenerateurIndexTokens(int nb) {
    this.nb = (nb < 0) ? 0 : nb;
  }

  public ArrayList<ArrayList<Name>> genererCandidats(ArrayList<Name> a, ArrayList<Name> b) {
    Map<Integer, List<Integer>> idx = new HashMap<>();
    for (int j = 0; j < b.size(); j++) {
      int nbTokens = b.get(j).getNumTokens();
      if (!idx.containsKey(nbTokens)) {
        idx.put(nbTokens, new ArrayList<>());
      }
      idx.get(nbTokens).add(j);
    }

    ArrayList<ArrayList<Name>> couples = new ArrayList<>();
    for (Name na : a) {
      int nbA = na.getNumTokens();
      for (int d = -nb; d <= nb; d++) {
        List<Integer> js = idx.get(nbA + d);
        if (js == null)
          continue;
        for (int j : js) {
          ArrayList<Name> paire = new ArrayList<>(2);
          paire.add(na);
          paire.add(b.get(j));
          couples.add(paire);
        }
      }
    }
    return couples;
  }

  // generee par IA
  public static void main(String[] args) {
    GenerateurIndexTokens gen = new GenerateurIndexTokens(1);
    ArrayList<Name> a = new ArrayList<>();
    a.add(new Name("1", new String[] { "Jean", "Dupont" }));
    ArrayList<Name> b = new ArrayList<>();
    b.add(new Name("2", new String[] { "Jean", "Dupont" }));
    System.out.println("Couples : " + gen.genererCandidats(a, b).size());
  }
}