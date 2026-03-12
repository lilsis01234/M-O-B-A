# Complete Technical Documentation

## Table of Contents

1. [Overview](#overview)
2. [Package Structure](#package-structure)
3. [Main Classes](#main-classes)
4. [Game Systems](#game-systems)
5. [Algorithms](#algorithms)
6. [Development Guide](#development-guide)

---

## Overview

This project is a 2D MOBA game engine written in pure Java using Java2D Swing. It implements all basic MOBA features:

- Smooth movement with pathfinding
- Robust collision system
- Camera with zoom and scrolling
- Optimized rendering with culling
- Extensible architecture for MOBA units

---

## Package Structure

### `src/Main/`

Application entry point.

| File | Description |
|-------|-------------|
| `Main.java` | Main class with `main()` method |

### `src/Core/`

Business logic independent of graphics engine.

#### `Core.Config`
All game configuration constants.

#### `Core.Database`
Data loading system (JSON-based, no external dependencies).
- `JsonDataProvider.java` : Custom JSON parser that loads heroes and spells from `src/Data/heroes.json`

#### `Core.Entity`
Entity and physics management.

| Class | Description |
|-------|-------------|
| `Entity` | Abstract base class for all entities |
| `Player` | User-controlled player |
| `PlayerMovement` | Complete movement logic (keyboard + mouse) |
| `PathFollower` | Pathfinding and path following |
| `CollisionDetector` | World collision detection |
| `Direction` | Cardinal direction enum |
| `MathUtils` | Math utility functions |
| `HitboxUtils` : Collision box utilities |
| `TileUtils` : Pixel-tile conversions |

#### `Core.Input`
Input interfaces for decoupling.

| Interface | Description |
|-----------|-------------|
| `MoveInput` | Keyboard movement interface |
| `TargetInput` | Mouse targeting interface |

#### `Core.Match`
Gameplay systems.

| Class | Description |
|-------|-------------|
| `PathFinder` | A* algorithm implementation |

#### `Core.Moba`
MOBA-specific logic.

##### `Core.Moba.Combat`
Combat system.
- `Stats` : Stats (health, mana, damage, etc.)
- `StatsModifier` : Stat modifiers

##### `Core.Moba.Items`
Item system.
- `Equipement` : Wearable equipment
- `EquipementTier` : Equipment tiers

##### `Core.Moba.Match`
Match management.
- `Partie` : Ongoing match manager

##### `Core.Moba.Spells`
Spell system.
- `Sort` : Spell representation
- `SortContext` : Spell execution context

##### `Core.Moba.Units`
Game units.
- `Unite` : Base class for all units
- `Tour` : Defensive towers with animation states (idle/attack)
- `TowerAI` : AI controlling tower targeting and attack timing
- `TowerProjectile` : Projectile fired by towers (spawns 12.5px below tower top)
- `Ancient` : Main bases (Nexus)
- `Minion` : Generated units (creeps)
- `Heros` : Controllable heroes
- `Creep` : Neutral units
- `RespawnTimer` : Respawn timer
- `RecallState` : Recall (back to base) state

**Tower Animation System (`Tour.java`)**:
- Frame 6-13: Idle animation loop (8 frames)
- Frame 14-20: Attack animation sequence (7 frames)
- Fireball launches at frame 17 (mid-animation)
- Animation completes at frame 20, returns to idle

##### `Core.Moba.World`
World elements.
- `Arena` : Complete arena with towers and bases
- `Equipe` : Team (Radiant/Dire)
- `Base` : Base structure with HP
- `Fontaine` : Regeneration zone
- `Vec2` : 2D position vector
- `TeamColor` : Team color (BLUE/RED)
- `Voie` : Lanes (TOP/MID/BOT)

##### `Core.Moba.Ids`
Identification system.
- `GameId` : Unique ID generator

#### `Core.Tile`
Map management.
- `TileMap` : In-memory map representation
- `CollisionTable` : Tile collision table

### `src/Engine/`

Game engine and graphics rendering.

#### `Engine.GamePanel`
Main Swing panel, handles display and initialization.

#### `Engine.GameEngine`
Main game loop at 60 FPS.

#### `Engine.Input`
Input handlers.
- `KeyHandler` : Keyboard event listener
- `MouseHandler` : Mouse event listener

#### `Engine.Render`
Graphics rendering system.
- `Camera` : View management (zoom, pan)
- `TileRenderer` : Tile rendering with culling
- `PlayerRenderer` : Player sprite rendering
- `HeroSpriteCache` : Composes hero sprites from character, hair, and outfit components
- `PlayerSprites` : Player image loading
- `TowerRenderer` : Tower rendering with sprite animations (21 frames: idle 6-13, attack 14-20). Renders towers centered on 2x2 tile blocks, health bar positioned above tower (4px tall, 10px padding).
- `ClickEffect` : Click ground visual effect

#### `Engine.Tile`
Resource loading.
- `Tile` : Tile representation
- `TileLoader` : Tile loader from file
- `MapParser` : Map file parser

---

## Main Classes

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

**Responsibilities**:
- Manage player state
- Delegate movement to `PlayerMovement`
- Update sprite animation

**Dependencies**:
- `MoveInput` : Keyboard interface
- `TargetInput` : Mouse interface
- `CollisionDetector` : Collision detection
- `PathFollower` : Pathfinding

---

### `Core.Entity.PlayerMovement`

This is the core of the movement system. Manages:

1. **Mouse click** : `processMouseClick()`
   - Check if path is clear
   - Launch pathfinding if needed
   - Set target

2. **Move to target** : `moveToTarget()`
   - Calculate direction
   - Apply movement
   - Handle collisions

3. **Keyboard movement** : `handleKeyboardMovement()`
   - Interpret WASD
   - Update direction
   - Handle collisions with slide

4. **Stuck detection** : `checkStuckAndRecalculate()`
   - Stuck frame counter
   - Recalculate path if needed

---

### `Core.Entity.PathFollower`

Manages pathfinding and path following.

```java
// Find path between two tiles
List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow)

// Smooth path (remove unnecessary nodes)
void smoothPath(CollisionDetector collisionDetector)

// Advance one node in path
void advancePath()

// Get current path node
int[] getCurrentPathTarget()
```

---

### `Core.Entity.CollisionDetector`

Detects collisions between entities and world.

```java
// Check if position is colliding
boolean collidesAt(double topLeftX, double topLeftY)

// Check if path between two points is clear
boolean isPathClear(double x1, double y1, double x2, double y2)
```

**Collision types**:
- Walls (solid tiles)
- Towers (buildings)
- Ancients (bases)

---

### `Core.Match.PathFinder`

A* algorithm implementation.

```java
public List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow)
```

**Characteristics**:
- Uses PriorityQueue for nodes to explore
- Heuristic: Manhattan distance
- 8 movement directions
- Obstacle handling (walls and towers)

---

### `Engine.Render.Camera`

Manages game view.

```java
// Update position (scrolling)
void update(int mouseX, int mouseY)

// Zoom
void zoom(int wheelRotation)

// Coordinate conversion screen <-> world
int screenToWorldX(int screenX)
int screenToWorldY(int screenY)
```

**Features**:
- Edge scrolling when mouse at edges
- Zoom with mouse wheel
- Dynamic zoom (adapts to map size)
- Clamping to stay within world bounds

---

### `Engine.Render.TileRenderer`

Renders map tiles.

```java
void draw(Graphics2D g2, Camera camera, int panelWidth, int panelHeight)
```

**Optimizations**:
- **Culling**: Only draws visible tiles
- Visible range calculation based on camera
- Animation support (water)

---

## Game Systems

### Movement System

The movement system handles three modes:

1. **Direct mode**: Path is clear, player goes straight
2. **Pathfinding mode**: Path is blocked, use A*
3. **Slide mode**: Direct movement blocked, slide along walls

### Collision System

The system uses AABB (Axis-Aligned Bounding Box) with:
- 6-pixel inset for smoother feel
- 5-point verification (4 corners + center)
- Tile and building collision checking

### Camera System

The camera implements:
- **Edge scrolling**: Movement when mouse at screen edges
- **Zoom**: Mouse wheel with min/max limits
- **Dynamic zoom**: Minimum zoom based on map size

---

## Algorithms

### A* (A-Star)

The pathfinding algorithm used:

```
1. Initialize openSet with start node
2. While openSet is not empty:
   a. current = node with smallest f in openSet
   b. If current == target: reconstruct path
   c. Move current to closedSet
   d. For each neighbor:
      - If obstacle or in closedSet: skip
      - Calculate g, h, f
      - If new path better: update and add to openSet
3. Return null if no path
```

**Complexity**: O(E log V) where E = edges, V = vertices

### Path Smoothing

After A* path calculation, smoothing is applied:

```java
// For each triplet of nodes A, B, C:
// If path A->C is clear (without passing through B)
// then delete B from path
```

---

## Development Guide

### Add a new unit

1. Create a class in `Core.Moba.Units` extending `Unite`
2. Implement abstract methods (stats, behavior)
3. Add rendering in `TowerRenderer` if needed

### Add a new tile type

1. Add ID in `Map.txt` file
2. Optionally add image in `src/Resource/Tiles/`
3. Modify `TileLoader` if needed

### Modify player stats

Go to `Core.Config.java` and modify:
- `PLAYER_DEFAULT_SPEED`
- `PLAYER_DEFAULT_X`
- `PLAYER_DEFAULT_Y`

---

## Glossary

| Term | Definition |
|--------|------------|
| AABB | Axis-Aligned Bounding Box - Axis-aligned collision box |
| Culling | Optimization that only draws visible elements |
| Hitbox | Collision area of an entity |
| Lane/Voie | Defined path on map (Top, Mid, Bot) |
| Pathfinding | Path search |
| Tier | Tower level (1=near base, 3=far) |
| Ancient | Main team structure (base) |
| Tick | Time unit in game loop |

---

## Public API

### Main classes to use

```java
// Create a player
Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena);

// Update player (call each frame)
player.update();

// Get position
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

// Camera
Camera camera = new Camera(width, height);
camera.update(mouseX, mouseY);
camera.zoom(wheelRotation);
int worldX = camera.screenToWorldX(screenX);
```

---

Documentation by Miantsa Fanirina
