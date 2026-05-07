package Detecteur_De_Conformite.Generateur;

import java.util.ArrayList;
import Detecteur_De_Conformite.Name;

public interface GenerateurCandidat {
    public abstract ArrayList<int[]> genererIndices(ArrayList<Name> listeA, ArrayList<Name> listeB);
}
