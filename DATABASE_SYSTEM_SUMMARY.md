# JSON Data Loading System - Complete Implementation

## Overview

A lightweight, dependency-free JSON data loading system for a MOBA game featuring **48 unique heroes** with complete stats, spells, and sprite configurations.

---

## Architecture

### JSON-Driven Configuration
- **48 heroes** stored in `src/Data/heroes.json` (52KB)
- Each hero defines: name, history, stats, sprite config (characterRow, hairRow, outfitFile, suitRow), and 3 spells
- **No external dependencies**: Custom JSON parser using Java standard library only
- Easy to edit, version control, balance without recompiling

### Data Loading Layer
- **Singleton JsonDataProvider** - Single instance, lazy initialization
- **Simple parsing** - Lightweight custom parser for JSON arrays and objects
- **Model classes** - Hero, Spell, Category (POJOs)
- **In-memory storage** - Loads all data at startup into Lists and Maps
- **Fast lookups** - O(1) hero retrieval by ID, O(1) spell lookup per hero

---

## Files

### Core/Database/
```
JsonDataProvider.java        # Singleton JSON parser and data provider
```

### Model Classes (Core.Database.model/)
```
Hero.java                    # Hero with stats and sprite configuration
Spell.java                   # Spell with damage, cooldown, mana cost, type
Category.java                # Hero category (Force, Agilité, Intelligence)
```

### Data
```
src/Data/heroes.json         # 48 heroes × 3 spells = 144 spells total
```

### No External Dependencies
✅ Uses only Java standard library (java.io, java.util)  
✅ No Gson, no Jackson, no database drivers  
✅ Zero external JAR files required  

---

## Data Structure

### Hero Model
```java
public class Hero {
    private int id;
    private String name;
    private String history;
    private int categoryId;           // 1=Force, 2=Agilité, 3=Intelligence
    private int baseHp, maxHp;
    private int attack, defense;
    private double attackSpeed;
    private int maxMana;
    private int characterRow;         // Row in Character Model.png (0-49)
    private int hairRow;             // Row in Hair/Hairs.png (0-9)
    private String outfitFile;       // PNG filename in Outfits/
    private Integer suitRow;        // Row in Suits.png (optional)
    private List<Spell> spells;
}
```

### Spell Model
```java
public class Spell {
    private int id;
    private int heroId;
    private String name;
    private String description;
    private String type;      // "dmg", "CC", "SP"
    private int damage;
    private double cooldown;
    private int manaCost;
}
```

### Category Model
```java
public class Category {
    private int id;
    private String name;
}
```

---

## Hero Categories (48 Total)

### Force (Tanks/Bruisers) - 17 heroes
**Stat Profile:** High HP (880-1150), High DEF (55-92), Low-Medium ATK (35-75), Low AS (0.3-1.2)
**Examples:** Goliath, Titan, Boulder, Fortress, Ironclad, Crusader, Paladin, Juggernaut, Aegis, Colossus, Berserker
**Spell Types:** Defensive buffs, stuns, knockbacks, area damage, damage reflection

### Agilité (DPS/Assassins) - 17 heroes
**Stat Profile:** Low HP (440-500), High ATK (82-95), High AS (1.8-2.3), Low DEF (17-30)
**Examples:** Swift, Blade, Shadow, Phantom, Nightshade, Viper, Huntress, Zephyr, Sting, Dagger, Trickster, Raven
**Spell Types:** Burst damage, invisibility, mobility, precision attacks, executes

### Intelligence (Mages/Support) - 14 heroes
**Stat Profile:** Very Low HP (380-460), Low ATK (25-45), Very High Mana (500-650), Low AS (0.8-1.1)
**Examples:** Archmage, Sorcerer, Warlock, Enchantress, Necromancer, Druid, Alchemist, Summoner, Time, Void
**Spell Types:** Massive AoE, teleportation, crowd control, healing, summons, reality manipulation

---

## Usage

### Get All Heroes
```java
JsonDataProvider dataProvider = JsonDataProvider.getInstance();
List<Hero> allHeroes = dataProvider.getAllHeroes();
```

### Get Hero by ID
```java
Hero hero = dataProvider.getHeroById(5);
if (hero != null) {
    System.out.println(hero.getName() + " - " + hero.getHistory());
}
```

### Get Spells for a Hero
```java
List<Spell> spells = dataProvider.getSpellsForHero(heroId);
for (Spell spell : spells) {
    System.out.println(spell.getName() + ": " + spell.getDescription());
}
```

### Get Categories
```java
List<Category> categories = dataProvider.getAllCategories();
Category force = dataProvider.getCategoryById(1);
Category agilite = dataProvider.getCategoryByName("Agilité");
```

### Hero Sprite Configuration
Each hero specifies how to compose their visual appearance:

```json
{
  "characterRow": 2,           // Row in Character Model.png (body/skin)
  "hairRow": 3,               // Row in Hairs.png
  "outfitFile": "leather_armor.png",  // File in Outfits/ (mapped to Outfit1-6.png)
  "suitRow": 0                // Row in Suits.png (optional, null if none)
}
```

**Sprite Composition (HeroSpriteCache)**:
1. Load base character body from `Character Model.png` at specified row
2. Load hair from `Hairs.png` at specified row
3. Load outfit file (mapped to one of 6 generic outfits via hash)
4. Composite: base → outfit → hair (layered in that order)

---

## Implementation Details

### Custom JSON Parser
- **parseJsonArray**: Splits JSON array by tracking brace nesting
- **parseJsonObject**: Parses key-value pairs, handles strings, numbers, and nested arrays
- **parseInt/parseDouble**: Safe number parsing with defaults
- **No external libraries**: Only `java.io` and `java.util`

### Sprite Cache Optimization
- **Cache key**: `characterRow_hairRow_outfitFile_direction_frame`
- Ensures each hero's unique visual combination gets its own cached sprite
- Prevents all heroes from sharing the same appearance (bug fix)

---

## Adding New Heroes

1. **Edit JSON** (`src/Data/heroes.json`):
   ```json
   {
     "name": "NewHero",
     "history": "Hero backstory...",
     "category": "Force",
     "baseHp": 1000, "maxHp": 1000, "attack": 60, "defense": 70,
     "attackSpeed": 0.8, "maxMana": 150,
     "characterRow": 10, "hairRow": 2, "outfitFile": "leather_armor.png", "suitRow": null,
     "spells": [
       { "name": "Spell1", "description": "...", "damage": 100, "cooldown": 5.0, "manaCost": 40, "type": "dmg" },
       { "name": "Spell2", "description": "...", "damage": 0, "cooldown": 10.0, "manaCost": 50, "type": "SP" },
       { "name": "Spell3", "description": "...", "damage": 150, "cooldown": 12.0, "manaCost": 60, "type": "dmg" }
     ]
   }
   ```

2. **No recompilation needed if only JSON changes** (unless adding new sprite files)

3. **Data auto-loads** on first `JsonDataProvider.getInstance()` call

---

## Build System

- **Build script:** `build.ps1` (PowerShell)
- **Dependencies:** None (pure Java standard library)
- **Output:** `bin/` directory

```powershell
# Clean build
.\build.ps1 -Clean

# Normal build
.\build.ps1
```

---

## Key Features

✅ **48 unique heroes** with distinct stats, spells, and lore  
✅ **JSON-driven** - Edit hero data without recompiling  
✅ **Zero dependencies** - Uses only Java standard library  
✅ **Sprite flexibility** - Each hero configures visual appearance  
✅ **Fast loading** - Single file read, in-memory storage  
✅ **Thread-safe** - Singleton with synchronized access  
✅ **Lightweight** - ~300 lines of parser code  
✅ **Extensible** - Easy to add more heroes or properties  

---

## Recent Changes (v1.1.0)

- ✅ **Fixed hero rendering bug**: All heroes now display unique appearances
  - Issue: Cache key used `hero.getId()` which was always 0 for JSON-loaded heroes
  - Fix: Cache key now uses `characterRow_hairRow_outfitFile_direction_frame`
  - File: `Engine/Render/HeroSpriteCache.java`
  
- ✅ **Removed Gson dependency**: Replaced with custom JSON parser
  - Eliminated `lib/gson-2.10.1.jar`
  - Created `Core/Database/JsonDataProvider.java` with pure Java implementation
  - Maintained same API, no changes to other code
  
- ✅ **Simplified architecture**: Removed database layer (SQLite, DAO pattern)
  - Project now uses direct JSON loading with in-memory data
  - Reduced complexity, faster startup, no external binaries

---

## Technical Stack

- **Language:** Java 17+
- **JSON:** Custom parser (no external libraries)
- **Pattern:** Singleton, Factory (implicit)
- **Build:** Custom PowerShell script with classpath
- **Total Classes:** 3 database-related classes (Hero, Spell, Category, JsonDataProvider)
- **Lines of Code:** ~500 including JSON parser (excluding JSON data)

---

**Status:** ✅ Fully functional, zero-dependency data loading  
**Data:** ✅ 48 heroes, 144 spells loaded from JSON  
**Performance:** ✅ Fast startup, minimal memory footprint  
**Ready:** ✅ Production-ready for the game engine
