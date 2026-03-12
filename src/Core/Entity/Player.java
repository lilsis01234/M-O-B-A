package Core.Entity;

import Core.Config;
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
    private int level = 1;
    
    // Death and respawn tracking
    private boolean isAlive = true;
    private long deathTimeNanos = 0;
    private long respawnEndTimeNanos = 0;
    
    public Player(MoveInput moveInput, TargetInput targetInput, 
                  TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        this.moveInput = moveInput;
        this.targetInput = targetInput;
        this.arena = arena;
        
        this.collisionDetector = new CollisionDetector(tileMap, collisionTable, arena);
        this.pathFollower = new PathFollower(tileMap, collisionTable, arena);
        this.movement = new PlayerMovement(moveInput, targetInput, collisionDetector, pathFollower);
        
        this.stats = new Stats(250, 200, 15, 10, 200.0);
        
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
        return checkFountainProximity();
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
        
        // Check if player is in fountain for regeneration
        if (isInFountain() && equipe() != null && equipe().fontaine() != null) {
            double deltaSeconds = 1.0 / 60.0; // Match game loop
            equipe().fontaine().regen(stats, deltaSeconds);
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
}
