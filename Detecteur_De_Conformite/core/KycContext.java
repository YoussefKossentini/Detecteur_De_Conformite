package core;

import java.util.*;
import model.Name;
import comparateur.*;
import pretraiteur.*;

public class KycContext {
    public List<Name> baseClients = new ArrayList<>();
   public List<List<Name>> listesControle = new ArrayList<>();
     public List<String> nomsListesControle = new ArrayList<>();
    public boolean[] listesActives = new boolean[0];

    // cache des pretraitements
  public List<List<Name>> listesPretraitees = new ArrayList<>();
    public boolean cacheValide = false;

//generee par ia
    // comparateurs disponibles
    public Comparateur[] tousComparateurs = {
       new ComparateurLevenshtein(),
        new ComparateurJaroWinkler(),
      new ComparateurSoundex()
    };

   public String[] nomComparateurs = { "Levenshtein", "Jaro-Winkler", "Soundex" };
     public boolean[] comparateursActifs = { true, false, false };
    public ComparateurComposite.OrchestrationType orchestrationComparateur = ComparateurComposite.OrchestrationType.MAXIMUM;
    public double seuil = 0.85;

  // pretraiteurs disponibles
    public Pretraiteur[] tousPretraiteurs = {
            // new PretraiteurTransliteration(),
          new PretraiteurMinMaj(),
            new PretraiteurSuppAccent(),
          new PretraiteurSuppPonct()
    };

    public String[] nomPretraiteurs = { "MinMaj", "SuppAccent", "SuppPonct" };
  public List<Integer> ordrePretraiteurs = new ArrayList<>(Arrays.asList(0, 1, 2));

    public Exporteur exporteur = new ExporteurCSV();
}