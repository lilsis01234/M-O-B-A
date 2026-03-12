# Documentation Technique Complète

## Table des Matières

1. [Vue d'Ensemble](#vue-densemble)
2. [Structure des Packages](#structure-des-packages)
3. [Classes Principales](#classes-principales)
4. [Systèmes de Jeu](#systèmes-de-jeu)
5. [Algorithmes](#algorithmes)
6. [Guide de Développement](#guide-de-développement)

---

## Vue d'Ensemble

Ce projet est un moteur de jeu MOBA 2D écrit en Java pur utilisant l'API Java2D de Swing. Il implémente toutes les fonctionnalités de base d'un jeu MOBA :

- Déplacement fluide avec pathfinding
- Système de collision robuste
- Caméra avec zoom et scrolling
- Rendu optimisé avec culling
- Architecture extensible pour les unités MOBA

---

## Structure des Packages

### `src/Main/`

Point d'entrée de l'application.

| Fichier | Description |
|---------|-------------|
| `Main.java` | Classe principale avec la méthode `main()` |

### `src/Core/`

Logique métier indépendante du moteur graphique.

#### `Core.Config`
Centralise toutes les constantes de configuration du jeu.

#### `Core.Entity`
Gestion des entités et de la physique.

| Classe | Description |
|--------|-------------|
| `Entity` | Classe de base abstraite pour toutes les entités |
| `Player` | Le joueur contrôlé par l'utilisateur |
| `PlayerMovement` | Logique complète de mouvement (clavier + souris) |
| `PathFollower` | Gestion du pathfinding et suivi de chemin |
| `CollisionDetector` | Détection des collisions avec le monde |
| `Direction` | Énumération des directions cardinales |
| `MathUtils` | Fonctions mathématiques utilitaires |
| `HitboxUtils` | Utilitaires pour les boites de collision |
| `TileUtils` | Conversions entre pixels et tuiles |

#### `Core.Input`
Interfaces pour découpler les entrées.

| Interface | Description |
|-----------|-------------|
| `MoveInput` | Interface pour le mouvement clavier |
| `TargetInput` | Interface pour le ciblage souris |

#### `Core.Match`
Systèmes liés au gameplay.

| Classe | Description |
|--------|-------------|
| `PathFinder` | Implémentation de l'algorithme A* |

#### `Core.Moba`
Logique spécifique au genre MOBA.

##### `Core.Moba.Combat`
Système de combat.
- `Stats` : Statistiques (vie, mana, dégats, etc.)
- `StatsModifier` : Modificateurs de statistiques

##### `Core.Moba.Items`
Système d'items.
- `Equipement` : Équipement portable
- `EquipementTier` : Niveaux d'équipement

##### `Core.Moba.Match`
Gestion de partie.
- `Partie` : Gestionnaire de partie en cours

##### `Core.Moba.Spells`
Système de sorts.
- `Sort` : Représentation d'un sort
- `SortContext` : Contexte d'exécution d'un sort

##### `Core.Moba.Units`
Unités du jeu.
- `Unite` : Classe de base pour toutes les unités
- `Tour` : Tours défensives
- `Ancient` : Bases principales (Nexus)
- `Minion` : Unitée générées ( creeps )
- `Heros` : Héros contrôlables
- `Creep` : Unités neutres
- `RespawnTimer` : Timer de résurrection
- `RecallState` : État de rappel (back to base)

##### `Core.Moba.World`
Éléments du monde.
- `Arena` : L'arène complète avec tours et bases
- `Equipe` : Une équipe (Radiant/Dire)
- `Base` : Structure de base avec PV
- `Fontaine` : Zone de régénération
- `Vec2` : Vecteur 2D pour les positions
- `TeamColor` : Couleur d'équipe (BLEU/ROUGE)
- `Voie` : Lanes du jeu (TOP/MID/BOT)

##### `Core.Moba.Ids`
Système d'identification.
- `GameId` : Générateur d'IDs uniques

#### `Core.Tile`
Gestion de la carte.
- `TileMap` : Représentation de la carte en mémoire
- `CollisionTable` : Table de collision des tuiles

### `src/Engine/`

Moteur de jeu et rendu graphique.

#### `Engine.GamePanel`
Panel Swing principal, gère l'affichage et l'initialisation.

#### `Engine.GameEngine`
Boucle de jeu principale à 60 FPS.

#### `Engine.Input`
Gestionnaires d'entrées.
- `KeyHandler` : Écoute les événements clavier
- `MouseHandler` : Écoute les événements souris

#### `Engine.Render`
Système de rendu graphique.
- `Camera` : Gestion de la vue (zoom, pan)
- `TileRenderer` : Rendu des tuiles avec culling
- `PlayerRenderer` : Rendu du sprite du joueur
- `PlayerSprites` : Chargement des images du joueur
- `TowerRenderer` : Rendu des tours et bâtiments
- `ClickEffect` : Effet visuel de clic

#### `Engine.Tile`
Chargement des ressources graphiques.
- `Tile` : Représentation d'une tuile
- `TileLoader` : Chargeur de tuiles depuis un fichier
- `MapParser` : Parseur du fichier de carte

---

## Classes Principales

### `Core.Entity.Player`

```java
public class Player extends Entity {
    private final MoveInput moveInput;
    private final TargetInput targetInput;
    private final CollisionDetector collisionDetector;
    private final PathFollower pathFollower;
    private final PlayerMovement movement;
}
```

**Responsabilités** :
- Gérer l'état du joueur
- Déléguer le mouvement à `PlayerMovement`
- Mettre à jour l'animation du sprite

**Dépendances** :
- `MoveInput` : Interface clavier
- `TargetInput` : Interface souris
- `CollisionDetector` : Détection des collisions
- `PathFollower` : Pathfinding

---

### `Core.Entity.PlayerMovement`

C'est le cœur du système de mouvement. Gère :

1. **Clic souris** : `processMouseClick()`
   - Vérifie si le chemin est dégagé
   - Lance le pathfinding si nécessaire
   - Définit la cible

2. **Mouvement vers cible** : `moveToTarget()`
   - Calcule la direction
   - Applique le mouvement
   - Gère les collisions

3. **Mouvement clavier** : `handleKeyboardMovement()`
   - Interprète WASD
   - Met à jour la direction
   - Gère les collisions avec slide

4. **Détection de blocage** : `checkStuckAndRecalculate()`
   - Compteur de frames bloqué
   - Recalcule le chemin si nécessaire

---

### `Core.Entity.PathFollower`

Gère le pathfinding et le suivi de chemin.

**Méthodes principales** :

```java
// Trouve un chemin entre deux tuiles
List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow)

// Lisse le chemin (supprime les nodes inutiles)
void smoothPath(CollisionDetector collisionDetector)

// Avance d'un node dans le chemin
void advancePath()

// Retourne le node actuel du chemin
int[] getCurrentPathTarget()
```

---

### `Core.Entity.CollisionDetector`

Détecte les collisions entre les entités et le monde.

```java
// Vérifie si une position est en collision
boolean collidesAt(double topLeftX, double topLeftY)

// Vérifie si le chemin entre deux points est dégagé
boolean isPathClear(double x1, double y1, double x2, double y2)
```

**Types de collisions** :
- Murs (tuilesolidée)
- Tours (bâtiments)
- Anciens (bases)

---

### `Core.Match.PathFinder`

Implémentation de l'algorithme A*.

```java
public List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow)
```

**Caractéristiques** :
- Utilise une PriorityQueue pour les nodes à explorer
- Heuristique : distance de Manhattan
- 8 directions de mouvement
- Gestion des obstacles (murs et tours)

---

### `Engine.Render.Camera`

Gère la vue du jeu.

```java
// Met à jour la position (scrolling)
void update(int mouseX, int mouseY)

// Zoom
void zoom(int wheelRotation)

// Conversion coordonnées écran <-> monde
int screenToWorldX(int screenX)
int screenToWorldY(int screenY)
```

**Fonctionnalités** :
- Scrolling aux bords de l'écran
- Zoom avec la molette
- Zoom dynamique (s'adapte à la taille de la carte)
- Clamping pour ne pas dépasser les limites du monde

---

### `Engine.Render.TileRenderer`

Rend les tuiles de la carte.

```java
void draw(Graphics2D g2, Camera camera, int panelWidth, int panelHeight)
```

**Optimisations** :
- **Culling** : Ne dessine que les tuiles visibles
- Calcul de la plage visible basée sur la caméra
- Support des animations (eau)

---

## Systèmes de Jeu

### Système de Mouvement

Le système de mouvement gère trois modes :

1. **Mode direct** : Le chemin est dégagé, le joueur va tout droit
2. **Mode pathfinding** : Le chemin est bloqué, on utilise A*
3. **Mode slide** : Le mouvement direct est bloqué, on slide le long des murs

### Système de Collision

Le système utilise des hitboxes AABB (Axis-Aligned Bounding Box) avec :
- Un inset de 6 pixels pour plus de fluidité
- 5 points de vérification (4 coins + centre)
- Vérification des collisions avec les tuiles et les bâtiments

### Système de Caméra

La caméra implémente :
- **Edge scrolling** : Déplacement quand la souris est aux bords
- **Zoom** : Molette avec limite min/max
- **Dynamic zoom** : Zoom minimum basé sur la taille de la carte

---

## Algorithmes

### A* (A-Star)

L'algorithme de pathfinding utilisé :

```
1. Initialiser openSet avec le node de départ
2. Tant que openSet n'est pas vide :
   a. Node actuel = node avec le plus petit f dans openSet
   b. Si node actuel == cible : reconstructeur le chemin
   c. Déplacer node actuel vers closedSet
   d. Pour chaque voisin :
      - Si obstacle ou dans closedSet : skip
      - Calculer g, h, f
      - Si nouveau chemin meilleur : mettre à jour et ajouter à openSet
3. Retourner null si pas de chemin
```

**Complexité** : O(E log V) où E = edges, V = vertices

### Lissage de Chemin

Après le calcul du chemin A*, on applique un lissage :

```java
// Pour chaque triplet de nodes A, B, C :
// Si le chemin A->C est dégagé (sans passer par B)
// alors supprimer B du chemin
```

---

## Guide de Développement

### Ajouter une nouvelle unité

1. Créer une classe dans `Core.Moba.Units` qui hérite de `Unite`
2. Implémenter les méthodes abstraites (stats, comportement)
3. Ajouter le rendu dans `TowerRenderer` si nécessaire

### Ajouter un nouveau type de tuile

1. Ajouter l'ID dans le fichier `Map.txt`
2. Optionally ajouter une image dans `src/Resource/Tiles/`
3. Modifier `TileLoader` si nécessaire

### Modifier les statistiques du joueur

Aller dans `Core.Config.java` et modifier :
- `PLAYER_DEFAULT_SPEED`
- `PLAYER_DEFAULT_X`
- `PLAYER_DEFAULT_Y`

---

## Glossaire

| Terme | Definition |
|--------|------------|
| AABB | Axis-Aligned Bounding Box - Boite de collision alignée aux axes |
| Culling | Optimisation qui ne dessine que les éléments visibles |
| Hitbox | Zone de collision d'une entité |
| Lane/Voie | Chemin défini sur la carte (Top, Mid, Bot) |
| Pathfinding | Recherche de chemin |
| Tier | Niveau d'une tour (1=près de la base, 3=loin) |
| Ancient | Structure principale de l'équipe (base) |
| Tick | Unité de temps dans la boucle de jeu |

---

## API Publique

### Classes principales à utiliser

```java
// Créer un joueur
Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena);

// Mettre à jour le joueur (à appeler chaque frame)
player.update();

// Obtenir la position
double x = player.getX();
double y = player.getY();
Direction dir = player.getDirection();

// Collision
CollisionDetector detector = new CollisionDetector(tileMap, collisionTable, arena);
boolean collides = detector.collidesAt(x, y);
boolean pathClear = detector.isPathClear(x1, y1, x2, y2);

// Pathfinding
PathFollower follower = new PathFollower(tileMap, collisionTable, arena);
List<int[]> path = follower.findPath(startCol, startRow, targetCol, targetRow);

// Caméra
Camera camera = new Camera(width, height);
camera.update(mouseX, mouseY);
camera.zoom(wheelRotation);
int worldX = camera.screenToWorldX(screenX);
```

---

Document écrit par Miantsa Fanirina
