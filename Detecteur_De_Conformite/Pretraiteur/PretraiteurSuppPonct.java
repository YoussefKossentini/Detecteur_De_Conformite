package Detecteur_De_Conformite.Pretraiteur;

import java.util.ArrayList;

public class PretraiteurSuppPonct implements Pretraiteur {

    @Override
    public ArrayList<String> pretraiter(ArrayList<String> nomPretraite) {
        ArrayList<String> result = new ArrayList<>();
        if (nomPretraite == null) return result;
        for (String s : nomPretraite) {
            if (s == null) {
                // Keep null or skip? Previous implementation kept it.
                // But generally we want to skip or handle nulls.
                // Let's stay consistent with what was there but more robust.
                result.add(null);
            } else {
                result.add(s.replaceAll("\\p{Punct}", ""));
            }
        }
        return result;
    }
}
