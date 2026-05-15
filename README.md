# Detecteur_De_Conformite
## Présentation
Le **Détecteur de Conformité** est une application Java conçue pour résoudre les problèmes de qualité de données lors de la manipulation de noms. Dans un monde où les données peuvent être saisies avec des fautes de frappe, des oublis d'accents ou des formats inconsistants, ce moteur permet de retrouver l'identité correcte d'une personne à partir d'une recherche approximative (FUZZY search). 

## Structure du projet 
```
Detecteur_De_Conformite/
│
├── .gitignore
├── README.md
│
├── run.sh
│
├── TestCapacite.java                # Pour les tests
├── TestPerf.java
│
├── lib/
│   └── icu4j-78.3.jar
│
├── model/
│   ├── Name.java                    # Structure de données pour les noms
│   │                                # encapsule id + tokens du nom brut
│   ├── Alerte.java
│   └── Resultat.java                # Gestion des résultats de recherche
│                                    # score + candidat matché
│
├── core/
│   ├── Moteur.java                  # c'est le moteur de recherche lui même
│   │                                # orchestre les 3 piliers (prétraitement,
│   │                                # comparaison, sélection)
│   ├── KycContext.java
│   ├── CsvManager.java
│   ├── ExcelManager.java
│   ├── Exporteur.java
│   ├── ExporteurCSV.java
│   └── BenchmarkKYC.java
│
├── pretraiteur/                     # Modules de nettoyage
│   ├── Pretraiteur.java             # Interface de base
│   │                                # CONTRAT pour la chaîne de traitement
│   ├── PretraiteurMinMaj.java
│   ├── PretraiteurSuppAccent.java
│   ├── PretraiteurSuppPonct.java
│   ├── PretraiteurTransliteration.java
│   └── titres/                      # Gestion des titres (M., Dr, etc.)
│       ├── DictTitres.java
│       ├── ExtracteurTitre.java
│       └── titres.txt
│                                    # liste complète des titres (civil, militaire...)
│
├── generateur/                      # (Candidat, ScanComplet...)
│   ├── GenerateurCandidat.java
│   │                                # interface pour optimiser les paires à comparer
│   ├── GenerateurScanComplet.java
│   ├── GenerateurIndexDouble.java
│   └── GenerateurIndexTokens.java
│
├── comparateur/                     # Algorithmes de similarité
│   ├── Comparateur.java             # Interface de base
│   │                                # CONTRAT comparer(ArrayList, ArrayList)
│   ├── ComparateurLevenshtein.java
│   ├── ComparateurJaroWinkler.java
│   ├── ComparateurSoundex.java
│   └── ComparateurComposite.java
│
├── selectionneur/                   # (Top, Percentage...)
│   ├── Selectionneur.java           # Interface de base
│   │                                # CONTRAT selectionner(ArrayList<Resultat>)
│   ├── SelectionneurTop.java
│   │                                # trie par score décroissant
│   └── SelectionneurPercentage.java
│                                    # filtre par % du meilleur score
│
├── ui/
│   └── MenuKYC.java
│
└── test/
    ├── TestKYC.java
    ├── test_clients.csv
    ├── test_user_list.csv
    └── resultats_test.csv
```
 
---
## Fonctionnement
Le système repose sur trois piliers fondamentaux :
- **Pipeline de Prétraitement** : Une série de filtres qui transforment une chaîne brute en une forme canonique "propre".
- **Génération des candidats** : 
- **Moteur de Comparaison** : Un ensemble d'algorithmes (Distance de Levenshtein, Jaro-Winkler, Soundex) pour calculer un score de similarité.
- **Sélectionneur de Résultats** : Un module qui classe et filtre les meilleurs candidats trouvés dans la base de données.

### Pilier 1 : Pipeline du prétraitement 

Le prétraitement est l'étape qui prépare les chaînes de caractères avant qu'elles ne soient comparées par les algorithmes de similarité. Son but est d'éliminer le "bruit" (accents, ponctuation, titres) pour ne garder que l'information essentielle.

**Architecture**
Tous les modules de prétraitement héritent de l'interface `Pretraiteur`. Cela permet au `Moteur` de gérer une **chaîne de traitement** (pipeline) où chaque module effectue une tâche spécifique l'un après l'autre.

---

** 1. Normalisation MAJ_min (`PretraiteurMinMaj`) **
*   **Fonctionnement** : Convertit l'intégralité du texte en majuscules (ou minuscules).
*   **Utilité** : Garantit que la recherche est insensible à la casse. 


** 2. Suppression de la Ponctuation (`PretraiteurSuppPonct`) **
*   **Fonctionnement** : Utilise l'expression régulière `\\p{Punct}` pour identifier et supprimer tous les symboles de ponctuation (virgules, points d'exclamation, tirets, etc.).
*   **Utilité** : Nettoie les saisies utilisateur "sales" ou les données provenant de formulaires mal formatés.


** 3. Suppression des Accents (`PretraiteurSuppAccent`) **
*   **Fonctionnement** : Utilise la classe `java.text.Normalizer` pour décomposer les caractères accentués (ex: 'é' devient 'e' + '´') puis supprime les marques diacritiques.
*   **Utilité** : Permet de comparer des noms sans se soucier des erreurs d'accentuation courantes.

** 4. Translittération des langues non-latines (`PretraiteurTransliteration`) **
*   **Fonctionnement** :
    *   Utilise la bibliothèque **ICU4J** (`icu4j-78.3.jar`) via la classe `com.ibm.icu.text.Transliterator`.
    *   Applique une chaîne de règles : `"Russian-Latin/BGN; Any-Latin; Latin-ASCII"` — d'abord le cyrillique vers le latin (norme BGN), puis tout autre script non-latin vers le latin, puis les caractères latins restants vers l'ASCII pur.
    *   Exemple de conversion :   `ДЕМИДОВИЧ` → `DEMIDOVICH` (cyrillique/russe)
        
    *   **Utilité** : Permet de détecter des correspondances entre un nom saisi en alphabet latin et le même nom enregistré dans un autre script (arabe, cyrillique, grec, chinois…), cas fréquent dans les listes PEP internationales.

** 5. Gestion des Titres Civils (`ExtracteurTitre` & `DictTitres`)** 
*   **Fonctionnement** :
    *   `DictTitres` : Charge une liste de titres (M., Mme, Dr, Prof, etc.) depuis un fichier `titres.txt`.
    *   `ExtracteurTitre` : Identifie si un mot est un titre et permet de l'isoler ou de le supprimer du nom complet.
*   **Utilité** : Évite que le titre ne fausse la comparaison du nom de famille.

---

** Tests et Performance **
Chaque module inclut une méthode `main` permettant de :
```
i.  Vérifier le bon fonctionnement fonctionnel.
ii.  Mesurer la performance avec un **test de complexité**.
iii.  Valider que le traitement est bien de complexité **O(n)**, garantissant **une exécution rapide même sur de gros volumes de données**.
```
## Pilier 2 : Génération des Candidats
 
La génération des candidats est l'étape qui détermine **quels enregistrements de la base de données (listeB) méritent d'être comparés** à chaque entrée de la requête (listeA). Son but est d'éviter une comparaison exhaustive coûteuse en ne retenant que les paires "plausibles".
 
**Architecture**
 
Tous les générateurs implémentent l'interface `GenerateurCandidat`, qui expose une unique méthode `genererCandidats(listeA, listeB)`. Cela permet au `Moteur` de **substituer librement** une stratégie par une autre selon le compromis vitesse/rappel souhaité.
 
---
 
**1. Scan Complet (`GenerateurScanComplet`)**
 
*   **Fonctionnement** : Génère le produit cartésien intégral des deux listes — chaque élément de listeA est apparié avec *tous* les éléments de listeB.
*   **Complexité** : **O(n × m)** — croissance quadratique.
*   **Utilité** : Sert de **référence de rappel maximal** (aucun candidat n'est omis) et de base de comparaison pour évaluer les stratégies plus rapides. Inadapté à de gros volumes.
 
**2. Index par Nombre de Tokens (`GenerateurIndexTokens`)**
 
*   **Fonctionnement** :
    *   **Phase d'indexation** : parcourt listeB une seule fois et construit un `Map<Integer, List<Integer>>` qui associe le nombre de tokens d'un nom à la liste de ses indices.
    *   **Phase de requête** : pour chaque nom de listeA, sonde l'index dans une fenêtre `[nbTokens - nb, nbTokens + nb]` (tolérance configurable `nb`).
*   **Complexité** : **O(m + n × k)** où k est le nombre de candidats retenus, bien inférieur à m en pratique.
*   **Utilité** : Élimine rapidement les noms dont le nombre de tokens est trop différent (ex : un prénom seul ne sera pas comparé à un nom composé de quatre tokens).
 
**3. Index Double Tokens × Longueur (`GenerateurIndexDouble`)**
 
*   **Fonctionnement** :
    *   **Phase d'indexation** (`construireIndex`) : construit un index à deux niveaux `Map<Integer, Map<Integer, List<Integer>>>` — d'abord par nombre de tokens, puis par longueur totale des tokens.
    *   **Mise en cache** : l'index est conservé en mémoire (`indexCache`) et n'est **recalculé que si listeB change** (comparaison par référence `listeB != derniereListeB`), évitant un recalcul inutile lors d'appels répétés sur la même base.
    *   **Phase de requête** : pour chaque nom de listeA, sonde l'index dans une double fenêtre :
        *   tolérance sur le nombre de tokens : [-nbPermisTokens, +nbPermisTokens]
        *   tolérance sur la longueur totale : [-longPermis, +longPermis]
*   **Complexité** : **O(m)** pour l'indexation + **O(n × k)** pour la requête, avec k très réduit grâce au double filtre.
*   **Utilité** : Stratégie la plus sélective — un nom de 10 caractères en 2 tokens ne sera apparié qu'avec des noms de longueur et de structure proches, réduisant drastiquement le nombre de paires transmises au moteur de comparaison.
 
---
 
**Tests et Performance**
 
Chaque générateur inclut une méthode `main` et une classe de démonstration (`DemoIndexDouble`) permettant de :
 
    i.   Vérifier le contenu de l'index construit (tokens → longueur → indices).
    ii.  Valider que le cache est bien réutilisé (référence identique à derniereListeB).
    iii. Confirmer que seules les paires respectant les fenêtres de tolérance sont retournées,
         garantissant un bon équilibre entre rappel et performance sur de gros volumes.

### Pilier 2 : Comparaison 
//oussama el fonctionnement mte3 el comparateurs 

### Pilier 3 : Séléctionneurs 
//Dammak séléctionneurs

