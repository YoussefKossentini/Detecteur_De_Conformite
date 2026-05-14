package pretraiteur.titres;

import pretraiteur.Pretraiteur;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
BufferedReader permet de lire le fichier ligne par ligne.
C’est plus rapide et plus simple pour parcourir un fichier texte.
*/

public class DictTitres implements Pretraiteur {
  private final Set<String> titres = new HashSet<>();

  public DictTitres(String cheminFichier) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(cheminFichier));
    String ligne;
    while ((ligne = br.readLine()) != null) {
      ligne = ligne.trim().toLowerCase();
      if (!ligne.isEmpty()) {
        titres.add(ligne);
      }
    }
    br.close();
  }

  public ArrayList<String> pretraiter(ArrayList<String> tokens) {
    ArrayList<String> resultat = new ArrayList<>();
    for (String nom : tokens) {
      String[] mots = nom.toLowerCase().split(" ");
      if (mots.length > 0 && titres.contains(mots[0])) {
        resultat.add(mots[0]);
      } else {
        resultat.add(nom);
      }
    }

    return resultat;
  }
}