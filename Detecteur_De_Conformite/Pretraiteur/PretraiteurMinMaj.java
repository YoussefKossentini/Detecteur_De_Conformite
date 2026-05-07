package Detecteur_De_Conformite.Pretraiteur;

import java.util.ArrayList;

public class PretraiteurMinMaj implements Pretraiteur {
    @Override
    public ArrayList<String> pretraiter(ArrayList<String> nomPretraite) {
        ArrayList<String> resultat = new ArrayList<>();
        if (nomPretraite == null) return resultat;
        for (String mot : nomPretraite) {
            if (mot != null && !mot.isEmpty()) {
                resultat.add(mot.toLowerCase().trim());
            }
        }
        return resultat;
    }
}
