package Detecteur_De_Conformite.Pretraiteur;

import java.text.Normalizer;
import java.util.ArrayList;

public class PretraiteurSuppAccent implements Pretraiteur {

    public String pretraiter(String s) {
        if (s == null) return null;
        String normalisee = Normalizer.normalize(s, Normalizer.Form.NFD);
        String sansAccent = normalisee.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sansAccent;
    }

    @Override
    public ArrayList<String> pretraiter(ArrayList<String> nomPretraite) {
        ArrayList<String> resultat = new ArrayList<String>();
        if (nomPretraite == null) return resultat;
        for (String mot : nomPretraite) {
            resultat.add(pretraiter(mot));
        }
        return resultat;
    }
}
