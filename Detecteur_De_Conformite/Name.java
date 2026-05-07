package Detecteur_De_Conformite;

public class Name {
    private String id;
    private String[] nomBrute;

    public Name(String id, String[] nomBrute) {
        this.id = id;
        this.nomBrute = nomBrute;
    }

    public String[] getNomBrute() {
        return this.nomBrute;
    }

    public String getId() {
        return this.id;
    }
}
