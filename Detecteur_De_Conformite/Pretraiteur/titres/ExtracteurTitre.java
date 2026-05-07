package Detecteur_De_Conformite.Pretraiteur.titres;

public class ExtracteurTitre {

    private final DictTitres dictTitres;

    public ExtracteurTitre(DictTitres dictTitres) {
        this.dictTitres = dictTitres;
    }

    public String supprimerTitre(String nomComplet) {
        if (nomComplet == null || nomComplet.isEmpty()) return nomComplet;
        String titre = dictTitres.extraireTitre(nomComplet);
        if (titre == null) return nomComplet;

        int titreLenNorm = titre.length();
        String normOrig = nomComplet.toLowerCase()
            .replace('\u2019', ' ').replace('\u0060', ' ').replace('\'', ' ')
            .replaceAll("\\s{2,}", " ").trim();

        if (normOrig.startsWith(titre)) {
            int offset = titreLenNorm;
            while (offset < normOrig.length() && normOrig.charAt(offset) == ' ') offset++;
            String reste = normOrig.substring(offset);
            return reste.isEmpty() ? nomComplet : reste;
        }
        return nomComplet;
    }

    public String extraireTitreSeul(String nomComplet) {
        return dictTitres.extraireTitre(nomComplet);
    }
}
