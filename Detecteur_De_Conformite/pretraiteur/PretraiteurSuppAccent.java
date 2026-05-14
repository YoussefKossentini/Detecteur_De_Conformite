package pretraiteur;

import java.text.Normalizer;
import java.util.ArrayList;

public class PretraiteurSuppAccent implements Pretraiteur {
  public ArrayList<String> pretraiter(ArrayList<String> tokens) {
    ArrayList<String> resultat = new ArrayList<>();
    for (String mot : tokens) {
      if (mot == null) {
        resultat.add("");
        continue;
      }
      String decompose = Normalizer.normalize(mot, Normalizer.Form.NFD);
      resultat.add(decompose.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
    }
    return resultat;
  }
}