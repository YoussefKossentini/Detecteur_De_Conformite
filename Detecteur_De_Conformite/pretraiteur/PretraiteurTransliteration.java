/*
 * //ce code est a fait a laide de Claude AI
 * package pretraiteur;
 * 
 * import java.util.ArrayList;
 * import com.ibm.icu.text.Transliterator;
 * 
 * public class PretraiteurTransliteration implements Pretraiteur {
 * private static final String RULES =
 * "Russian-Latin/BGN; Any-Latin; Latin-ASCII"; //cet attribut contient les
 * regles de transliteration (suggérées par ia)
 * private static final Transliterator transliterator =
 * Transliterator.getInstance(RULES);
 * 
 * public ArrayList<String> pretraiter(ArrayList<String> tokens) {
 * ArrayList<String> res = new ArrayList<>();
 * if (tokens == null) {
 * return res;
 * }
 * 
 * for (String token : tokens) {
 * if (token == null || token.isEmpty()) {
 * res.add(token);
 * continue;
 * }
 * res.add(transliterator.transliterate(token));
 * }
 * return res;
 * }
 * 
 * // generee par ia
 * public static void main(String[] args) {
 * PretraiteurTransliteration pt = new PretraiteurTransliteration();
 * 
 * ArrayList<String> test = new ArrayList<>();
 * test.add("ДЕМИДОВИЧ");
 * test.add("محمد");
 * test.add("北京");
 * test.add("Αθήνα");
 * 
 * try {
 * System.out.println("--- TEST TRANSLITTÉRATION ICU4J ---");
 * ArrayList<String> resultats = pt.pretraiter(test);
 * for (int i = 0; i < test.size(); i++) {
 * System.out.println(test.get(i) + "  =>  " + resultats.get(i));
 * }
 * } catch (NoClassDefFoundError e) {
 * System.err.
 * println("ERREUR : La bibliothèque icu4j.jar est absente du classpath.");
 * System.err.
 * println("Téléchargez-la sur https://icu.unicode.org/ et placez-la dans le dossier lib/."
 * );
 * }
 * }
 * }
 */