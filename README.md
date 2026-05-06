# Detecteur_De_Conformite
## Présentation
Le **Détecteur de Conformité** est une application Java conçue pour résoudre les problèmes de qualité de données lors de la manipulation de noms. Dans un monde où les données peuvent être saisies avec des fautes de frappe, des oublis d'accents ou des formats inconsistants, ce moteur permet de retrouver l'identité correcte d'une personne à partir d'une recherche approximative (FUZZY search). 

## Structure du projet 
```
.
├── Moteur.java                  # c'est le moteur de recherche lui même
│                                #    orchestre les 3 piliers (prétraitement,
│                                #     comparaison, sélection)
├── Name.java                    # Structure de données pour les noms
│                                #    encapsule id + tokens du nom brut
├── DemoMoteur.java              # Démo principale
│                                #    scénario de recherche avec requête "sale"
├── Resultat.java                # Gestion des résultats de recherche
│                                #    DTO score + candidat matché
├── GestionListe.java            # Gestionnaire de base de données de noms
│                                #    singleton pour fusionner les listes
├── README.md                    # Présentation du projet
│
├── pretraiteur/                 # Modules de nettoyage
│   ├── Pretraiteur.java         # Interface de base
│   │                            # CONTRAT pour la chaîne de traitement
│   ├── MinMaj/                  # Conversion MAJ_min 
│   │   └── PretraiteurMinMaj.java
│   │                            
│   ├── SuppAccent/              # Suppression accents
│   │   └── PretraiteurSuppAccent.java
│   │                            
│   ├── SuppPonct/               # Suppression ponctuation
│   │   └── PretraiteurSuppPonct.java
│   │                            
│   ├── titres/                  # Gestion des titres (M., Dr, etc.)
│   │   ├── DictTitres.java
│   │   │                        
│   │   ├── ExtracteurTitre.java
│   │   │                        
│   │   ├── DemoDetecteurTitres.java
│   │   │                        
│   │   └── titres.txt
│   │                            # liste complète des titres (civil, militaire...)
│   └── decomposer.java          # Décomposition de noms
│                                
│
├── Comparateur/                 # Algorithmes de similarité
│   ├── Comparateur.java         # Interface de base
│   │                            # CONTRAT comparer(ArrayList, ArrayList)
│   ├── ComparateurLevenshtein.java
│   │                           
│   ├── ComparateurJaroWinkler.java
│   │                           
│   └── ComparateurSoundex.java
│                                
│
├── Generateur/                  # (Candidat, ScanComplet...)
│   ├── GenerateurCandidat.java
│   │                            # interface pour optimiser les paires à comparer
│   ├── GenerateurScanComplet.java
│   │                            
│   ├── GenerateurIndexDouble.java
│   │                            
│   └── GenrateurIndexTokens.java
│                                
│
└── Selectionneur/               # (Top, Percentage...)
    ├── Selectionneur.java         # Interface de base
    │                                # CONTRAT selectionner(ArrayList<Resultat>)
    ├── SelectionneurTop.java
    │                                # trie par score décroissant
    └── SelectionneurPercentage.java
                                     # filtre par % du meilleur score
```
---
## Fonctionnement
Le système repose sur trois piliers fondamentaux :
- **Pipeline de Prétraitement** : Une série de filtres qui transforment une chaîne brute en une forme canonique "propre".
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
    

** 4. Gestion des Titres Civils (`ExtracteurTitre` & `DictTitres`)** 
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
