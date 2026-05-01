public class Resultat {
    private double score;
    private Name candidat;

<<<<<<< HEAD
    public double getScore() {
        return this.score;
    }
}
=======
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
>>>>>>> 628594f (feat: ajout projet minijava)
