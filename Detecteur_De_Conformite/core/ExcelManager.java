/*
 * package core;
 * 
 * import model.Name;
 * import org.apache.poi.ss.usermodel.*;
 * import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 * 
 * import java.io.FileInputStream;
 * import java.io.IOException;
 * import java.util.ArrayList;
 * import java.util.List;
 * 
 * public class ExcelManager implements Exporteur {
 * 
 * public static List<Name> lire(String cheminFichier) {
 * List<Name> liste = new ArrayList<>();
 * 
 * if (cheminFichier == null || cheminFichier.isEmpty()) {
 * System.out.println("  Chemin de fichier vide.");
 * return liste;
 * }
 * 
 * // Supprimer les guillemets si présents
 * if ((cheminFichier.startsWith("'") && cheminFichier.endsWith("'"))
 * || (cheminFichier.startsWith("\"") && cheminFichier.endsWith("\""))) {
 * cheminFichier = cheminFichier.substring(1, cheminFichier.length() - 1);
 * }
 * 
 * try (FileInputStream fis = new FileInputStream(cheminFichier);
 * Workbook workbook = new XSSFWorkbook(fis)) {
 * 
 * Sheet sheet = workbook.getSheetAt(0); // première feuille
 * 
 * for (Row row : sheet) {
 * if (row.getRowNum() == 0)
 * continue; // ignorer l'en-tête
 * 
 * // Colonne A (index 0) : id
 * Cell cellId = row.getCell(0);
 * if (cellId == null)
 * continue;
 * String id = cellId.toString().trim();
 * if (id.isEmpty())
 * continue;
 * 
 * // Colonnes B, C, D... : tokens du nom
 * List<String> tokens = new ArrayList<>();
 * for (int col = 1; col < row.getLastCellNum(); col++) {
 * Cell cell = row.getCell(col);
 * if (cell != null) {
 * String valeur = cell.toString().trim();
 * if (!valeur.isEmpty()) {
 * tokens.add(valeur);
 * }
 * }
 * }
 * 
 * if (!tokens.isEmpty()) {
 * liste.add(new Name(id, tokens.toArray(new String[0])));
 * }
 * }
 * 
 * } catch (IOException e) {
 * System.out.println("  Erreur lecture Excel : " + e.getMessage());
 * }
 * 
 * return liste;
 * }
 * }
 */