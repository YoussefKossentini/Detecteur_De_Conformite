package core;

import java.io.*;
import java.util.*;
import model.Name;

public class CsvManager {
   public static List<Name> chargerCSV(String chemin) {
       if (chemin == null) {
         return new ArrayList<>();
       }

     chemin = chemin.trim();
       if ((chemin.startsWith("'") && chemin.endsWith("'")) || (chemin.startsWith("\"") && chemin.endsWith("\""))) {
         chemin = chemin.substring(1, chemin.length() - 1);
       }

       List<Name> liste = new ArrayList<>();
       try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
         String ligne = br.readLine();
         if (ligne != null && !ligne.toLowerCase().startsWith("id")) {
             System.out.println("  Attention : le fichier CSV ne commence pas par 'id,name'.");
         }

         while ((ligne = br.readLine()) != null) {
             String[] parts = ligne.split(",", 2);
               if (parts.length < 2) {
                 continue;
               }
             String id = parts[0].trim();
             String[] tokens = parts[1].trim().split("\\s+");
             liste.add(new Name(id, tokens));
         }
       } catch (IOException e) {
         System.out.println("  Erreur lecture : " + e.getMessage());
       }

       return liste;
   }
//generee par ia
   public static void main(String[] args) {
       System.out.println("Test chargement CSV...");
     List<Name> res = chargerCSV("test.csv");
       System.out.println("Taille: " + res.size());
   }
}