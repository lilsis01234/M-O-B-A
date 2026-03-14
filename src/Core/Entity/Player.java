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
 * Classe fonctionnelle pour le Player
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia
 */
public class Player extends Entity {
    
    private final MoveInput moveInput;
    private final TargetInput targetInput;
    private final CollisionDetector collisionDetector;
    private final PathFollower pathFollower;
    private final PlayerMovement movement;
    private final Stats stats;
    private final TileMap tileMap;
    private Arena arena;
    private Equipe team;
    
    private int level = 1;
    
    private final Hero hero;
    private final int characterRow;
    private final int hairRow;
    private final String outfitFile;
    private final Integer suitRow;
    
    private boolean isAlive = true;
    private long respawnEndTimeNanos = 0;
    private boolean justRespawned = false;
    
    private long lastWoodDamageTimeNanos = 0;
    private static final long WOOD_DAMAGE_INTERVAL_NANOS = 200_000_000;
    
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
        
        this.stats = createStats(hero);
        this.characterRow = getCharacterRowFromHero(hero);
        this.hairRow = getHairRowFromHero(hero);
        this.outfitFile = getOutfitFromHero(hero);
        this.suitRow = getSuitRowFromHero(hero);
        
        this.collisionDetector = new CollisionDetector(tileMap, collisionTable, arena);
        this.pathFollower = new PathFollower(tileMap, collisionTable, arena);
        this.movement = new PlayerMovement(moveInput, targetInput, collisionDetector, pathFollower);
        
        initializePosition();
        movement.setInitialPosition(getX(), getY());
        
        assignTeam();
    }

    private Stats createStats(Hero hero) {
        if (hero != null) {
            Stats stats = new Stats(hero.getMaxHp(), hero.getMaxMana(), hero.getAttack(), 
                          hero.getDefense(), hero.getAttackSpeed());
            stats.takeDamage(hero.getMaxHp() - hero.getBaseHp());
            return stats;
        }
        return new Stats(250, 200, 15, 10, 200.0);
    }

    private int getCharacterRowFromHero(Hero hero) {
        return hero != null ? hero.getCharacterRow() : 0;
    }

    private int getHairRowFromHero(Hero hero) {
        return hero != null ? hero.getHairRow() : 0;
    }

    private String getOutfitFromHero(Hero hero) {
        return hero != null ? hero.getOutfitFile() : "Outfit1.png";
    }

    private Integer getSuitRowFromHero(Hero hero) {
        return hero != null ? hero.getSuitRow() : null;
    }

    private void initializePosition() {
        setX(Config.getPlayerDefaultX());
        setY(Config.getPlayerDefaultY());
        setSpeed((int) getSpeedFromHero(hero));
        setDirection(Direction.DOWN);
    }
    
    private double getSpeedFromHero(Hero hero) {
        if (hero != null && hero.getSpeed() > 0) {
            return hero.getSpeed();
        }
        return (double) Config.getPlayerDefaultSpeed();
    }

    private void assignTeam() {
        Tour closestTower = findClosestTeamTower();
        this.team = closestTower != null ? closestTower.equipe() : arena.tours().get(0).equipe();
    }

    private Tour findClosestTeamTower() {
        double closestDist = Double.MAX_VALUE;
        Tour closest = null;
        
        for (Tour tower : arena.tours()) {
            if (tower.equipe().couleur() == TeamColor.BLUE) {
                double dist = calculateDistanceToTower(tower);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = tower;
                }
            }
        }
        return closest;
    }

    private double calculateDistanceToTower(Tour tower) {
        double dx = tower.position().x() * Config.getTileSize() - getX();
        double dy = tower.position().y() * Config.getTileSize() - getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public Equipe equipe() {
        return team;
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
        if (!hasValidTeamOrArena()) return false;
        if (!isOnWoodTile()) return false;
        
        Vec2 myAncient = getMyAncientPosition();
        Vec2 enemyAncient = getEnemyAncientPosition();
        if (myAncient == null || enemyAncient == null) return false;
        
        return isCloserToAncient(myAncient, enemyAncient);
    }
    
    public boolean isOnEnemyWood() {
        if (!hasValidTeamOrArena()) return false;
        if (!isOnWoodTile()) return false;
        
        Vec2 myAncient = getMyAncientPosition();
        Vec2 enemyAncient = getEnemyAncientPosition();
        if (myAncient == null || enemyAncient == null) return false;
        
        return isCloserToAncient(enemyAncient, myAncient);
    }

    private boolean hasValidTeamOrArena() {
        return team != null && arena != null;
    }

    private boolean isOnWoodTile() {
        int tileX = getTileCoordinate(getX());
        int tileY = getTileCoordinate(getY());
        if (!isValidTile(tileX, tileY)) return false;
        return tileMap.getTileAt(tileY, tileX) == 1;
    }

    private int getTileCoordinate(double pixel) {
        return (int)(pixel / Config.getTileSize());
    }

    private boolean isValidTile(int tileX, int tileY) {
        return tileY >= 0 && tileY < tileMap.getRows() 
            && tileX >= 0 && tileX < tileMap.getColumns();
    }

    private Vec2 getMyAncientPosition() {
        return team.couleur() == TeamColor.BLUE 
            ? arena.getBlueAncient() 
            : arena.getRedAncient();
    }

    private Vec2 getEnemyAncientPosition() {
        return team.couleur() == TeamColor.BLUE 
            ? arena.getRedAncient() 
            : arena.getBlueAncient();
    }

    private boolean isCloserToAncient(Vec2 closer, Vec2 farther) {
        int tileX = getTileCoordinate(getX());
        int tileY = getTileCoordinate(getY());
        double distToCloser = Math.hypot(tileX - closer.x(), tileY - closer.y());
        double distToFarther = Math.hypot(tileX - farther.x(), tileY - farther.y());
        return distToCloser < distToFarther - 2.0;
    }
    
    public boolean estMorte() {
        return stats.isDead();
    }
    
    public boolean isAlive() {
        return isAlive;
    }
    
    public double getRespawnTimeRemaining() {
        if (isAlive || respawnEndTimeNanos <= 0) return 0;
        return Math.max(0, (respawnEndTimeNanos - System.nanoTime()) / 1e9);
    }
    
    public void subirDegats(int rawDamage) {
        if (!isAlive) return;
        stats.takeDamage(Math.max(0, rawDamage));
        if (stats.isDead()) {
            handleDeath();
        }
    }
    
    private void handleDeath() {
        isAlive = false;
        respawnEndTimeNanos = System.nanoTime() + calculateRespawnTimeNanos();
    }
    
    private long calculateRespawnTimeNanos() {
        double baseSeconds = getBaseRespawnTimeFromLevel(level);
        return (long) (baseSeconds * 1e9);
    }
    
    private double getBaseRespawnTimeFromLevel(int lvl) {
        if (lvl >= 1 && lvl <= 5) return 5.0 + (lvl - 1) * 2.5;
        if (lvl >= 6 && lvl <= 10) return 20.0 + (lvl - 6) * 3.0;
        if (lvl >= 11 && lvl <= 15) return 40.0 + (lvl - 11) * 4.0;
        if (lvl >= 16 && lvl <= 20) return 70.0 + (lvl - 16) * 6.0;
        if (lvl >= 21 && lvl <= 25) return 100.0 + Math.min((lvl - 21) * 4.0, 20.0);
        return 120.0;
    }
    
    private void handleRespawn() {
        isAlive = true;
        justRespawned = true;
        stats.heal(stats.maxHp());
        stats.restoreMana(stats.maxMana());
        respawnAtFountain();
    }
    
    public boolean justRespawned() {
        return justRespawned;
    }
    
    public void clearJustRespawned() {
        justRespawned = false;
    }
    
    public boolean isMovingWithWASD() {
        return moveInput.isAnyKeyPressed();
    }

    private void respawnAtFountain() {
        if (team != null && team.fontaine() != null) {
            Vec2 fountainPos = team.fontaine().position();
            int tileSize = Config.getTileSize();
            setX(fountainPos.x() * tileSize);
            setY(fountainPos.y() * tileSize);
        }
    }
    
    public PlayerMovement getMovement() {
        return movement;
    }
    
    public void setMovementTarget(double x, double y) {
        movement.setTarget(x, y);
    }
    
    public void update() {
        if (!isAlive) {
            checkRespawnTimer();
            return;
        }
        
        updateFountainEffects();
        movement.update(this);
    }

    private void checkRespawnTimer() {
        if (System.nanoTime() >= respawnEndTimeNanos) {
            handleRespawn();
        }
    }

    private void updateFountainEffects() {
        if (!isOnWoodTile()) return;
        
        Vec2 myAncient = getMyAncientPosition();
        Vec2 enemyAncient = getEnemyAncientPosition();
        if (myAncient == null || enemyAncient == null) return;

        int tileX = getTileCoordinate(getX());
        int tileY = getTileCoordinate(getY());
        double distToOwn = Math.hypot(tileX - myAncient.x(), tileY - myAncient.y());
        double distToEnemy = Math.hypot(tileX - enemyAncient.x(), tileY - enemyAncient.y());
        
        if (distToOwn < distToEnemy - 2.0) {
            applyFountainHealing();
        } else if (distToEnemy < distToOwn - 2.0) {
            applyEnemyWoodDamage();
        }
    }

    private void applyFountainHealing() {
        double deltaSeconds = 1.0 / 60.0;
        team.fontaine().regen(stats, deltaSeconds);
    }

    private void applyEnemyWoodDamage() {
        long currentNanos = System.nanoTime();
        if (currentNanos - lastWoodDamageTimeNanos >= WOOD_DAMAGE_INTERVAL_NANOS) {
            subirDegats(60);
            lastWoodDamageTimeNanos = currentNanos;
        }
    }
    
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

    private int gold = 0;
    
    public int getGold() {
        return gold;
    }
    
    public void addGold(int amount) {
        gold += amount;
    }
    
    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    private Object selectedTarget;
    
    public boolean hasSelectedTarget() {
        return selectedTarget != null;
    }
    
    public Object getSelectedTarget() {
        return selectedTarget;
    }
    
    public void setSelectedTarget(Object target) {
        this.selectedTarget = target;
    }
}
