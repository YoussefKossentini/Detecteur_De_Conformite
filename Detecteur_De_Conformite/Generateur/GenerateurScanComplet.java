import java.util.ArrayList;

public class GenerateurScanComplet implements GenerateurCandidat {
    private Name nomOriginal;

    public ArrayList<int[]> genererIndices(ArrayList<Name> listeA, ArrayList<Name> listeB) {
        ArrayList<int[]> intex= new ArrayList<>();
        for (int i=0; i<listeA.size(); i++){
            for (int j=0; j<listeB.size(); j++){
                intex.add(new int[] {i,j});
            }
        }
        return intex;
    }
}