# Java 2D MOBA Game

A 2D MOBA demonstration game built with Java and Java2D.

## About

This project is a fully functional 2D MOBA game engine with smooth movement, A* pathfinding, collision detection, and optimized rendering.

**Developed by**: Miantsa Fanirina  
**License**: MIT  
**Version**: 1.0.0

## Features

### Movement System
- **Fluid movement**: Keyboard (WASD) and mouse (right-click) movement
- **A* Pathfinding**: Smart pathfinding with trajectory smoothing
- **Collision detection**: Collision with walls, towers, and buildings
- **Slide system**: Sliding along obstacles when direct movement is blocked
- **Anti-stuck**: Automatic path recalculation when player is stuck

### Camera
- **Zoom**: Mouse wheel zoom (0.5x to 3x)
- **Scrolling**: Auto-scroll at screen edges
- **Dynamic zoom**: Auto-adapt to map size

### Graphics
- **Tilemap**: Tile-based map with image and color support
- **Water animation**: Animated water effect with multiple frames
- **Optimized rendering**: Only draws visible tiles (culling)
- **Visual effects**: Click ground effect

### MOBA Architecture
- **Arena system**: Two teams (Blue/Red)
- **Lanes**: Top, Mid, Bottom
- **Towers**: Tiered defensive structures with attack animations
  - Idle animation: Orb hovering cycle
  - Attack animation: Charge-up sequence with orb expansion
  - Projectile launch at midpoint of attack animation
- **Bases**: Ancients (main structures)
- **Hero System**: 48 unique heroes loaded from JSON with distinct appearances
- **No external dependencies**: Pure Java standard library (no Gson, no database)

### Recent Changes (v1.1.0)
- ✅ Fixed hero rendering: Each hero now displays unique appearance based on characterRow, hairRow, and outfitFile
- ✅ Removed Gson dependency: Replaced with custom JSON parser using only Java standard library
- ✅ Simplified data layer: Removed database system, now using direct JSON file loading
- ✅ Improved sprite caching: Cache key now includes visual attributes for proper hero differentiation

## Controls

| Input | Action |
|-------|--------|
| W | Move up |
| A | Move left |
| S | Move down |
| D | Move right |
| Right click | Move to clicked point |
| Scroll up | Zoom in |
| Scroll down | Zoom out |
| Screen edge | Move camera |

## Installation

### Prerequisites
- Java 17 or higher
- Windows (PowerShell scripts)

### Build

```powershell
# Build the project
powershell -ExecutionPolicy Bypass -File build.ps1
```

### Run

```powershell
# Run the game
powershell -ExecutionPolicy Bypass -File run.ps1
```

## Project Structure

```
src/
├── Main/                    # Game entry point
│   └── Main.java           # main method
│
├── Core/                    # Business logic (engine-independent)
│   ├── Config.java         # Global game configuration
│   ├── Database/           # Data loading (JSON-based, no external dependencies)
│   │   └── JsonDataProvider.java  # Custom JSON parser for heroes & spells
│   │
│   ├── Entity/             # Entities and physics
│   │   ├── Entity.java            # Base class
│   │   ├── Player.java            # The player
│   │   ├── PlayerMovement.java    # Movement logic
│   │   ├── PathFollower.java     # Pathfinding and path following
│   │   ├── CollisionDetector.java # Collision detection
│   │   ├── Direction.java        # Direction enum
│   │   ├── MathUtils.java        # Math utilities
│   │   ├── HitboxUtils.java      # Hitbox utilities
│   │   └── TileUtils.java        # Tile utilities
│   │
│   ├── Input/              # Input interfaces
│   │   ├── MoveInput.java        # Keyboard movement interface
│   │   └── TargetInput.java      # Mouse targeting interface
│   │
│   ├── Match/              # Game systems
│   │   └── PathFinder.java       # A* algorithm
│   │
│   ├── Moba/               # MOBA logic
│   │   ├── Combat/                # Combat system
│   │   │   ├── Stats.java              # Unit stats
│   │   │   └── StatsModifier.java      # Stat modifiers
│   │   ├── Items/                     # Item system
│   │   │   ├── Equipement.java           # Equipment
│   │   │   └── EquipementTier.java       # Equipment tiers
│   │   ├── Match/                      # Match management
│   │   │   └── Partie.java             # Match class
│   │   ├── Spells/                     # Spell system
│   │   │   ├── Sort.java                 # Spell
│   │   │   └── SortContext.java         # Spell context
│   │   ├── Units/                      # Game units
│   │   │   ├── Unite.java               # Base unit class
│   │   │   ├── Tour.java                # Towers
│   │   │   ├── Ancient.java            # Ancients (bases)
│   │   │   ├── Minion.java             # Minions
│   │   │   ├── Heros.java              # Heroes
│   │   │   ├── Creep.java              # Creeps
│   │   │   ├── RespawnTimer.java       # Respawn timer
│   │   │   └── RecallState.java        # Recall state
│   │   ├── World/                       # World elements
│   │   │   ├── Arena.java               # Main arena
│   │   │   ├── Equipe.java             # Team
│   │   │   ├── Base.java               # Base
│   │   │   ├── Fontaine.java           # Fountain
│   │   │   ├── Vec2.java              # 2D Vector
│   │   │   ├── TeamColor.java         # Team color
│   │   │   └── Voie.java              # Lanes
│   │   └── Ids/                        # Identification
│   │       └── GameId.java             # Unique IDs
│   │
│   └── Tile/                # Map management
│       ├── TileMap.java            # Map representation
│       └── CollisionTable.java     # Tile collision table
│
└── Engine/                   # Game engine and rendering
    ├── GamePanel.java            # Main Swing panel
    ├── GameEngine.java          # Game loop
    │
    ├── Input/                   # Input handling
    │   ├── KeyHandler.java      # Keyboard handler
    │   └── MouseHandler.java    # Mouse handler
    │
    ├── Render/                  # Graphics rendering
    │   ├── Camera.java         # Camera management
    │   ├── TileRenderer.java   # Tile rendering
    │   ├── PlayerRenderer.java # Player rendering
    │   ├── PlayerSprites.java  # Player sprite loading
    │   ├── TowerRenderer.java  # Tower rendering
    │   ├── HeroSpriteCache.java # Hero sprite composition cache
    │   └── ClickEffect.java    # Click visual effect
    │
    └── Tile/                   # Tile loading
        ├── Tile.java           # Tile representation
        ├── TileLoader.java    # Tile loader
        └── MapParser.java      # Map parser
```

## Technical Architecture

### Architecture Pattern

The project follows a layered architecture:

1. **Core**: Pure business logic, no graphics engine dependencies
2. **Engine**: Game engine, rendering, input handling

This separation allows:
- Testing logic independently from rendering
- Reusing Core code in other projects
- Easier maintenance

### Data Flow

```
Main.main()
    └── GamePanel (initialization)
            ├── Map loading (MapParser)
            ├── Tile loading (TileLoader)
            ├── Arena creation (Arena)
            └── Player creation (Player)

Game Loop (60 FPS):
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
                ├── Apply camera transform
                ├── TileRenderer.draw()
                ├── TowerRenderer.draw()
                ├── PlayerRenderer.draw()
                └── ClickEffects.draw()
```

### A* Algorithm

Pathfinding uses A* (A-star) algorithm with:

- **Heuristic**: Manhattan distance
- **Movement**: 8 directions (including diagonals)
- **Costs**: Diagonal movement cost √2
- **Obstacles**: Walls and towers
- **Optimization**: Early stopping when target reached

### Collision Detection

The collision system uses:
- **AABB (Axis-Aligned Bounding Box)**: Aligned collision boxes
- **Multi-point check**: 5-point verification (4 corners + center)
- **Hitbox inset**: Inner margin for smoother feel
- **Path clearing**: Path verification with reduced inset

## Configuration

See `Core.Config.java` for all parameters:

| Parameter | Value | Description |
|-----------|--------|-------------|
| TILE_SIZE | 48 (16×3) | Tile size in pixels |
| MAX_FPS | 60 | Frames per second |
| PLAYER_SPEED | 4 | Movement speed |
| CAMERA_SPEED | 20 | Scroll speed |
| MIN_ZOOM | 0.5 | Minimum zoom |
| MAX_ZOOM | 3.0 | Maximum zoom |

## Game Map

The map is defined in `src/Data/Map.txt` with format:
- Tile ID : color : name : [base64 image]

Special MOBA tiles:
- 20 : Blue tower
- 21 : Red tower
- 22 : Blue ancient (base)
- 23 : Red ancient (base)

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the project
2. Create a branch (`git checkout -b feature/name`)
3. Commit your changes (`git commit -m 'Add...'`)
4. Push to the branch (`git push origin feature/name`)
5. Create a Pull Request

## Acknowledgments

Developed with passion by Miantsa Fanirina.

Thanks to all contributors and testers!
