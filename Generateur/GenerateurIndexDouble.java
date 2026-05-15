package generateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Name;

public class GenerateurIndexDouble implements GenerateurCandidat {
  private final int nbPermisTokens;
  private final int longPermis;

  private Map<Integer, Map<Integer, List<Integer>>> indexCache = null;
  private List<Name> derniereListeB = null;

  public GenerateurIndexDouble(int nbPermisTokens, int longPermis) {
    this.nbPermisTokens = nbPermisTokens;
    this.longPermis = longPermis;
  }

  public void construireIndex(ArrayList<Name> listeB) {
    Map<Integer, Map<Integer, List<Integer>>> nouvelIndex = new HashMap<>();
    for (int j = 0; j < listeB.size(); j++) {
      Name b = listeB.get(j);
      int nbTokensB = b.getNumTokens();
      int longueurB = b.getTotalLength();

      if (!nouvelIndex.containsKey(nbTokensB)) {
        nouvelIndex.put(nbTokensB, new HashMap<>());
      }
      Map<Integer, List<Integer>> mapLong = nouvelIndex.get(nbTokensB);

      if (!mapLong.containsKey(longueurB)) {
        mapLong.put(longueurB, new ArrayList<>());
      }
      mapLong.get(longueurB).add(j);
    }
    this.indexCache = nouvelIndex;
    this.derniereListeB = listeB;
  }

  public ArrayList<ArrayList<Name>> genererCandidats(ArrayList<Name> listeA, ArrayList<Name> listeB) {
    if (listeB != derniereListeB || indexCache == null) {
      construireIndex(listeB);
    }

    ArrayList<ArrayList<Name>> couples = new ArrayList<>();
    for (Name a : listeA) {
      int nbTokensA = a.getNumTokens();
      int longueurA = a.getTotalLength();

      for (int dt = -nbPermisTokens; dt <= nbPermisTokens; dt++) {
        Map<Integer, List<Integer>> mapLong = indexCache.get(nbTokensA + dt);
        if (mapLong == null)
          continue;

        for (int dl = -longPermis; dl <= longPermis; dl++) {
          List<Integer> js = mapLong.get(longueurA + dl);
          if (js == null)
            continue;
          for (int j : js) {
            ArrayList<Name> paire = new ArrayList<>(2);
            paire.add(a);
            paire.add(listeB.get(j));
            couples.add(paire);
          }
        }
      }
    }
    return couples;
  }

  // generee par ia
  public static void main(String[] args) {
    GenerateurIndexDouble gen = new GenerateurIndexDouble(1, 2);
    ArrayList<Name> l1 = new ArrayList<>();
    l1.add(new Name("1", new String[] { "Jean", "Dupont" }));
    ArrayList<Name> l2 = new ArrayList<>();
    l2.add(new Name("A", new String[] { "Jean", "Dupont" }));
    l2.add(new Name("B", new String[] { "Pierre", "Moreau" }));
    ArrayList<ArrayList<Name>> res = gen.genererCandidats(l1, l2);
    for (ArrayList<Name> p : res) {
      System.out.println("Paire: " + p.get(0).getId() + " - " + p.get(1).getId());
    }
  }
}