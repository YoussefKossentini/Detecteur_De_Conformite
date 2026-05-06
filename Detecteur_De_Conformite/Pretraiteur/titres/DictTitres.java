//'l'objectif principal de cette classe est la recherche et la détécte du titre civil a partir du nom entré par l'user


//Plusieurs lignes de ce code sont suggerees par IA (après une très grande discussion) elle sert a : 
//Charge titres.txt en mémoire dans un HashSet
//Normalise les titres (minuscules, apostrophes , espace)
//extraireTitre(phrase) : détecte le titre dans une phrase (Longest Match First)
//contient(titre) : vérifie si un titre existe dans le dictionnaire
//getNbMotsTitre(phrase) : retourne le nombre de mots du titre détecté
//getTailleDictionnaire() / getMaxMotsParTitre() : infos sur le dictionnairee

package pretraiteur.titres;
import java.io.*;
import java.util.*;

public class DictTitres {

    private final Set<String> titres;
    private int maxMotsParTitre;

    // ── Constructeur fichier ───────────────────────────────────────────────
    public DictTitres(String cheminFichier) {
        this.titres = new HashSet<>(15000, 0.5f); // load factor 0.5 → moins de collisions
        chargerDonnees(cheminFichier);
    }

    // ── Constructeur tableau (sans fichier) ───────────────────────────────
    public DictTitres(String[] titresArray) {
        this.titres = new HashSet<>(titresArray.length * 3, 0.5f);
        for (String t : titresArray) {
            String n = normaliser(t);
            if (n != null) {
                titres.add(n);
                int nb = compterMots(n);
                if (nb > maxMotsParTitre) maxMotsParTitre = nb;
            }
        }
    }

    private void chargerDonnees(String chemin) {
        try (BufferedReader br = new BufferedReader(new FileReader(chemin), 65536)) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String n = normaliser(ligne);
                if (n != null) {
                    titres.add(n);
                    int nb = compterMots(n);
                    if (nb > maxMotsParTitre) maxMotsParTitre = nb;
                }
            }
        } catch (IOException e) {
            System.err.println("Avertissement : '" + chemin + "' non trouvé.");
        }
    }

    // ── Normalisation centralisée (apostrophes + lowercase + trim) ─────────
    // Appelée UNE SEULE FOIS au chargement, jamais à la recherche
    private static String normaliser(String s) {
        if (s == null) return null;
        String r = s.trim().toLowerCase()
            .replace('\u2019', ' ')   // '
            .replace('\u0060', ' ')   // `
            .replace('\'',     ' ');  // '
        // Collapse espaces multiples
        r = r.replaceAll("\\s{2,}", " ");
        return r.isEmpty() ? null : r;
    }

    // ── Compte les mots sans split() (évite l'allocation tableau) ─────────
    private static int compterMots(String s) {
        int count = 0;
        boolean inWord = false;
        for (int i = 0; i < s.length(); i++) {
            boolean space = s.charAt(i) == ' ';
            if (!space && !inWord) { count++; inWord = true; }
            else if (space)         { inWord = false; }
        }
        return count;
    }

    /**
    
     * Algorithme LONGEST MATCH FIRST — version optimisée.
     *
     * Optimisations :
     *   1. Normalisation inline (pas d'objet String intermédiaire pour normaliser)
     *   2. Split UNE seule fois → tableau réutilisé
     *   3. StringBuilder réutilisé pour construire les candidats (pas de String.join)
     *   4. Descend de maxWindow → 1 (longest match first)
     *   5. Max 3 positions scannées (titre toujours en tête)
     *   6. Arrêt précoce au premier match
     */
    public String extraireTitre(String phrase) {
        if (phrase == null || phrase.isEmpty()) return null;

        // Normalisation inline (une seule passe char par char)
        char[] buf = new char[phrase.length()];
        int    len = 0;
        boolean prevSpace = false;
        for (int i = 0; i < phrase.length(); i++) {
            char c = phrase.charAt(i);
            // lowercase ASCII fast path
            if (c >= 'A' && c <= 'Z') c = (char)(c + 32);
            // normalise apostrophes → espace
            if (c == '\'' || c == '\u2019' || c == '\u0060') c = ' ';
            // collapse espaces
            if (c == ' ') {
                if (!prevSpace && len > 0) { buf[len++] = ' '; prevSpace = true; }
            } else {
                buf[len++] = c;
                prevSpace = false;
            }
        }
        // trim trailing space
        if (len > 0 && buf[len - 1] == ' ') len--;
        if (len == 0) return null;

        // Découpage en mots (indices dans buf, zéro allocation)
        int[] starts = new int[64];
        int[] ends   = new int[64];
        int   nbMots = 0;
        int   i = 0;
        while (i < len) {
            if (buf[i] != ' ') {
                starts[nbMots] = i;
                while (i < len && buf[i] != ' ') i++;
                ends[nbMots++] = i;
            } else {
                i++;
            }
        }
        if (nbMots == 0) return null;

        int maxWindow = Math.min(maxMotsParTitre, nbMots);
        StringBuilder sb = new StringBuilder(64);

        // LONGEST MATCH FIRST
        for (int taille = maxWindow; taille >= 1; taille--) {
            int maxPos = Math.min(nbMots - taille + 1, 3);
            for (int pos = 0; pos < maxPos; pos++) {
                // Construire le candidat avec StringBuilder (zéro allocation String intermédiaire)
                sb.setLength(0);
                for (int w = pos; w < pos + taille; w++) {
                    if (w > pos) sb.append(' ');
                    sb.append(buf, starts[w], ends[w] - starts[w]);
                }
                if (titres.contains(sb.toString())) {
                    return sb.toString(); // arrêt précoce
                }
            }
        }
        return null;
    }

    // ── API publique ───────────────────────────────────────────────────────
    public boolean contient(String candidat) {
        if (candidat == null) return false;
        String n = normaliser(candidat);
        return n != null && titres.contains(n);
    }

    public int getNbMotsTitre(String phrase) {
        String t = extraireTitre(phrase);
        return t == null ? 0 : compterMots(t);
    }

    public int getMaxMotsParTitre()    { return maxMotsParTitre; }
    public int getTailleDictionnaire() { return titres.size(); }
}
