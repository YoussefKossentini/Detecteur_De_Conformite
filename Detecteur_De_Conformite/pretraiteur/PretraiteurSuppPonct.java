package pretraiteur;

import java.util.ArrayList;

public class PretraiteurSuppPonct implements Pretraiteur {
  public ArrayList<String> pretraiter(ArrayList<String> tokens) {
    ArrayList<String> resultat = new ArrayList<>();
    for (String s : tokens) {
      if (s == null) {
        resultat.add("");
        continue;
      }
      resultat.add(s.replaceAll("\\p{Punct}", ""));
    }
    return resultat;
  }
}