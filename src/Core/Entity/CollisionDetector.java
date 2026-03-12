package Core.Entity;

import Core.Config;
import Core.Moba.Units.Ancient;
import Core.Moba.Units.Tour;
import Core.Moba.World.Arena;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;

/**
 * Détecte les collisions entre les entités et le monde.
 */
public class CollisionDetector {

    private static final double HITBOX_INSET_PX = 6.0;

    private final TileMap tileMap;
    private final CollisionTable collisionTable;
    private final Arena arena;

    public CollisionDetector(TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        this.tileMap = tileMap;
        this.collisionTable = collisionTable;
        this.arena = arena;
    }

    /**
     * Vérifie si une position est en collision.
     */
    public boolean collidesAt(double topLeftX, double topLeftY) {
        return collidesWithTile(topLeftX, topLeftY) || 
               (arena != null && collidesWithTowers(topLeftX, topLeftY));
    }

    private boolean collidesWithTile(double topLeftX, double topLeftY) {
        int tileSize = Config.getTileSize();
        HitboxUtils.Hitbox hitbox = HitboxUtils.createEntityHitbox(topLeftX, topLeftY);
        
        return isWallAt(hitbox.getLeft(), hitbox.getTop()) ||
               isWallAt(hitbox.getRight(), hitbox.getTop()) ||
               isWallAt(hitbox.getLeft(), hitbox.getBottom()) ||
               isWallAt(hitbox.getRight(), hitbox.getBottom()) ||
               isWallAt(hitbox.getCenterX(), hitbox.getCenterY());
    }

    private boolean isWallAt(double pixelX, double pixelY) {
        int tileSize = Config.getTileSize();
        int col = (int) Math.floor(pixelX / tileSize);
        int row = (int) Math.floor(pixelY / tileSize);
        int tileId = tileMap.getTileAt(row, col);
        return collisionTable.hasCollision(tileId);
    }

    private boolean collidesWithTowers(double topLeftX, double topLeftY) {
        HitboxUtils.Hitbox hitbox = HitboxUtils.createEntityHitbox(topLeftX, topLeftY);
        int tileSize = Config.getTileSize();

        for (Tour tower : arena.tours()) {
            HitboxUtils.Hitbox towerHitbox = new HitboxUtils.Hitbox(
                tower.position().x() * tileSize,
                tower.position().y() * tileSize,
                tower.width() * tileSize,
                tower.height() * tileSize
            );
            if (HitboxUtils.aabbIntersects(hitbox, towerHitbox)) {
                return true;
            }
        }

        for (Ancient ancient : arena.ancients()) {
            HitboxUtils.Hitbox ancientHitbox = new HitboxUtils.Hitbox(
                ancient.position().x() * tileSize,
                ancient.position().y() * tileSize,
                ancient.width() * tileSize,
                ancient.height() * tileSize
            );
            if (HitboxUtils.aabbIntersects(hitbox, ancientHitbox)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Vérifie si le chemin entre deux points est dégagé.
     */
    public boolean isPathClear(double x1, double y1, double x2, double y2) {
        double distance = MathUtils.distance(x1, y1, x2, y2);
        if (distance < 1) return true;

        double pathClearInset = HITBOX_INSET_PX + 2.0;
        int steps = (int) Math.max(1, distance / (Config.getTileSize() / 8.0));

        for (int i = 1; i <= steps; i++) {
            double ratio = (double) i / steps;
            double checkX = x1 + (x2 - x1) * ratio;
            double checkY = y1 + (y2 - y1) * ratio;
            if (collidesAtCustom(checkX, checkY, pathClearInset)) {
                return false;
            }
        }
        return true;
    }

    private boolean collidesAtCustom(double topLeftX, double topLeftY, double inset) {
        HitboxUtils.Hitbox hitbox = HitboxUtils.createEntityHitbox(topLeftX, topLeftY, inset);
        
        return isWallAt(hitbox.getLeft(), hitbox.getTop()) ||
               isWallAt(hitbox.getRight(), hitbox.getTop()) ||
               isWallAt(hitbox.getLeft(), hitbox.getBottom()) ||
               isWallAt(hitbox.getRight(), hitbox.getBottom()) ||
               isWallAt(hitbox.getCenterX(), hitbox.getCenterY()) ||
               (arena != null && collidesWithTowersCustom(topLeftX, topLeftY, inset));
    }

    private boolean collidesWithTowersCustom(double topLeftX, double topLeftY, double inset) {
        HitboxUtils.Hitbox hitbox = HitboxUtils.createEntityHitbox(topLeftX, topLeftY, inset);
        int tileSize = Config.getTileSize();

        for (Tour tower : arena.tours()) {
            HitboxUtils.Hitbox towerHitbox = new HitboxUtils.Hitbox(
                tower.position().x() * tileSize,
                tower.position().y() * tileSize,
                tower.width() * tileSize,
                tower.height() * tileSize
            );
            if (HitboxUtils.aabbIntersects(hitbox, towerHitbox)) {
                return true;
            }
        }

        for (Ancient ancient : arena.ancients()) {
            HitboxUtils.Hitbox ancientHitbox = new HitboxUtils.Hitbox(
                ancient.position().x() * tileSize,
                ancient.position().y() * tileSize,
                ancient.width() * tileSize,
                ancient.height() * tileSize
            );
            if (HitboxUtils.aabbIntersects(hitbox, ancientHitbox)) {
                return true;
            }
        }

        return false;
    }
}
