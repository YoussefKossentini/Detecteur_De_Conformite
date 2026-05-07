package Detecteur_De_Conformite;

import java.util.ArrayList;

public class GestionListe {
    private static GestionListe instance;
    private ArrayList<Name> allListe;

    private GestionListe() {
        this.allListe = new ArrayList<>();
    }

    public static synchronized GestionListe getInstance() {
        if (instance == null) {
            instance = new GestionListe();
        }
        return instance;
    }

    public void addListe(ArrayList<Name> newListe) {
        if (newListe != null) {
            allListe.addAll(newListe);
        }
    }

    public void addName(Name name) {
        if (name != null) {
            allListe.add(name);
        }
    }

    public ArrayList<Name> getAllListe() {
        return new ArrayList<>(allListe);
    }
}
