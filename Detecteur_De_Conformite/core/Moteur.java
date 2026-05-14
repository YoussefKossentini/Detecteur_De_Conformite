package core;

import java.util.*;
import model.*;
import pretraiteur.*;
import comparateur.*;
import generateur.*;
import selectionneur.*;

public class Moteur {
   private GenerateurCandidat generateur;
  private Pretraiteur[] pretraiteurs;
   private Comparateur comparateur;
  private Selectionneur selectionneur;

    public Moteur(GenerateurCandidat generateur, Pretraiteur[] pretraiteurs, Comparateur comparateur, Selectionneur selectionneur) {
      this.generateur = generateur;
        this.pretraiteurs = pretraiteurs;
     this.comparateur = comparateur;
       this.selectionneur = selectionneur;
   }

//generee par ia
   public List<Alerte> rechercherPretraite(List<Name> listeControle, String source, Name requete) {
     return rechercherPretraite(listeControle, source, requete, false);
   }

  public List<Alerte> rechercherPretraite(List<Name> listeControle, String source, Name requete, boolean earlyExit) {
       ArrayList<Name> requeteList = new ArrayList<>(1);
        requeteList.add(requete);

     ArrayList<Name> listeControleArray = (listeControle instanceof ArrayList) ? (ArrayList<Name>) listeControle : new ArrayList<>(listeControle);

       ArrayList<ArrayList<Name>> paires = generateur.genererCandidats(requeteList, listeControleArray);

        // Calculé une seule fois, hors de la boucle
        List<String> nomRequete = requete.getTokens();

        ArrayList<Resultat> resultats = new ArrayList<>(paires.size());
       for (ArrayList<Name> paire : paires) {
         Name candidat = paire.get(1);
           double score = comparateur.comparer(nomRequete, candidat.getTokens());
         resultats.add(new Resultat(candidat, score));

           if (earlyExit && score >= 0.99) {
             ArrayList<Resultat> seul = new ArrayList<>(1);
              seul.add(new Resultat(candidat, score));
               resultats = seul;
                break;
           }
       }

     ArrayList<Resultat> filtres = selectionneur.selectionner(resultats);
       List<Alerte> alertes = new ArrayList<>(filtres.size());
       String nomCliStr = String.join(" ", requete.getNomBrute());
     for (Resultat r : filtres) {
           String nomTrStr = String.join(" ", r.getCandidat().getNomBrute());
           alertes.add(new Alerte(requete.getId(), nomCliStr, r.getCandidat().getId(), nomTrStr, source, r.getScore()));
       }
       return alertes;
   }

    public List<Name> pretraiterBase(List<Name> base) {
     if (pretraiteurs.length == 0)
           return base;

       List<Name> resultat = new ArrayList<>(base.size());
     for (Name n : base) {
           ArrayList<String> tokens = new ArrayList<>(Arrays.asList(n.getNomBrute()));
           for (Pretraiteur p : pretraiteurs)
             tokens = p.pretraiter(tokens);
         resultat.add(new Name(n.getId(), tokens.toArray(new String[0])));
       }
       return resultat;
   }

  public Name pretraiterNom(Name n) {
       ArrayList<String> tokens = new ArrayList<>(Arrays.asList(n.getNomBrute()));
     for (Pretraiteur p : pretraiteurs)
           tokens = p.pretraiter(tokens);
       return new Name(n.getId(), tokens.toArray(new String[0]));
   }
}