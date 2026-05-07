package Detecteur_De_Conformite.Selectionneur;

import java.util.ArrayList;
import Detecteur_De_Conformite.Resultat;

public interface Selectionneur {
    ArrayList<Resultat> selectionner(ArrayList<Resultat> resultats);
}
