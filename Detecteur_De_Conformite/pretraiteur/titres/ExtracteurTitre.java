package pretraiteur.titres;

import pretraiteur.Pretraiteur;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
BufferedReader permet de lire le fichier ligne par ligne.
C'est plus rapide et plus simple pour parcourir un fichier texte.
*/

public class ExtracteurTitre implements Pretraiteur {
  private final Set<String> titres = new HashSet<>();

  public ExtracteurTitre(String cheminFichier) throws Exception {
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
      if (nom == null || nom.isEmpty()) {
        resultat.add(nom);
        continue;
      }
      String[] mots = nom.toLowerCase().split(" ");
      String nomSansTitre = supprimerTitre(mots);
      resultat.add(nomSansTitre);
    }
    return resultat;
  }

  // Longest Match First : essaie du titre le plus long au plus court
  private String supprimerTitre(String[] mots) {
    for (int taille = mots.length; taille >= 1; taille--) {
      StringBuilder candidat = new StringBuilder();
      for (int i = 0; i < taille; i++) {
        if (i > 0)
          candidat.append(' ');
        candidat.append(mots[i]);
      }
      if (titres.contains(candidat.toString())) {
        StringBuilder reste = new StringBuilder();
        for (int i = taille; i < mots.length; i++) {
          if (i > taille)
            reste.append(' ');
          reste.append(mots[i]);
        }
        return reste.toString();
      }
    }
    return String.join(" ", mots);
  }
}