package Detecteur_De_Conformite.Generateur;

import java.util.ArrayList;
import Detecteur_De_Conformite.Name;

public class GenerateurScanComplet implements GenerateurCandidat {

    @Override
    public ArrayList<int[]> genererIndices(ArrayList<Name> listeA, ArrayList<Name> listeB) {
        ArrayList<int[]> indices = new ArrayList<>();
        if (listeA == null || listeB == null) return indices;
        for (int i = 0; i < listeA.size(); i++) {
            for (int j = 0; j < listeB.size(); j++) {
                indices.add(new int[]{i, j});
            }
        }
        return indices;
    }
}
