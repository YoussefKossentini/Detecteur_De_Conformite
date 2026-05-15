package generateur;

import java.util.*;
import model.Name;

public class DemoIndexDouble {

    public static void main(String[] args) {
        ArrayList<Name> listeB = new ArrayList<>();
        listeB.add(new Name("A", new String[]{"Jean", "Dupont"}));
        listeB.add(new Name("B", new String[]{"Pierre"}));
        listeB.add(new Name("C", new String[]{"Paul", "Bernard"}));
        listeB.add(new Name("D", new String[]{"Jean"}));
        listeB.add(new Name("E", new String[]{"Jean", "Dupond"}));

        GenerateurIndexDouble gen = new GenerateurIndexDouble(1, 2);
        gen.construireIndex(listeB);

        System.out.println("Index construit (tokens -> longueur -> indices):");
        Map<Integer, Map<Integer, List<Integer>>> idx = gen.getIndexCacheForDebug();
        for (Integer nbTokens : idx.keySet()) {
            System.out.println(" nbTokens=" + nbTokens);
            Map<Integer, List<Integer>> mapLong = idx.get(nbTokens);
            for (Integer length : mapLong.keySet()) {
                System.out.println("   length=" + length + " -> indices=" + mapLong.get(length));
                for (Integer i : mapLong.get(length)) {
                    Name n = listeB.get(i);
                    System.out.println("     - idx " + i + ": id=" + n.getId() + ", tokens=" + n.getTokens());
                }
            }
        }

        System.out.println("\nDerniereListeB référence == listeB: " + (gen.getDerniereListeBForDebug() == listeB));

        ArrayList<Name> listeA = new ArrayList<>();
        listeA.add(new Name("Q1", new String[]{"jean", "dupont"}));
        listeA.add(new Name("Q2", new String[]{"Paul"}));

        System.out.println("\nGénération des paires pour listeA:");
        ArrayList<ArrayList<Name>> pairs = gen.genererCandidats(listeA, listeB);
        for (ArrayList<Name> p : pairs) {
            System.out.println(" paire: " + p.get(0).getId() + " - " + p.get(1).getId() + "  (tokensA=" + p.get(0).getNumTokens() + ", tokensB=" + p.get(1).getNumTokens() + ", lenA=" + p.get(0).getTotalLength() + ", lenB=" + p.get(1).getTotalLength() + ")");
        }

    }
}
