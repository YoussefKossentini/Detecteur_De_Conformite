package pretraiteur;

import java.util.ArrayList;

public class PretraiteurMinMaj implements Pretraiteur {
  public ArrayList<String> pretraiter(ArrayList<String> tokens) {
    ArrayList<String> resultat = new ArrayList<>();
    for (String m : tokens) {
      if (m == null) {
        resultat.add("");
        continue;
      }
      resultat.add(m.toLowerCase().trim());
    }
    return resultat;
  }
}