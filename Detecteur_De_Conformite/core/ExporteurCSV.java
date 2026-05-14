package core;
import java.util.List;
import java.io.*;
import model.Alerte;

public class ExporteurCSV implements Exporteur {
//generee par ia
   public void exporter(List<Alerte> alertes, String chemin) {
       chemin = chemin.trim();
     if ((chemin.startsWith("'") && chemin.endsWith("'")) || (chemin.startsWith("\"") && chemin.endsWith("\""))) {
           chemin = chemin.substring(1, chemin.length() - 1);
     }

       try (PrintWriter pw = new PrintWriter(new FileWriter(chemin))) {
         pw.println("id_client,nom_client,id_trouve,nom_trouve,source,score");
           for (Alerte a : alertes) {
             String ligne = a.getIdClient() + "," + a.getNomClient() + "," + a.getIdTrouve() + "," + a.getNomTrouve() + "," + a.getSource() + "," + String.format("%.3f", a.getScore());
             pw.println(ligne);
           }
         System.out.println("  Rapport exporté : " + chemin);
       } catch (IOException e) {
           System.out.println("  Erreur export : " + e.getMessage());
       }
   }
}