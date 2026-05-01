public class Name {
    private String id;
    private String[] nomBrute;

    Name(String id, String[] nomBrute) {
        this.id = id;
        this.nomBrute = nomBrute;
    }

    String[] getNomBrute() {
        return this.nomBrute;
    }

    public String getId() {
        return this.id;
    }
}