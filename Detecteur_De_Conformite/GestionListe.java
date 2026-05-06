import java.util.ArrayList;

public class GestionListe {
    private static ArrayList<Name> allListe = new ArrayList<Name>();

    GestionListe(ArrayList<Name> allListe) {
        this.allListe = allListe;
    }

    public static void addListe(ArrayList<Name> newListe) {
        allListe.addAll(newListe);
    }
}