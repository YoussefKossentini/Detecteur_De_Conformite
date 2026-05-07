package Detecteur_De_Conformite;

public class Resultat {
    private double score;
    private Name candidat;

    public Resultat(Name candidat, double score) {
        this.candidat = candidat;
        this.score = score;
    }

    public double getScore() {
        return this.score;
    }

    public Name getCandidat() {
        return this.candidat;
    }
}
