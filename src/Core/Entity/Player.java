package Core.Entity;

import Core.Config;
import Core.Database.model.Hero;
import Core.Input.MoveInput;
import Core.Input.TargetInput;
import Core.Moba.Combat.Stats;
import Core.Moba.Units.Tour;
import Core.Moba.World.Arena;
import Core.Moba.World.Equipe;
import Core.Moba.World.TeamColor;
import Core.Moba.World.Vec2;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;

/**
 * Représente le joueur contrôlé par l'utilisateur.
 */
public class Player extends Entity {
    
    private final MoveInput moveInput;
    private final TargetInput targetInput;
    private final CollisionDetector collisionDetector;
    private final PathFollower pathFollower;
    private final PlayerMovement movement;
    private final Stats stats;
    private Arena arena;
    private Equipe equipe;
    private final TileMap tileMap;
    private int level = 1;
    
    // Hero-specific data
    private final Hero hero;
    private final int characterRow;
    private final int hairRow;
    private final String outfitFile;
    private final Integer suitRow;
    
    // Death and respawn tracking
    private boolean isAlive = true;
    private long deathTimeNanos = 0;
    private long respawnEndTimeNanos = 0;
    
    // Enemy wood damage tracking
    private long lastWoodDamageTimeNanos = 0;
    private static final long WOOD_DAMAGE_INTERVAL_NANOS = 200_000_000; // 200ms
    
    public Player(MoveInput moveInput, TargetInput targetInput, 
                  TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        this(moveInput, targetInput, tileMap, collisionTable, arena, null);
    }
    
    public Player(MoveInput moveInput, TargetInput targetInput, 
                  TileMap tileMap, CollisionTable collisionTable, Arena arena, Hero hero) {
        this.moveInput = moveInput;
        this.targetInput = targetInput;
        this.arena = arena;
        this.tileMap = tileMap;
        this.hero = hero;
        
        // Extract hero-specific rendering data
        if (hero != null) {
            this.characterRow = hero.getCharacterRow();
            this.hairRow = hero.getHairRow();
            this.outfitFile = hero.getOutfitFile();
            this.suitRow = hero.getSuitRow();
            
            // Use hero's base stats
            this.stats = new Stats(hero.getMaxHp(), hero.getMaxMana(), hero.getAttack(), hero.getDefense(), hero.getAttackSpeed());
        } else {
            // Default values for when no hero is specified (shouldn't happen in normal gameplay)
            this.characterRow = 0;
            this.hairRow = 0;
            this.outfitFile = "Outfit1.png";
            this.suitRow = null;
            this.stats = new Stats(250, 200, 15, 10, 200.0);
        }
        
        this.collisionDetector = new CollisionDetector(tileMap, collisionTable, arena);
        this.pathFollower = new PathFollower(tileMap, collisionTable, arena);
        this.movement = new PlayerMovement(moveInput, targetInput, collisionDetector, pathFollower);
        
        setX(Config.getPlayerDefaultX());
        setY(Config.getPlayerDefaultY());
        setSpeed(Config.getPlayerDefaultSpeed());
        setDirection(Direction.DOWN);
        
        movement.setInitialPosition(getX(), getY());
        
        assignTeam();
    }
    
    private void assignTeam() {
        double playerX = getX();
        double playerY = getY();
        
        double closestBlueDist = Double.MAX_VALUE;
        Tour closestBlueTower = null;
        
        for (Tour tour : arena.tours()) {
            if (tour.equipe().couleur() == TeamColor.BLUE) {
                double dx = tour.position().x() * Config.getTileSize() - playerX;
                double dy = tour.position().y() * Config.getTileSize() - playerY;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < closestBlueDist) {
                    closestBlueDist = dist;
                    closestBlueTower = tour;
                }
            }
        }
        
        if (closestBlueTower != null) {
            this.equipe = closestBlueTower.equipe();
        } else {
            this.equipe = arena.tours().get(0).equipe();
        }
    }
    
    public Equipe equipe() {
        return equipe;
    }
    
    public int level() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
    
    public Stats stats() {
        return stats;
    }
    
    public boolean isInFountain() {
        if (equipe() == null || arena == null) return false;
        int tileSize = Core.Config.getTileSize();
        int tileX = (int)(getX() / tileSize);
        int tileY = (int)(getY() / tileSize);
        if (tileY < 0 || tileY >= tileMap.getRows() || tileX < 0 || tileX >= tileMap.getColumns()) return false;
        if (tileMap.getTileAt(tileY, tileX) != 1) return false; // not wood
        
        Vec2 myAncient = (equipe().couleur() == TeamColor.BLUE) ? arena.getBlueAncient() : arena.getRedAncient();
        Vec2 enemyAncient = (equipe().couleur() == TeamColor.BLUE) ? arena.getRedAncient() : arena.getBlueAncient();
        if (myAncient == null || enemyAncient == null) return false;
        
        double distToOwn = Math.hypot(tileX - myAncient.x(), tileY - myAncient.y());
        double distToEnemy = Math.hypot(tileX - enemyAncient.x(), tileY - enemyAncient.y());
        double margin = 2.0;
        return distToOwn < distToEnemy - margin;
    }
    
    public boolean isOnEnemyWood() {
        if (equipe() == null || arena == null) return false;
        int tileSize = Core.Config.getTileSize();
        int tileX = (int)(getX() / tileSize);
        int tileY = (int)(getY() / tileSize);
        if (tileY < 0 || tileY >= tileMap.getRows() || tileX < 0 || tileX >= tileMap.getColumns()) return false;
        if (tileMap.getTileAt(tileY, tileX) != 1) return false; // not wood
        
        Vec2 myAncient = (equipe().couleur() == TeamColor.BLUE) ? arena.getBlueAncient() : arena.getRedAncient();
        Vec2 enemyAncient = (equipe().couleur() == TeamColor.BLUE) ? arena.getRedAncient() : arena.getBlueAncient();
        if (myAncient == null || enemyAncient == null) return false;
        
        double distToOwn = Math.hypot(tileX - myAncient.x(), tileY - myAncient.y());
        double distToEnemy = Math.hypot(tileX - enemyAncient.x(), tileY - enemyAncient.y());
        double margin = 2.0;
        return distToEnemy < distToOwn - margin;
    }
    
    public boolean estMorte() {
        return stats.isDead();
    }
    
    public boolean isAlive() {
        return isAlive;
    }
    
    public double getRespawnTimeRemaining() {
        if (isAlive || respawnEndTimeNanos <= 0) return 0;
        long currentNanos = System.nanoTime();
        return Math.max(0, (respawnEndTimeNanos - currentNanos) / 1e9);
    }
    
    public void subirDegats(int rawDamage) {
        if (!isAlive) return; // Can't take damage while dead
        stats.takeDamage(Math.max(0, rawDamage));
        if (stats.isDead() && isAlive) {
            handleDeath();
        }
    }
    
    private void handleDeath() {
        isAlive = false;
        deathTimeNanos = System.nanoTime();
        respawnEndTimeNanos = deathTimeNanos + calculateRespawnTimeNanos();
    }
    
    private long calculateRespawnTimeNanos() {
        // Base respawn time in seconds based on level
        double baseRespawnSeconds = getBaseRespawnTimeFromLevel(level);
        
        // Apply modifiers (match duration not yet implemented, talents/items not yet)
        double finalRespawnSeconds = baseRespawnSeconds;
        
        return (long) (finalRespawnSeconds * 1e9);
    }
    
    private double getBaseRespawnTimeFromLevel(int level) {
        // Respawn times based on level ranges:
        // Level 1-5: 5-15 seconds
        // Level 6-10: 20-35 seconds
        // Level 11-15: 40-60 seconds
        // Level 16-20: 70-100 seconds
        // Level 21-25: 100-120 seconds
        if (level >= 1 && level <= 5) {
            return 5.0 + (level - 1) * 2.5; // 5, 7.5, 10, 12.5, 15
        } else if (level >= 6 && level <= 10) {
            return 20.0 + (level - 6) * 3.0; // 20, 23, 26, 29, 32
        } else if (level >= 11 && level <= 15) {
            return 40.0 + (level - 11) * 4.0; // 40, 44, 48, 52, 56
        } else if (level >= 16 && level <= 20) {
            return 70.0 + (level - 16) * 6.0; // 70, 76, 82, 88, 94
        } else if (level >= 21 && level <= 25) {
            return 100.0 + Math.min((level - 21) * 4.0, 20.0); // 100-120
        } else {
            // Default for higher levels: cap at 120s
            return 120.0;
        }
    }
    
    private void handleRespawn() {
        isAlive = true;
        // Restore health and mana
        stats.heal(stats.maxHp());
        stats.restoreMana(stats.maxMana());
        // Reset position to team fountain (spawn point)
        Equipe team = equipe();
        if (team != null && team.fontaine() != null) {
            // Fountain position is in tile coordinates, convert to pixels
            Vec2 fountainPos = team.fontaine().position();
            int tileSize = Core.Config.getTileSize();
            setX(fountainPos.x() * tileSize);
            setY(fountainPos.y() * tileSize);
        }
    }
    
    public void update() {
        if (!isAlive) {
            long currentNanos = System.nanoTime();
            if (currentNanos >= respawnEndTimeNanos) {
                handleRespawn();
            }
            return; // Skip movement update while dead
        }
        
        int tileSize = Core.Config.getTileSize();
        int tileX = (int)(getX() / tileSize);
        int tileY = (int)(getY() / tileSize);
        
        // Ensure within map bounds
        if (tileY >= 0 && tileY < tileMap.getRows() && tileX >= 0 && tileX < tileMap.getColumns()) {
            int tileId = tileMap.getTileAt(tileY, tileX);
            
            if (tileId == 1) { // Wood floor (fountain area)
                if (equipe() != null && arena != null) {
                    Vec2 myAncient = (equipe().couleur() == TeamColor.BLUE) ? arena.getBlueAncient() : arena.getRedAncient();
                    Vec2 enemyAncient = (equipe().couleur() == TeamColor.BLUE) ? arena.getRedAncient() : arena.getBlueAncient();
                    
                    if (myAncient != null && enemyAncient != null) {
                        double distToOwn = Math.hypot(tileX - myAncient.x(), tileY - myAncient.y());
                        double distToEnemy = Math.hypot(tileX - enemyAncient.x(), tileY - enemyAncient.y());
                        double margin = 2.0; // tile tolerance
                        
                        if (distToOwn < distToEnemy - margin) {
                            // Friendly wood - heal and mana regen
                            double deltaSeconds = 1.0 / 60.0; // Match game loop
                            equipe().fontaine().regen(stats, deltaSeconds);
                        } else if (distToEnemy < distToOwn - margin) {
                            // Enemy wood - take damage every 200ms (3x tower damage = 60)
                            long currentNanos = System.nanoTime();
                            if (currentNanos - lastWoodDamageTimeNanos >= WOOD_DAMAGE_INTERVAL_NANOS) {
                                subirDegats(60);
                                lastWoodDamageTimeNanos = currentNanos;
                            }
                        }
                    }
                }
            }
        }
        
        movement.update(this);
    }
    
    private boolean checkFountainProximity() {
        if (equipe() == null || equipe().fontaine() == null) return false;
        
        Vec2 fountainPos = equipe().fontaine().position();
        int tileSize = Core.Config.getTileSize();
        double fountainCenterX = fountainPos.x() * tileSize + tileSize / 2.0;
        double fountainCenterY = fountainPos.y() * tileSize + tileSize / 2.0;
        
        double playerCenterX = getX() + tileSize / 2.0;
        double playerCenterY = getY() + tileSize / 2.0;
        
        double dx = playerCenterX - fountainCenterX;
        double dy = playerCenterY - fountainCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Player is in fountain if within 1.5 tiles of fountain center
        return distance < tileSize * 1.5;
    }
    
    // Getter methods for hero-specific data
    public int getCharacterRow() {
        return characterRow;
    }
    
    public int getHairRow() {
        return hairRow;
    }
    
    public String getOutfitFile() {
        return outfitFile;
    }
    
    public Integer getSuitRow() {
        return suitRow;
    }
    
    public Hero getHero() {
        return hero;
    }
}
