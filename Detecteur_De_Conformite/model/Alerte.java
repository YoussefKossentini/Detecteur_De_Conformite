package model;

public class Alerte {
  private String idClient;
  private String nomClient;
  private String idTrouve;
  private String nomTrouve;
  private String source;
  private double score;

  public Alerte(String idClient, String nomClient, String idTrouve, String nomTrouve, String source, double score) {
    this.idClient = idClient;
    this.nomClient = nomClient;
    this.idTrouve = idTrouve;
    this.nomTrouve = nomTrouve;
    this.source = source;
    this.score = score;
  }

  public String getIdClient() {
    return idClient;
  }

  public String getNomClient() {
    return nomClient;
  }

  public String getIdTrouve() {
    return idTrouve;
  }

  public String getNomTrouve() {
    return nomTrouve;
  }

  public String getSource() {
    return source;
  }

  public double getScore() {
    return score;
  }
}
