# Java 2D MOBA Game Engine

A fully functional 2D MOBA game engine built with pure Java and Java2D Swing. This project demonstrates core MOBA gameplay mechanics including movement, pathfinding, combat, tower AI, and team-based gameplay.

**Developer**: Miantsa Fanirina  
**License**: MIT  
**Version**: 1.2.1

---

## Table of Contents

1. [Features](#features)
2. [Quick Start](#quick-start)
3. [Controls](#controls)
4. [Project Structure](#project-structure)
5. [Technical Architecture](#technical-architecture)
6. [Game Systems](#game-systems)
7. [Configuration](#configuration)
8. [Build & Run](#build--run)
9. [Hero System](#hero-system)
10. [Development Guide](#development-guide)

---

## Features

### Movement System
- **Fluid keyboard movement**: WASD controls with smooth acceleration
- **Mouse navigation**: Right-click to move to any point on the map
- **A* Pathfinding**: Intelligent pathfinding with trajectory smoothing algorithm
- **Advanced collision detection**: Multi-point verification (5-point AABB) against walls, towers, and buildings
- **Slide system**: Smooth sliding along obstacles when direct movement is blocked
- **Anti-stuck mechanism**: Automatic path recalculation when player is stuck for too long
- **Direction-aware sprites**: Player sprite changes based on movement direction

### Camera System
- **Edge scrolling**: Camera moves when mouse is near screen edges (80px threshold)
- **Mouse wheel zoom**: Adjustable zoom from 1.3x to 2.0x with smooth transitions
- **Dynamic zoom adaptation**: Camera automatically adjusts based on map size
- **World bounds clamping**: Camera stays within map boundaries
- **Smooth interpolation**: Camera movements are smooth and responsive

### Graphics & Rendering
- **Tile-based map**: 48x48 pixel tiles with support for image and color rendering
- **Optimized rendering**: View frustum culling - only visible tiles are rendered
- **Animated water tiles**: Multi-frame water animation for visual appeal
- **Hero sprite composition**: Dynamic sprite layering from base character, hair, outfit, and suit
- **Tower animations**: 21-frame animated towers with idle and attack states
- **Visual effects**: Click ground ripple effects
- **Health bars**: Above-unit health indicators

### MOBA Gameplay
- **Team system**: Blue (Radiant) and Red (Dire) teams
- **Lane system**: Three lanes - TOP, MID, BOTTOM
- **Tower hierarchy**: 3 tiers of towers per lane (near, mid, far from base)
- **Tower AI**: Automatic targeting of enemies in range, attack cooldown management
- **Tower projectiles**: Fireballs launched at specific animation frames
- **Base structures**: CoreBases (Nexus) as main team bases
- **Minions**: AI-controlled creep waves that spawn and move along lanes
- **Fountain zones**: Regeneration areas near base

### Hero System
- **48 unique heroes**: Loaded dynamically from JSON configuration
- **3 hero categories**: Force (Tank), Agilité (DPS), Intelligence (Mage)
- **Custom appearances**: Each hero has unique character, hair, outfit, and optional suit
- **Spell system**: Each hero has 3 unique spells with damage, cooldown, and mana cost
- **No recompilation needed**: Hero balance can be adjusted in JSON without rebuilding

### Data Architecture
- **Zero external dependencies**: Pure Java standard library only
- **Custom JSON parser**: Lightweight parser without Gson or Jackson
- **In-memory caching**: Fast hero and spell lookups
- **Singleton data provider**: Thread-safe access to game data

---

## Quick Start

```powershell
# Clone the repository
git clone <repository-url>
cd java-2d-game-demo

# Build the project
powershell -ExecutionPolicy Bypass -File build.ps1

# Run the game
powershell -ExecutionPolicy Bypass -File run.ps1
```

---

## Controls

| Input | Action |
|-------|--------|
| W | Move up (north) |
| A | Move left (west) |
| S | Move down (south) |
| D | Move right (east) |
| Right click | Move to clicked world position |
| Mouse wheel up | Zoom in |
| Mouse wheel down | Zoom out |
| Mouse at screen edge | Pan camera |

---

## Project Structure

```
java-2d-game-demo/
├── src/
│   ├── Main/                         # Application entry point
│   │   └── Main.java                # main() method, game initialization
│   │
│   ├── Core/                        # Business logic (engine-independent)
│   │   ├── Config.java              # Global game constants and settings
│   │   │
│   │   ├── Database/                # JSON data loading system
│   │   │   ├── JsonDataProviderFactory.java  # Factory for data providers
│   │   │   └── model/
│   │   │       ├── Hero.java             # Hero data model
│   │   │       ├── Spell.java            # Spell data model
│   │   │       ├── Category.java        # Hero category (Force/Agilité/Intelligence)
│   │   │       ├── SpellType.java       # Spell type enumeration
│   │   │       ├── Player.java          # Player save data
│   │   │       └── PlayerHero.java      # Player's owned heroes
│   │   │
│   │   ├── Entity/                  # Entities and physics
│   │   │   ├── Entity.java               # Abstract base for all entities
│   │   │   ├── Player.java              # User-controlled player entity
│   │   │   ├── PlayerMovement.java      # Complete movement logic (keyboard + mouse)
│   │   │   ├── PathFollower.java        # Pathfinding and path following
│   │   │   ├── CollisionDetector.java   # World collision detection
│   │   │   ├── Direction.java           # Cardinal direction enum
│   │   │   ├── MathUtils.java           # Math utility functions
│   │   │   ├── HitboxUtils.java         # Collision box utilities
│   │   │   └── TileUtils.java           # Pixel-tile coordinate conversions
│   │   │
│   │   ├── Input/                   # Input abstraction interfaces
│   │   │   ├── MoveInput.java           # Keyboard movement interface
│   │   │   └── TargetInput.java         # Mouse targeting interface
│   │   │
│   │   ├── Match/                   # Match-level systems
│   │   │   └── PathFinder.java          # A* pathfinding algorithm
│   │   │
│   │   ├── Moba/                    # MOBA-specific logic
│   │   │   ├── Combat/
│   │   │   │   ├── Stats.java               # Unit stats (HP, mana, damage, etc.)
│   │   │   │   └── StatsModifier.java       # Stat modification system
│   │   │   │
│   │   │   ├── Items/
│   │   │   │   ├── Equipement.java         # Equipment item
│   │   │   │   └── EquipementTier.java     # Equipment tier levels
│   │   │   │
│   │   │   ├── Match/
│   │   │   │   └── Partie.java            # Match session manager
│   │   │   │
│   │   │   ├── Spells/
│   │   │   │   ├── Sort.java              # Spell representation
│   │   │   │   └── SortContext.java       # Spell execution context
│   │   │   │
│   │   │   ├── Units/
│   │   │   │   ├── Unite.java            # Base unit class (abstract)
│   │   │   │   ├── Tour.java             # Tower with animations
│   │   │   │   ├── TowerAI.java         # AI controlling tower behavior
│   │   │   │   ├── TowerProjectile.java # Projectile fired by towers
│   │   │   │   ├── CoreBase.java         # Base structure (Nexus)
│   │   │   │   ├── Minion.java          # AI-controlled creep units
│   │   │   │   ├── Heros.java           # Controllable hero units
│   │   │   │   ├── Creep.java           # Neutral/forest units
│   │   │   │   ├── RespawnTimer.java    # Hero respawn timer
│   │   │   │   └── RecallState.java     # Recall-to-base state
│   │   │   │
│   │   │   ├── World/
│   │   │   │   ├── Arena.java          # Complete arena with all structures
│   │   │   │   ├── Equipe.java         # Team (Radiant/Dire)
│   │   │   │   ├── Base.java           # Base structure with HP
│   │   │   │   ├── Fontaine.java       # Regeneration fountain zone
│   │   │   │   ├── Vec2.java          # 2D position vector
│   │   │   │   ├── TeamColor.java     # Team color (BLUE/RED)
│   │   │   │   └── Voie.java          # Lane (TOP/MID/BOT)
│   │   │   │
│   │   │   └── Ids/
│   │   │       └── GameId.java       # Unique ID generator
│   │   │
│   │   └── Tile/                    # Map management
│   │       ├── TileMap.java         # In-memory map representation
│   │       └── CollisionTable.java  # Tile collision lookup table
│   │
│   └── Engine/                      # Game engine and rendering
│       ├── GamePanel.java          # Main Swing JPanel
│       ├── GameEngine.java         # Game loop (60 FPS)
│       ├── GameInitializer.java    # Game initialization coordinator
│       ├── HeroSelectionPanel.java  # Hero selection UI
│       │
│       ├── Input/                  # Input handlers
│       │   ├── KeyHandler.java      # Keyboard event listener
│       │   ├── MouseHandler.java    # Mouse event listener
│       │   ├── MoveInput.java       # Engine implementation of MoveInput
│       │   └── TargetInput.java     # Engine implementation of TargetInput
│       │
│       ├── Render/                 # Graphics rendering
│       │   ├── Camera.java              # View management (zoom, pan, culling)
│       │   ├── TileRenderer.java       # Tile rendering with view culling
│       │   ├── PlayerRenderer.java     # Player sprite rendering
│       │   ├── PlayerSprites.java      # Player sprite image loading
│       │   ├── HeroSpriteCache.java   # Hero sprite composition cache
│       │   ├── HairSprites.java       # Hair sprite loading
│       │   ├── OutfitSprites.java     # Outfit sprite loading
│       │   ├── SuitSprites.java       # Suit (armor) sprite loading
│       │   ├── TowerRenderer.java     # Tower sprite rendering
│       │   ├── TowerSpriteCache.java  # Tower sprite animation cache
│       │   ├── ProjectileRenderer.java     # Projectile rendering
│       │   ├── ProjectileSpriteCache.java  # Projectile sprite cache
│       │   ├── HealthBarRenderer.java # Health bar rendering
│       │   ├── ClickEffect.java       # Click ground visual effect
│       │   ├── DebugRenderer.java     # Debug overlay rendering
│       │   ├── UIRenderer.java        # UI overlay rendering
│       │   ├── WorldRenderer.java    # World elements rendering
│       │   └── RenderingUtils.java   # Shared rendering utilities
│       │
│       └── Tile/                    # Tile resources
│           ├── Tile.java           # Tile representation
│           ├── TileLoader.java     # Tile image loader
│           └── MapParser.java      # Map file parser
│
├── src/Data/
│   ├── heroes.json             # 48 hero configurations
│   └── Map.txt                # Tile map definition
│
├── src/Resource/              # Game assets
│   ├── Characters/            # Character sprites
│   ├── Tiles/                 # Tile images
│   └── ...
│
├── lib/                       # External JAR dependencies (none required)
├── out/                       # Compiled output
├── build.ps1                 # Build script
├── run.ps1                   # Run script
└── README.md                 # This file
```

---

## Technical Architecture

### Layered Architecture

The project follows a clear **layered architecture** that separates concerns:

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                      │
│         (JPanel, Hero Selection, Rendering)      │
├─────────────────────────────────────────────────┤
│                Engine Layer                     │
│    (Game loop, Input, Rendering, Camera)        │
├─────────────────────────────────────────────────┤
│                 Core Layer                      │
│  (Movement, Collision, Pathfinding, MOBA logic)│
├─────────────────────────────────────────────────┤
│                Data Layer                       │
│         (JSON parsing, Hero/Spell data)         │
└─────────────────────────────────────────────────┘
```

**Benefits**:
- Core logic can be tested independently from rendering
- Code is reusable in other projects
- Clear separation of concerns
- Easier debugging and maintenance

### Game Loop

The game runs at a fixed **60 FPS** using a delta-time based loop:

```
Main.main()
  └─> GameInitializer.initialize()
        ├─> MapParser.loadMap()          // Load Map.txt
        ├─> TileLoader.loadTiles()       // Load tile images
        ├─> Arena.create()               // Create teams/towers/bases
        └─> JsonDataProviderFactory.create()  // Load heroes.json

Game Loop (60 FPS):
  └─> GameEngine.gameLoop()
        └─> update()
              ├─> updateCamera()        // Edge scroll + zoom
              ├─> updateClickEffects()  // Visual effects
              ├─> updateTowers()       // Tower AI + projectiles
              └─> player.update()       // Movement + collision
```

### Data Flow

```
Input → Core Logic → Engine → Rendering → Screen

Example: Player clicks on map
1. MouseHandler captures click (screen coordinates)
2. Camera converts to world coordinates
3. PlayerMovement receives target position
4. CollisionDetector checks path validity
5. If blocked, PathFinder calculates A* path
6. PathFollower follows path node by node
7. Player updates position
8. PlayerRenderer draws sprite at new position
```

---

## Game Systems

### Movement System

The movement system handles three modes:

1. **Direct Mode**: Path is clear, player moves straight to target
2. **Pathfinding Mode**: Path is blocked, A* algorithm finds optimal route
3. **Slide Mode**: Direct movement blocked, player slides along obstacles

**Flow**:
```
processMouseClick(targetX, targetY)
  ├─> collisionDetector.isPathClear(player, target)
  │     └─> If clear: moveToTarget()
  │     └─> If blocked: findPath() → followPath()
  └─> handleKeyboardMovement()
        └─> Apply WASD input with collision check

moveToTarget()
  ├─> Calculate direction vector
  ├─> Normalize to speed
  ├─> Apply collision check
  └─> Update position

checkStuckAndRecalculate()
  ├─> Track stuck frames
  ├─> If stuck > threshold: recalculate path
  └─> Reset on successful movement
```

### A* Pathfinding Algorithm

The PathFinder implements A* (A-Star) algorithm:

```
1. Initialize openSet with start node (priority queue by f-score)
2. While openSet is not empty:
   a. current = node with lowest f-score in openSet
   b. If current == target: reconstruct path using parents
   c. Move current to closedSet
   d. For each neighbor (8 directions):
      - If obstacle or in closedSet: skip
      - Calculate g (cost from start), h (heuristic to target), f = g + h
      - If new path is better: update parent and add to openSet
3. Return null if no path found
```

**Characteristics**:
- **Heuristic**: Manhattan distance (|dx| + |dy|)
- **Movement**: 8 directions including diagonals
- **Diagonal cost**: √2 (approximately 1.414)
- **Obstacles**: Walls and buildings
- **Complexity**: O(E log V) where E = edges, V = vertices

### Path Smoothing

After A* path calculation, smoothing removes unnecessary waypoints:

```
For each triplet of nodes A → B → C:
  If isPathClear(A, C):
    Remove B from path
```

This creates smoother, more natural movement trajectories.

### Collision Detection

The collision system uses **AABB (Axis-Aligned Bounding Box)**:

```
collidesAt(x, y)
  ├─> Calculate entity hitbox (inset by 6px for smoothness)
  ├─> 5-point verification:
  │     ├─> Top-left corner
  │     ├─> Top-right corner
  │     ├─> Bottom-left corner
  │     ├─> Bottom-right corner
  │     └─> Center point
  ├─> Check tile collision (solid tiles)
  ├─> Check building collision (towers, core bases)
  └─> Return true if any point collides
```

**Design decisions**:
- 6-pixel inset creates smoother feel
- 5-point check prevents corner clipping
- Tile and building collision separately for precision

### Tower AI System

Towers have a complete AI system:

```
TowerAI.update(deltaTime, allUnits)
  ├─> Find targets in range (filter by team)
  ├─> Select priority target (closest to tower)
  ├─> Start attack animation if target valid
  └─> Fire projectile at animation frame 17

TowerAI.shouldAttack()
  ├─> Has valid target
  ├─> Attack cooldown ready
  └─> Target in range

TowerAI.fire()
  ├─> Create TowerProjectile
  ├─> Calculate trajectory to target
  ├─> Apply damage on impact
  └─> Reset attack cooldown
```

### Camera System

```
Camera.update(mouseX, mouseY)
  ├─> Edge scrolling:
  │     ├─> If mouse < edge threshold: move camera up/left
  │     └─> If mouse > screen - edge: move camera down/right
  ├─> Zoom: apply mouse wheel rotation
  └─> Clamp to world bounds

Coordinate conversion:
  ├─> screenToWorld(screenX, screenY)
  │     └─> Apply inverse zoom and camera offset
  └─> worldToScreen(worldX, worldY)
        └─> Apply zoom and camera offset
```

---

## Configuration

All game configuration is centralized in `Core.Config.java`:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `TILE_SIZE` | 48 (16×3) | Tile size in pixels |
| `MAX_FPS` | 60 | Frames per second |
| `PLAYER_SPEED` | 2 | Movement speed in pixels/frame |
| `PLAYER_DEFAULT_X` | 5 tiles | Starting X position |
| `PLAYER_DEFAULT_Y` | 95 tiles | Starting Y position |
| `SPRITE_ANIMATION_SPEED` | 10 | Animation frame rate |
| `CAMERA_EDGE_THRESHOLD` | 80 | Edge scroll sensitivity (pixels) |
| `CAMERA_SPEED` | 20 | Camera scroll speed |
| `MIN_ZOOM` | 1.3 | Minimum zoom level |
| `MAX_ZOOM` | 2.0 | Maximum zoom level |
| `ZOOM_STEP` | 0.1 | Zoom change per wheel tick |

---

## Build & Run

### Prerequisites
- Java 17 or higher
- Windows (PowerShell scripts)
- ~100MB disk space

### Build Commands

```powershell
# Clean and build (removes previous output)
powershell -ExecutionPolicy Bypass -File build.ps1 -Clean

# Incremental build
powershell -ExecutionPolicy Bypass -File build.ps1
```

### Run Commands

```powershell
# Run the game
powershell -ExecutionPolicy Bypass -File run.ps1
```

### Output
- Compiled classes: `out/production/java-2d-game-demo/`
- No JAR files generated (direct execution)

---

## Hero System

### Hero Categories

The game features 48 heroes across 3 categories:

| Category | French Name | Stat Focus | Count |
|----------|-------------|------------|-------|
| Tank | Force | HP, Defense | 17 |
| DPS | Agilité | Attack Speed, Damage | 17 |
| Mage | Intelligence | Mana, Spell Power | 14 |

### Hero Configuration (JSON)

Each hero in `heroes.json` defines:

```json
{
  "id": 1,
  "name": "Goliath",
  "history": "A towering warrior...",
  "category": "Force",
  "baseHp": 1000,
  "maxHp": 1000,
  "attack": 55,
  "defense": 80,
  "attackSpeed": 0.8,
  "maxMana": 200,
  "characterRow": 0,
  "hairRow": 0,
  "outfitFile": "plate_armor.png",
  "suitRow": null,
  "spells": [
    {
      "name": "Smash",
      "description": "Deals damage...",
      "type": "dmg",
      "damage": 80,
      "cooldown": 5.0,
      "manaCost": 30
    }
  ]
}
```

### Sprite Composition

Hero visuals are composed dynamically:

```
HeroSpriteCache.compose(hero)
  1. Load base character from "Character Model.png" at characterRow
  2. Load hair from "Hairs.png" at hairRow
  3. Load outfit from "Outfits/" + outfitFile (mapped to Outfit1-6.png)
  4. Load optional suit from "Suits.png" at suitRow
  5. Composite layers: base → outfit → suit → hair
  6. Cache result with key: characterRow_hairRow_outfitFile_direction_frame
```

---

## Development Guide

### Adding a New Hero

1. Edit `src/Data/heroes.json`
2. Add new hero object with all required fields
3. Optionally add sprite images to appropriate folders
4. No recompilation needed for JSON-only changes

### Adding a New Unit Type

1. Create class in `Core.Moba.Units` extending `Unite`
2. Implement abstract methods (stats, behavior, rendering)
3. Add to `Arena` in `Core.Moba.World.Arena`
4. Create renderer in `Engine.Render` if custom graphics needed

### Modifying Game Balance

Edit values in `Core.Config.java`:
- Movement speed: `PLAYER_DEFAULT_SPEED`
- Tower damage: Edit in tower class or JSON
- Hero stats: Edit in `src/Data/heroes.json`

### Debug Mode

Enable debug rendering by calling `DebugRenderer` in game loop to see:
- Collision boxes
- Path visualization
- Unit health bars
- Camera bounds

---

## Recent Changes

### v1.2.1 (Current)
- **Fixed HeroSelectionPanel click handling**: Ensures layout bounds are calculated before click detection to prevent click events from being missed
- **Fixed PauseMenu visibility issues**: Improved show/hide methods with proper positioning at mouse cursor location
- **Enhanced cursor management**: Dynamic cursor changes between default and hand cursor based on hover states in HUD and PauseMenu
- **Unified mouse handling**: Centralized mouse callbacks in GamePanel that route to appropriate handlers based on current game state (MAIN_MENU, HERO_SELECTION, PLAYING, PAUSED)
- **Fixed StackOverflowError**: Removed recursive setVisible() call in PauseMenu

### v1.1.0
- **48 unique heroes** with distinct stats and spells
- **Hero sprite differentiation**: Fixed cache key to use characterRow/hairRow_outfitFile
- **Zero external dependencies**: Custom JSON parser replaces Gson
- **Simplified architecture**: Removed SQLite, direct JSON loading
- **Improved sprite caching**: Each hero has unique visual appearance
- **Tower projectile system**: Fireballs with proper animation timing

### v1.0.0
- Basic movement system
- A* pathfinding
- Camera with zoom/scroll
- Tile-based rendering with culling
- Basic MOBA structures (towers, bases)

---

## Troubleshooting

### Game doesn't start
- Verify Java 17+ is installed: `java -version`
- Check build completed successfully: `ls out/production/`
- Ensure all source files compiled without errors

### Heroes all look the same
- This was fixed in v1.1.0 with HeroSpriteCache update
- Ensure using latest version
- Check heroes.json has unique characterRow values

### Performance issues
- Reduce zoom level (zoom out)
- Enable view culling (already enabled by default)
- Check no infinite loops in pathfinding

---

## License

MIT License - See LICENSE file for details.

---

## Acknowledgments

Developed with passion by **Miantsa Fanirina**.

Thanks to all contributors and testers who helped shape this project!

---

## Contact

For questions, issues, or contributions, please open an issue on the project repository.