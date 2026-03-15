# Complete Technical Documentation

**Version**: 1.2.1

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Package Structure](#package-structure)
4. [Core Systems](#core-systems)
5. [Game Loop](#game-loop)
6. [Algorithms](#algorithms)
7. [Rendering Pipeline](#rendering-pipeline)
8. [Data Systems](#data-systems)
9. [Unit Systems](#unit-systems)
10. [Configuration Reference](#configuration-reference)
11. [Development Guide](#development-guide)
12. [Glossary](#glossary)

---

## Overview

This project is a production-ready 2D MOBA game engine written entirely in Java using Java2D Swing. It implements all core MOBA features with a clean, layered architecture that separates game logic from rendering.

**Key Statistics**:
- **~95 Java classes** across Core and Engine layers
- **48 unique heroes** loaded from JSON with 144 spells total
- **60 FPS** fixed timestep game loop
- **Zero external dependencies** - pure Java standard library
- **HUD system** with flexbox layout
- **Auto-detect keyboard layout** (QWERTY/AZERTY)
- **True fullscreen** mode (F11)
- **Enhanced UI state management** - unified mouse handling across all game states
- **Dynamic cursor management** - cursor changes based on hover states (hand cursor for interactive elements)
- **Improved PauseMenu** - proper positioning at mouse location with correct visibility handling
- **Fixed HeroSelectionPanel** - ensures layout bounds are calculated before click detection

---

## Architecture

### Layer Separation

```
┌─────────────────────────────────────────────────────────────────┐
│                        UI / Rendering                            │
│  (GamePanel, HeroSelectionPanel, all Renderer classes)          │
├─────────────────────────────────────────────────────────────────┤
│                         Engine Layer                             │
│  (GameEngine, Input handlers, Camera, Rendering coordination)    │
├─────────────────────────────────────────────────────────────────┤
│                         Core Layer                               │
│  (Entity, Movement, Collision, Pathfinding, MOBA logic)         │
├─────────────────────────────────────────────────────────────────┤
│                          Data Layer                              │
│  (JsonDataProvider, Hero/Spell models, Map data)                │
└─────────────────────────────────────────────────────────────────┘
```

**Design Principles**:
1. **Core independence**: Core package has no dependencies on Engine classes
2. **Single responsibility**: Each class has one clear purpose
3. **Dependency injection**: Interfaces (MoveInput, TargetInput) decouple logic from implementation
4. **Lazy initialization**: Systems initialize on first use

---

## Package Structure

### `src/Main/`

Application entry point.

| File | Description |
|------|-------------|
| `Main.java` | Application bootstrap, initializes all subsystems |

### `src/Core/`

Business logic independent of the graphics engine. This code can be reused in other projects.

#### `Core.Config`

All game configuration constants in one place.

**Key constants**:
- `TILE_SIZE`: 48 pixels (16 × 3 scale)
- `MAX_FPS`: 60 frames per second
- `PLAYER_SPEED`: 2 pixels per frame
- `CAMERA_*`: Edge threshold, speed, zoom levels

#### `Core.Database`

JSON-based data loading system (no external dependencies).

| File | Description |
|------|-------------|
| `JsonDataProviderFactory.java` | Factory for creating data providers |
| `JsonDataProvider.java` | Custom JSON parser (created by factory) |
| `model/Hero.java` | Hero data model with stats and spells |
| `model/Spell.java` | Spell data model |
| `model/Category.java` | Hero category (Force/Agilité/Intelligence) |
| `model/Player.java` | Player save data |
| `model/PlayerHero.java` | Player's owned heroes |
| `model/SpellType.java` | Spell type enumeration |

#### `Core.Entity`

Entity management and physics.

| Class | Description |
|-------|-------------|
| `Entity` | Abstract base class for all game entities |
| `Player` | User-controlled player entity |
| `PlayerMovement` | Complete movement logic (keyboard + mouse input) |
| `PathFollower` | Path following after A* pathfinding |
| `CollisionDetector` | World collision detection (AABB) |
| `Direction` | Cardinal direction enum (NORTH, SOUTH, EAST, WEST) |
| `MathUtils` | Math utility functions (distance, clamping) |
| `HitboxUtils` | Collision box utilities |
| `TileUtils` | Pixel-to-tile coordinate conversions |

#### `Core.Input`

Input abstraction interfaces for decoupling.

| Interface | Description |
|-----------|-------------|
| `MoveInput` | Keyboard movement input interface |
| `TargetInput` | Mouse targeting input interface |

#### `Core.Match`

Match-level systems.

| Class | Description |
|-------|-------------|
| `PathFinder` | A* (A-Star) algorithm implementation |

#### `Core.Moba`

MOBA-specific game logic.

##### `Core.Moba.Combat`

Combat system for units.

| Class | Description |
|-------|-------------|
| `Stats` | Unit statistics (HP, mana, damage, defense, attack speed) |
| `StatsModifier` | Stat modification system (buffs, debuffs) |

##### `Core.Moba.Items`

Item and equipment system.

| Class | Description |
|-------|-------------|
| `Equipement` | Equipment item |
| `EquipementTier` | Equipment tier levels |

##### `Core.Moba.Match`

Match session management.

| Class | Description |
|-------|-------------|
| `Partie` | Ongoing match manager |

##### `Core.Moba.Spells`

Spell system for heroes.

| Class | Description |
|-------|-------------|
| `Sort` | Spell representation |
| `SortContext` | Spell execution context |

##### `Core.Moba.Units`

All game unit types.

| Class | Description |
|-------|-------------|
| `Unite` | Abstract base class for all units |
| `Tour` | Defensive towers with animation states (idle/attack) |
| `TowerAI` | AI controlling tower targeting and attack timing |
| `TowerProjectile` | Projectile fired by towers |
| `CoreBase` | Main bases (Nexus) |
| `Minion` | AI-controlled creep units |
| `Heros` | Controllable heroes |
| `Creep` | Neutral/forest units |
| `RespawnTimer` | Hero respawn timer |
| `RecallState` | Recall (back to base) state |

**Tower Animation System (`Tour.java`)**:
- Frame 6-13: Idle animation loop (8 frames) - orb hovering cycle
- Frame 14-20: Attack animation sequence (7 frames) - charge-up sequence
- Fireball launches at frame 17 (mid-animation)
- Animation completes at frame 20, returns to idle

##### `Core.Moba.World`

World elements and map structures.

| Class | Description |
|-------|-------------|
| `Arena` | Complete arena with all towers, bases, and units |
| `Equipe` | Team (Radiant/Dire) |
| `Base` | Base structure with HP |
| `Fontaine` | Regeneration zone (fountain) |
| `Vec2` | 2D position vector |
| `TeamColor` | Team color (BLUE/RED) |
| `Voie` | Lane (TOP/MID/BOT) |

##### `Core.Moba.Ids`

Identification system.

| Class | Description |
|-------|-------------|
| `GameId` | Unique ID generator for units |

#### `Core.Tile`

Map management.

| Class | Description |
|-------|-------------|
| `TileMap` | In-memory map representation |
| `CollisionTable` | Tile collision lookup table |

### `src/Engine/`

Game engine and graphics rendering.

#### `Engine.GamePanel`

Main Swing panel.

| Class | Description |
|-------|-------------|
| `GamePanel` | Main JPanel handling display and initialization |
| `GameEngine` | Main game loop at 60 FPS |
| `GameInitializer` | Game initialization coordinator |
| `HeroSelectionPanel` | Hero selection UI |

#### `Engine.Input`

Input handling with keyboard layout detection.

| Class | Description |
|-------|-------------|
| `KeyHandler` | Keyboard event listener with AZERTY/QWERTY auto-detection |
| `KeyboardLayoutDetector` | OS keyboard layout detection (Windows/Mac/Linux) |
| `MouseHandler` | Mouse event listener |
| `MoveInput` | Engine implementation of Core MoveInput |
| `TargetInput` | Engine implementation of Core TargetInput |

**Keyboard Layout Detection**:
- Uses `KeyboardFocusManager.getCurrentKeyboardLayout()` to detect OS layout
- Falls back to locale detection (FR = AZERTY)
- Automatically maps WASD for QWERTY, ZQSD for AZERTY

#### `Engine.Render`

Graphics rendering system.

| Class | Description |
|-------|-------------|
| `Camera` | View management (zoom, pan, culling, player follow) |
| `TileRenderer` | Tile rendering with view culling |
| `PlayerRenderer` | Player sprite rendering |
| `PlayerSprites` | Player sprite image loading |
| `HeroSpriteCache` | Hero sprite composition with hair flip for LEFT |
| `HairSprites` | Hair sprite loading |
| `OutfitSprites` | Outfit sprite loading |
| `SuitSprites` | Suit (armor) sprite loading |
| `TowerRenderer` | Tower rendering with animations |
| `TowerSpriteCache` | Tower sprite animation cache |
| `ProjectileRenderer` | Projectile rendering |
| `ProjectileSpriteCache` | Projectile sprite cache |
| `HealthBarRenderer` | Health bar rendering |
| `ClickEffect` | Click ground visual effect |
| `DebugRenderer` | Debug overlay rendering |
| `UIRenderer` | UI overlay rendering |
| `WorldRenderer` | World elements rendering |
| `RenderingUtils` | Shared rendering utilities |

#### `Engine.Render.HUD`

HUD rendering system with flexbox layout.

| Class | Description |
|-------|-------------|
| `HUDRenderer` | Main HUD coordinator |
| `FlexContainer` | Flexbox-like layout container |
| `MinimapRenderer` | Minimap with actual tile images |
| `ScoreboardRenderer` | Team score display |
| `GoldDisplayRenderer` | Player gold counter |
| `CharacterPanelRenderer` | Hero portrait and stats |
| `AbilityBarRenderer` | Spell/ability bar |
| `ItemBarRenderer` | Inventory item bar |
| `TargetInfoRenderer` | Current target display |
| `HUDBackgrounds` | HUD background images |

#### `Engine.Tile`

Tile loading and parsing.

#### `Engine.UI`

User interface panels and menus.

| Class | Description |
|-------|-------------|
| `MainPanel` | Main menu UI with buttons for Play, Options, Quit |
| `PauseMenu` | In-game pause menu with Resume, Options, Quit to Main Menu |

---

## Recent Changes

### v1.2.1

- **Fixed HeroSelectionPanel click handling**: Ensures layout bounds are calculated before click detection to prevent click events from being missed
- **Fixed PauseMenu visibility issues**: Improved show/hide methods with proper positioning at mouse cursor location
- **Enhanced cursor management**: Dynamic cursor changes between default and hand cursor based on hover states in HUD and PauseMenu
- **Unified mouse handling**: Centralized mouse callbacks in GamePanel that route to appropriate handlers based on current game state (MAIN_MENU, HERO_SELECTION, PLAYING, PAUSED)
- **Removed recursive setVisible() calls**: Fixed StackOverflowError in PauseMenu by removing recursive setVisible() call

| Class | Description |
|-------|-------------|
| `Tile` | Tile representation |
| `TileLoader` | Tile loader from file |
| `MapParser` | Map file parser |

---

## Core Systems

### Player Movement System

The movement system handles all player locomotion:

```java
public class PlayerMovement {
    private final MoveInput moveInput;
    private final TargetInput targetInput;
    private final CollisionDetector collisionDetector;
    private final PathFollower pathFollower;
}
```

**Movement modes**:

1. **Keyboard (WASD)**:
   ```
   handleKeyboardMovement()
     ├─> Read WASD state from MoveInput
     ├─> Calculate direction vector
     ├─> Apply speed
     ├─> Check collision (slide if blocked)
     └─> Update position
   ```

2. **Mouse (Right-click)**:
   ```
   processMouseClick(targetX, targetY)
     ├─> Convert screen to world coords
     ├─> Check if path is clear (CollisionDetector)
     ├─> If clear: direct movement
     └─> If blocked: launch A* pathfinding
   ```

3. **Path Following**:
   ```
   moveToTarget()
     ├─> Get next node from PathFollower
     ├─> Calculate direction to node
     ├─> Move toward node
     └─> When reached: get next node
   ```

4. **Anti-stuck**:
   ```
   checkStuckAndRecalculate()
     ├─> Track consecutive frames at same position
     ├─> If stuck > threshold (60 frames):
     │     ├─> Clear current path
     │     └─> Recalculate new path
     └─> Reset on successful movement
   ```

**Slide System**: When direct movement is blocked, player slides along obstacle edges to maintain momentum and avoid getting stuck.

### Pathfinding System

The A* algorithm implementation:

```java
public class PathFinder {
    public List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow)
}
```

**Algorithm**:
```
1. Initialize openSet with start node (PriorityQueue by f-score)
2. While openSet is not empty:
   a. current = node with lowest f in openSet
   b. If current == target: return reconstructPath(current)
   c. Move current to closedSet
   d. For each neighbor (8 directions):
      - If obstacle or in closedSet: continue
      - Calculate g = current.g + movementCost (1 or √2 for diagonal)
      - Calculate h = Manhattan distance to target
      - Calculate f = g + h
      - If neighbor not in openSet or new g < old g:
        - Update neighbor (parent, g, f)
        - Add to openSet if not present
3. Return null (no path found)
```

**Path Smoothing**: After pathfinding, smoothing removes unnecessary waypoints by checking if A→C is clear when A→B→C exists.

### Collision Detection

AABB collision with 5-point verification:

```java
public class CollisionDetector {
    public boolean collidesAt(double x, double y)
    public boolean isPathClear(double x1, double y1, double x2, double y2)
}
```

**Implementation**:
```
collidesAt(x, y)
  ├─> Calculate hitbox with 6px inset (smoother feel)
  ├─> Check 5 points:
  │     ├─> Top-left corner
  │     ├─> Top-right corner
  │     ├─> Bottom-left corner
  │     ├─> Bottom-right corner
  │     └─> Center point
  ├─> For each point:
  │     ├─> Convert to tile coordinates
  │     ├─> Check tile collision table
  │     └─> Check building collision (towers, core bases)
  └─> Return true if any point collides
```

### Tower AI System

Complete tower behavior:

```java
public class TowerAI {
    public void mettreAJour(double deltaTime, List<Object> unites)
    public boolean doitAttaquer(double deltaTime)
    public int calculerDegats()
    public Object cible()
}
```

**Behavior**:
```
update(deltaTime, allUnits)
  ├─> Filter units by team (only enemies in range)
  ├─> Find closest enemy unit to tower
  ├─> If target exists:
  │     ├─> Start attack animation
  │     └─> At animation frame 17: fire projectile
  └─> Update attack cooldown

shouldAttack()
  ├─> Has valid target in range
  ├─> Attack cooldown ready
  └─> Target is valid (not dead, not invulnerable)

fire()
  ├─> Create TowerProjectile
  ├─> Calculate trajectory to target
  ├─> Apply damage on impact
  └─> Reset attack cooldown
```

---

## Game Loop

The game runs at fixed 60 FPS:

```
GameEngine.gameLoop()
  └─> While running:
        ├─> Wait until frame time elapsed (16.67ms)
        └─> update()
              ├─> Check player respawn → center camera
              ├─> Update player position for camera
              ├─> updateCamera()
              │     ├─> Player follow mode (when using WASD)
              │     ├─> Edge scrolling (mouse at screen edges)
              │     └─> Zoom (mouse wheel)
              ├─> updateClickEffects()
              │     ├─> Add new click effects
              │     └─> Update/remove expired effects
              ├─> updateTowers()
              │     ├─> Update tower AI
              │     ├─> Fire projectiles if ready
              │     └─> Update projectiles
              └─> player.update()
                    └─> PlayerMovement.update()
                          ├─> Process input
                          ├─> Move (keyboard or mouse)
                          ├─> Check collisions
                          └─> Update sprite
```

**Frame timing**:
- Target: 60 FPS = 16.67ms per frame
- Implementation: `System.nanoTime()` for precision
- Delta time: Fixed at 1/60 second for consistency

### Input System

**Keyboard Layout Detection**:
```
KeyHandler initialization
  └─> KeyboardLayoutDetector.isAzerty()
        ├─> Try: KeyboardFocusManager.getCurrentKeyboardLayout()
        ├─> Catch: Fall back to Locale.getDefault()
        └─> Return: AZERTY if FR locale, else QWERTY

Result:
  ├─> QWERTY: W/A/S/D for UP/LEFT/DOWN/RIGHT
  └─> AZERTY: Z/Q/S/D for UP/LEFT/DOWN/RIGHT
```

**Keyboard Controls**:
- `W`/`Z`: Move up (QWERTY/AZERTY)
- `A`/`Q`: Move left (QWERTY/AZERTY)
- `S`: Move down
- `D`: Move right
- `R`: Center camera on player
- `F11`: Toggle fullscreen

**Mouse Controls**:
- `Right click`: Set movement target
- `Mouse wheel`: Zoom in/out
- `Mouse at edge`: Pan camera (unless over minimap)

---

## Algorithms

### A* (A-Star) Pathfinding

**Complexity**: O(E log V) where E = edges, V = vertices

**Heuristic**: Manhattan distance (|x1-x2| + |y1-y2|)

**Movement**: 8 directions (including diagonals)

**Cost calculation**:
- Cardinal direction: 1
- Diagonal direction: √2 (approximately 1.414)

**Obstacles handled**:
- Wall tiles (solid tiles in collision table)
- Building collision (towers, core bases)

### Path Smoothing

```
Input: path = [A, B, C, D, E]

For each triplet (A, B, C):
  If isPathClear(A, C):
    Remove B from path

Result: Removes unnecessary waypoints for smoother movement
```

### Collision Detection (AABB)

```
Input: position (x, y), entity size

1. Calculate hitbox with inset (6px for smoothness)
2. Test 5 points:
   - Top-left
   - Top-right
   - Bottom-left
   - Bottom-right
   - Center
3. For each point:
   - Convert to tile: col = x / TILE_SIZE, row = y / TILE_SIZE
   - Check collision table for tile
   - Check building collision (towers, core bases)
4. Return collision status
```

---

## Rendering Pipeline

```
GamePanel.paintComponent(Graphics2D g)
  └─> Apply camera transform
      ├─> g.translate(-cameraX, -cameraY)
      └─> g.scale(zoom, zoom)
      
      ├─> TileRenderer.draw(g, camera, width, height)
      │     └─> View frustum culling
      │         ├─> Calculate visible tile range
      │         └─> Only draw tiles in range
      │
      ├─> TowerRenderer.draw(g, arena.tours())
      │     └─> Draw 21-frame animated sprites
      │
      ├─> PlayerRenderer.draw(g, player)
      │     └─> Draw composed hero sprite
      │
      ├─> ProjectileRenderer.draw(g, projectiles)
      │
      ├─> ClickEffects.draw(g)
      │
      └─> Reset transform
            ├─> g.scale(1/zoom, 1/zoom)
            └─> g.translate(cameraX, cameraY)
```

### View Frustum Culling

Only visible tiles are rendered for performance:

```
TileRenderer.draw()
  ├─> Calculate visible range:
  │     ├─> startCol = cameraX / TILE_SIZE
  │     ├─> endCol = (cameraX + screenWidth) / TILE_SIZE
  │     ├─> startRow = cameraY / TILE_SIZE
  │     └─> endRow = (cameraY + screenHeight) / TILE_SIZE
  ├─> Add buffer (1 tile) for smooth scrolling
  └─> Iterate and draw only visible tiles
```

### Hero Sprite Composition

Heroes are rendered from multiple layered sprites:

```
HeroSpriteCache.compose(hero, direction, frame)
  1. Check cache for key: characterRow_hairRow_outfitFile_direction_frame
  2. If not cached:
     a. Load base character from "Character Model.png" at characterRow
     b. Load hair from "Hairs.png" at hairRow
     c. Load outfit from "Outfits/" + outfitFile (mapped to Outfit1-6.png via hash)
     d. Load optional suit from "Suits.png" at suitRow (if not null)
     e. Composite: base → outfit → suit (optional) → hair
     f. Cache result
  3. Return cached image
```

---

## Data Systems

### JSON Data Loading

Custom JSON parser without external dependencies:

```
JsonDataProviderFactory.create()
  ├─> Lazy initialization on first call
  ├─> Read src/Data/heroes.json
  ├─> Parse using custom parser:
  │     ├─> parseJsonArray() - splits by brace nesting
  │     ├─> parseJsonObject() - parses key-value pairs
  │     └─> parseInt/parseDouble() - safe number parsing
  └─> Store in memory (List<Hero>, Map<Integer, Hero>)
```

**Data loaded**:
- 48 heroes with unique stats
- 3 spells per hero (144 total)
- 3 categories (Force, Agilité, Intelligence)

### Map Parsing

Map defined in `src/Data/Map.txt`:

```
Format: id:color:name:[base64 image]

Example:
0:#808080:Grass:
1:#404040:Road:
20:#0000FF:BlueTower:
22:#FF0000:RedCoreBase:
```

---

## Unit Systems

### Unit Hierarchy

```
Unite (abstract)
  ├── Player (controlled by user)
  ├── Tour (tower - defensive structure)
  ├── CoreBase (base - main structure)
  ├── Minion (AI creep - lane unit)
  ├── Heros (player-controlled hero)
  └── Creep (neutral unit)
```

### Tower System

- **3 tiers**: Near base (tier 1), middle (tier 2), far (tier 3)
- **Attack range**: Approximately 3-4 tiles
- **Damage**: Varies by tier
- **Attack speed**: Fixed interval between attacks
- **Animation**: 21 frames (idle 8, attack 7, cooldown 6)

### Team System

- **Blue Team (Radiant)**: Starts at map position (5, 5)
- **Red Team (Dire)**: Starts at opposite side
- **Lanes**: TOP, MID, BOTTOM
- **Objective**: Destroy enemy CoreBase

---

## Configuration Reference

### Core.Config

| Constant | Value | Description |
|----------|-------|-------------|
| `ORIGINAL_TILE_SIZE` | 16 | Base tile size before scaling |
| `SCALE` | 3 | Multiplier for tile size |
| `TILE_SIZE` | 48 | Final tile size in pixels |
| `MAX_FPS` | 60 | Target frames per second |
| `PLAYER_DEFAULT_SPEED` | 2 | Movement speed |
| `PLAYER_DEFAULT_X` | 5 tiles | Starting X position |
| `PLAYER_DEFAULT_Y` | 95 tiles | Starting Y position |
| `SPRITE_ANIMATION_SPEED` | 10 | Animation speed |
| `CAMERA_EDGE_THRESHOLD` | 80 | Edge scroll sensitivity |
| `CAMERA_SPEED` | 20 | Camera scroll speed |
| `MIN_ZOOM` | 1.3 | Minimum zoom level |
| `MAX_ZOOM` | 2.0 | Maximum zoom level |
| `ZOOM_STEP` | 0.1 | Zoom change per wheel tick |

---

## Development Guide

### Add a New Hero

1. Edit `src/Data/heroes.json`
2. Add new hero object with unique ID
3. Set stats, spells, and sprite configuration
4. No recompilation needed for JSON-only changes

### Add a New Unit Type

1. Create class in `Core.Moba.Units` extending `Unite`
2. Implement abstract methods
3. Add to `Arena` in constructor
4. Create renderer in `Engine.Render` if custom graphics

### Modify Player Stats

Edit in `Core.Config.java`:
- `PLAYER_DEFAULT_SPEED`
- `PLAYER_DEFAULT_X`
- `PLAYER_DEFAULT_Y`

### Add a New Tile Type

1. Add ID in `src/Data/Map.txt`
2. Add tile image in `src/Resource/Tiles/` (optional)
3. Set collision in `CollisionTable`

### Debug Mode

Enable debug rendering in `GameEngine`:
- Path visualization
- Collision boxes
- Unit info overlay

---

## Glossary

| Term | Definition |
|------|-------------|
| AABB | Axis-Aligned Bounding Box - collision box aligned to axes |
| A* | A-Star algorithm - optimal pathfinding algorithm |
| Culling | Rendering optimization - only draw visible elements |
| Delta time | Time elapsed since last frame |
| Entity | Any object in the game world |
| FPS | Frames per second |
| Hitbox | Collision area of an entity |
| Lane/Voie | Defined path on map (TOP, MID, BOT) |
| MOBA | Multiplayer Online Battle Arena |
| Pathfinding | Finding optimal path between two points |
| Tier | Tower level (1=near base, 3=far) |
| CoreBase | Main team structure (base/Nexus) |
| Tick | Time unit in game loop (1/60 second) |
| Fountain | Regeneration zone near base |

---

## Public API

### Creating a Player

```java
Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena);
```

### Updating Player (call each frame)

```java
player.update();
```

### Getting Position

```java
double x = player.getX();
double y = player.getY();
Direction dir = player.getDirection();
```

### Collision Detection

```java
CollisionDetector detector = new CollisionDetector(tileMap, collisionTable, arena);
boolean collides = detector.collidesAt(x, y);
boolean pathClear = detector.isPathClear(x1, y1, x2, y2);
```

### Pathfinding

```java
PathFollower follower = new PathFollower(tileMap, collisionTable, arena);
List<int[]> path = follower.findPath(startCol, startRow, targetCol, targetRow);
```

### Camera

The Camera class manages the game viewport with the following features:

```java
Camera camera = new Camera(width, height);

// Standard update with edge scrolling
camera.update(mouseX, mouseY);

// Zoom control
camera.zoom(wheelRotation);

// Set follow player mode (for WASD movement)
camera.setFollowPlayer(true);
camera.updatePlayerPosition(playerX, playerY);

// Center camera on specific position
camera.centerOn(playerX, playerY);

// Set minimap bounds (disables edge scrolling when cursor is over minimap)
camera.setMinimapBounds(minimapX, minimapY, minimapSize);

// Convert coordinates
int worldX = camera.screenToWorldX(screenX);
```

**Features**:
- **Edge scrolling**: Pan camera when mouse is near screen edges
- **Zoom**: Mouse wheel zoom with configurable min/max levels
- **Player follow**: Camera follows player when using keyboard movement
- **Quick recenter**: Center camera on player with `centerOn()`
- **Minimap exclusion**: Disable edge scrolling when cursor is over minimap
- **World bounds**: Camera stays within map boundaries

---

## File Statistics

- **Total Java files**: ~90
- **Total lines of code**: ~5000 (excluding generated)
- **Package count**: 15
- **Data files**: 2 (heroes.json, Map.txt)

---

Documentation by Miantsa Fanirina