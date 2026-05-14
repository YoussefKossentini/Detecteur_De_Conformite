package comparateur;

import java.util.ArrayList;
import java.util.List;

public class ComparateurComposite implements Comparateur {

  public enum OrchestrationType {
      MAXIMUM, MOYENNE
  }

    private List<Comparateur> comparateurs;
    private OrchestrationType type;

    public ComparateurComposite(List<Comparateur> comparateurs, OrchestrationType type) {
      this.comparateurs = comparateurs;
      this.type = type;
    }

    public double comparer(List<String> s1, List<String> s2) {
        if (comparateurs.isEmpty()) {
          return 0.0;
        }

        double result = 0.0;

        if (type == OrchestrationType.MAXIMUM) {
            double max = 0.0;
            for (Comparateur c : comparateurs) {
              double score = c.comparer(s1, s2);
              if (score > max) {
                  max = score;
              }
            }
            result = max;

        } else if (type == OrchestrationType.MOYENNE) {
          double sum = 0.0;
          for (Comparateur c : comparateurs) {
              sum += c.comparer(s1, s2);
          }
          result = sum / comparateurs.size();
        }

        return result;
    }
}