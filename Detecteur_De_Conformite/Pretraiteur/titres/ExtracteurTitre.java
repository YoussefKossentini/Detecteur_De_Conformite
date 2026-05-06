//l'objectif principal de cette classe est de nettoyer le nom entré par l'user en supprimant son titre civil

package pretraiteur.titres;
public class ExtracteurTitre {

    private final DictTitres dictTitres;

    public ExtracteurTitre(DictTitres dictTitres) {
        this.dictTitres = dictTitres;
    }

    /**
     * Supprime le titre du nom complet.
     * Retourne le nom original si aucun titre détecté.
     */
    public String supprimerTitre(String nomComplet) {
        if (nomComplet == null || nomComplet.isEmpty()) return nomComplet;
        String titre = dictTitres.extraireTitre(nomComplet);
        if (titre == null) return nomComplet;

        // Suppression sans regex : avance de (longueur titre + 1 espace)
        // On travaille sur la version normalisée pour trouver l'offset
        int titreLenNorm = titre.length();
        // Chercher la fin du titre dans la chaîne originale normalisée
        String normOrig = nomComplet.toLowerCase()
            .replace('\u2019', ' ').replace('\u0060', ' ').replace('\'', ' ')
            .replaceAll("\\s{2,}", " ").trim();

        if (normOrig.startsWith(titre)) {
            int offset = titreLenNorm;
            // Sauter les espaces après le titre
            while (offset < normOrig.length() && normOrig.charAt(offset) == ' ') offset++;
            String reste = normOrig.substring(offset);
            return reste.isEmpty() ? nomComplet : reste;
        }
        return nomComplet;
    }

    /** Retourne uniquement le titre détecté, ou null. */
    public String extraireTitreSeul(String nomComplet) {
        return dictTitres.extraireTitre(nomComplet);
    }
}
