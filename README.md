# Java 2D MOBA Game

Un jeu de démonstration MOBA 2D développé en Java avec Java2D.

## À propos

Ce projet est un moteur de jeu MOBA 2D fonctionnel avec un système de déplacement fluide, pathfinding A*, gestion des collisions, et un système de rendu graphique optimisé.

**Développé par** : Miantsa Fanirina  
**Licence** : MIT  
**Version** : 1.0.0

## Fonctionnalités

### Système de Mouvement
- **Déplacement fluide** : Déplacement clavier (WASD) et souris (clic droit)
- **Pathfinding A\*** : Recherche de chemin intelligente avec lissage de trajectoire
- **Détection de collision** : Collision avec les murs, tours et bâtiments
- **Système de slide** : Glissement le long des obstacles quand le mouvement direct est bloqué
- **Anti-blocage** : Recalcul automatique du chemin quand le joueur est coincé

### Caméra
- **Zoom** : Molette de souris pour zoomer (0.5x à 3x)
- **Scrolling** : Déplacement automatique aux bords de l'écran
- **Zoom dynamique** : Adaptation automatique à la taille de la carte

### Graphismes
- **Tilemap** : Carte basée sur des tuiles avec support d'images et couleurs
- **Animation d'eau** : Effet d'eau animée avec plusieurs frames
- **Rendu optimisé** : Only dessine les tuiles visibles (culling)
- **Effets visuels** : Effet de clic au sol

### Architecture MOBA
- **Système d'arène** : Deux équipes (Bleu/Rouge)
- **Lanes** : Top, Mid, Bottom
- **Tours** : Tours avec niveaux (Tier 1, 2, 3)
- **Bases** : Anciens (structures principales)
- **Système extensible** : Unités, sorts, équipements

## Contrôles

| Entrée | Action |
|--------|--------|
| W | Déplacement vers le haut |
| A | Déplacement vers la gauche |
| S | Déplacement vers le bas |
| D | Déplacement vers la droite |
| Clic droit | Se déplacer vers le point cliqué |
| Molette haut | Zoom avant |
| Molette bas | Zoom arrière |
| Bord de l'écran | Déplacer la caméra |

## Installation

### Prérequis
- Java 17 ou supérieur
- Windows (scripts PowerShell)

### Compilation

```powershell
# Compiler le projet
powershell -ExecutionPolicy Bypass -File build.ps1
```

### Lancement

```powershell
# Lancer le jeu
powershell -ExecutionPolicy Bypass -File run.ps1
```

## Structure du Projet

```
src/
├── Main/                    # Point d'entrée du jeu
│   └── Main.java           # Méthode main
│
├── Core/                    # Logique métier (indépendant du moteur)
│   ├── Config.java         # Configuration globale du jeu
│   │
│   ├── Entity/             # Entités et physique
│   │   ├── Entity.java            # Classe de base
│   │   ├── Player.java            # Le joueur
│   │   ├── PlayerMovement.java    # Logique de mouvement
│   │   ├── PathFollower.java     # Pathfinding et suivi de chemin
│   │   ├── CollisionDetector.java # Détection des collisions
│   │   ├── Direction.java        # Énumération des directions
│   │   ├── MathUtils.java        # Utilitaires mathématiques
│   │   ├── HitboxUtils.java      # Utilitaires pour les hitboxes
│   │   └── TileUtils.java        # Utilitaires pour les tuiles
│   │
│   ├── Input/              # Interfaces d'entrée
│   │   ├── MoveInput.java        # Interface mouvement clavier
│   │   └── TargetInput.java      # Interface ciblage souris
│   │
│   ├── Match/              # Systèmes de jeu
│   │   └── PathFinder.java       # Algorithme A*
│   │
│   ├── Moba/               # Logique MOBA
│   │   ├── Combat/                # Système de combat
│   │   │   ├── Stats.java              # Statistiques des unités
│   │   │   └── StatsModifier.java      # Modificateurs de stats
│   │   ├── Items/                     # Système d'items
│   │   │   ├── Equipement.java           # Équipement
│   │   │   └── EquipementTier.java       # Niveaux d'équipment
│   │   ├── Match/                      # Gestion de partie
│   │   │   └── Partie.java             # Classe de partie
│   │   ├── Spells/                     # Système de sorts
│   │   │   ├── Sort.java                 # Sort
│   │   │   └── SortContext.java         # Contexte de sort
│   │   ├── Units/                      # Unités du jeu
│   │   │   ├── Unite.java               # Classe de base unité
│   │   │   ├── Tour.java                # Tours
│   │   │   ├── Ancient.java             # Bases (Anciens)
│   │   │   ├── Minion.java              # Minions
│   │   │   ├── Heros.java               # Héros
│   │   │   ├── Creep.java               # Creeps
│   │   │   ├── RespawnTimer.java        # Timer de respawn
│   │   │   └── RecallState.java         # État de rappel
│   │   ├── World/                       # Éléments du monde
│   │   │   ├── Arena.java               # Arène principale
│   │   │   ├── Equipe.java             # Équipe
│   │   │   ├── Base.java               # Base
│   │   │   ├── Fontaine.java           # Fontaine
│   │   │   ├── Vec2.java              # Vecteur 2D
│   │   │   ├── TeamColor.java         # Couleur d'équipe
│   │   │   └── Voie.java              # Voies (lanes)
│   │   └── Ids/                        # Identification
│   │       └── GameId.java             # IDs uniques
│   │
│   └── Tile/                # Gestion de la carte
│       ├── TileMap.java            # Représentation de la carte
│       └── CollisionTable.java     # Table de collisions
│
└── Engine/                   # Moteur de jeu et rendu
    ├── GamePanel.java            # Panel Swing principal
    ├── GameEngine.java          # Boucle de jeu
    │
    ├── Input/                   # Gestion des entrées
    │   ├── KeyHandler.java      # Gestionnaire clavier
    │   └── MouseHandler.java    # Gestionnaire souris
    │
    ├── Render/                  # Rendu graphique
    │   ├── Camera.java         # Gestion de la caméra
    │   ├── TileRenderer.java   # Rendu des tuiles
    │   ├── PlayerRenderer.java # Rendu du joueur
    │   ├── PlayerSprites.java   # Chargement sprites joueur
    │   ├── TowerRenderer.java  # Rendu des tours
    │   └── ClickEffect.java    # Effet visuel de clic
    │
    └── Tile/                   # Chargement des tuiles
        ├── Tile.java           # Représentation d'une tuile
        ├── TileLoader.java     # Chargeur de tuiles
        └── MapParser.java      # Parseur de carte
```

## Architecture Technique

### Pattern Architecture

Le projet suit une architecture en couches séparées :

1. **Core** : Logique métier pure, aucun dépendance au moteur graphique
2. **Engine** : Moteur de jeu, rendu, gestion des entrées

Cette separation permet de :
- Tester la logique indépendamment du rendu
- Réutiliser le code Core dans d'autres projets
- Faciliter la maintenance

### Flux de Données

```
Main.main()
    └── GamePanel (initialisation)
            ├── Chargement de la carte (MapParser)
            ├── Chargement des tuiles (TileLoader)
            ├── Création de l'arène (Arena)
            └── Création du joueur (Player)

Boucle de jeu (60 FPS):
    GameEngine.gameLoop()
        ├── GameEngine.update()
        │   ├── Camera.update() (scrolling + zoom)
        │   ├── ClickEffects.update()
        │   └── Player.update()
        │       └── PlayerMovement.update()
        │           ├── processMouseClick()
        │           ├── moveToTarget() / handleKeyboardMovement()
        │           └── checkStuckAndRecalculate()
        │
        └── GamePanel.renderLoop()
            └── paintComponent()
                ├── Application transformation caméra
                ├── TileRenderer.draw()
                ├── TowerRenderer.draw()
                ├── PlayerRenderer.draw()
                └── ClickEffects.draw()
```

### Algorithme A*

Le pathfinding utilise l'algorithme A* (A-star) avec les caractérisques suivantes :

- **Heuristique** : Distance de Manhattan
- **Mouvement** : 8 directions (incluant les diagonales)
- **Coûts** : Mouvement diagonal coût √2
- **Obstacles** : Murs et tours
- **Optimisation** :early stopping quand la cible est atteinte

### Détection de Collision

Le système de collision utilise :
- **AABB (Axis-Aligned Bounding Box)** : Boites de collision alignées
- **Multi-point check** : Vérification en 5 points (4 coins + centre)
- **Hitbox inset** : Marge intérieure pour plus de fluidité
- **Path clearing** : Vérification du chemin avec inset réduit

## Configuration

Voir `Core.Config.java` pour tous les paramètres :

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| TILE_SIZE | 48 (16×3) | Taille d'une tuile en pixels |
| MAX_FPS | 60 | Images par seconde |
| PLAYER_SPEED | 4 | Vitesse de déplacement |
| CAMERA_SPEED | 20 | Vitesse de scrolling |
| MIN_ZOOM | 0.5 | Zoom minimum |
| MAX_ZOOM | 3.0 | Zoom maximum |

## Carte du Jeu

La carte est définie dans `src/Data/Map.txt` avec le format :
- ID de tuile : couleur : nom : [image base64]

Tuiles spéciales pour MOBA :
- 20 : Tour bleue
- 21 : Tour rouge
- 22 : Ancient bleu (base)
- 23 : Ancient rouge (base)

## Contribution

Les contributions sont les bienvenues ! Veuillez suivre ces étapes :

1. Fork le projet
2. Créer une branche (`git checkout -b feature/nom`)
3. Commit vos changements (`git commit -m 'Ajout de...'`)
4. Push vers la branche (`git push origin feature/nom`)
5. Créer une Pull Request

## Remerciements

Développé avec passion par Miantsa Fanirina.

Merci à tous les contributeurs et testeurs !
