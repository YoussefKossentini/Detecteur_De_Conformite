package Detecteur_De_Conformite.Pretraiteur.titres;

import java.io.*;
import java.util.*;

public class DictTitres {

    private final Set<String> titres;
    private int maxMotsParTitre;

    public DictTitres(String cheminFichier) {
        this.titres = new HashSet<>(15000, 0.5f);
        chargerDonnees(cheminFichier);
    }

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

    private static String normaliser(String s) {
        if (s == null) return null;
        String r = s.trim().toLowerCase()
            .replace('\u2019', ' ')
            .replace('\u0060', ' ')
            .replace('\'',     ' ');
        r = r.replaceAll("\\s{2,}", " ");
        return r.isEmpty() ? null : r;
    }

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

    public String extraireTitre(String phrase) {
        if (phrase == null || phrase.isEmpty()) return null;

        char[] buf = new char[phrase.length()];
        int    len = 0;
        boolean prevSpace = false;
        for (int i = 0; i < phrase.length(); i++) {
            char c = phrase.charAt(i);
            if (c >= 'A' && c <= 'Z') c = (char)(c + 32);
            if (c == '\'' || c == '\u2019' || c == '\u0060') c = ' ';
            if (c == ' ') {
                if (!prevSpace && len > 0) { buf[len++] = ' '; prevSpace = true; }
            } else {
                buf[len++] = c;
                prevSpace = false;
            }
        }
        if (len > 0 && buf[len - 1] == ' ') len--;
        if (len == 0) return null;

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

        for (int taille = maxWindow; taille >= 1; taille--) {
            int maxPos = Math.min(nbMots - taille + 1, 3);
            for (int pos = 0; pos < maxPos; pos++) {
                sb.setLength(0);
                for (int w = pos; w < pos + taille; w++) {
                    if (w > pos) sb.append(' ');
                    sb.append(buf, starts[w], ends[w] - starts[w]);
                }
                if (titres.contains(sb.toString())) {
                    return sb.toString();
                }
            }
        }
        return null;
    }

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
