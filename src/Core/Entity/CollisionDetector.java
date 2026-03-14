package Core.Entity;

import Core.Config;
import Core.Moba.Units.Ancient;
import Core.Moba.Units.Tour;
import Core.Moba.World.Arena;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;

public class CollisionDetector {

    private final TileMap tileMap;
    private final CollisionTable collisionTable;
    private final Arena arena;

    public CollisionDetector(TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        this.tileMap = tileMap;
        this.collisionTable = collisionTable;
        this.arena = arena;
    }

    public boolean collidesAt(double topLeftX, double topLeftY) {
        return collidesWithTile(topLeftX, topLeftY) || 
               (arena != null && collidesWithStructures(topLeftX, topLeftY));
    }

    private boolean collidesWithTile(double topLeftX, double topLeftY) {
        HitboxUtils.Hitbox collisionBox = HitboxUtils.createEntityCollisionBox(topLeftX, topLeftY);
        
        return isWallAt(collisionBox.getLeft(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getLeft(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getCenterX(), collisionBox.getCenterY());
    }

    private boolean isWallAt(double pixelX, double pixelY) {
        int tileSize = Config.getTileSize();
        int col = (int) Math.floor(pixelX / tileSize);
        int row = (int) Math.floor(pixelY / tileSize);
        
        if (col < 0 || col >= tileMap.getColumns() || row < 0 || row >= tileMap.getRows()) {
            return true;
        }
        
        int tileId = tileMap.getTileAt(row, col);
        return collisionTable.hasCollision(tileId);
    }

    private boolean collidesWithStructures(double topLeftX, double topLeftY) {
        HitboxUtils.Hitbox entityCollisionBox = HitboxUtils.createEntityCollisionBox(topLeftX, topLeftY);

        for (Tour tower : arena.tours()) {
            HitboxUtils.Hitbox towerCollisionBox = HitboxUtils.createTowerCollisionBox(
                tower.position().x(), tower.position().y(), tower.width(), tower.height());
            if (HitboxUtils.aabbIntersects(entityCollisionBox, towerCollisionBox)) {
                return true;
            }
        }

        for (Ancient ancient : arena.ancients()) {
            HitboxUtils.Hitbox ancientCollisionBox = HitboxUtils.createAncientCollisionBox(
                ancient.position().x(), ancient.position().y(), ancient.width(), ancient.height());
            if (HitboxUtils.aabbIntersects(entityCollisionBox, ancientCollisionBox)) {
                return true;
            }
        }

        return false;
    }

    public boolean isPathClear(double x1, double y1, double x2, double y2) {
        double distance = MathUtils.distance(x1, y1, x2, y2);
        if (distance < 1) return true;

        double pathClearInset = HitboxUtils.HITBOX_INSET + 2.0;
        int steps = (int) Math.max(1, distance / (Config.getTileSize() / 8.0));

        for (int i = 1; i <= steps; i++) {
            double ratio = (double) i / steps;
            double checkX = x1 + (x2 - x1) * ratio;
            double checkY = y1 + (y2 - y1) * ratio;
            if (collidesAtPathCheck(checkX, checkY, pathClearInset)) {
                return false;
            }
        }
        return true;
    }

    private boolean collidesAtPathCheck(double topLeftX, double topLeftY, double inset) {
        HitboxUtils.Hitbox collisionBox = HitboxUtils.createEntityCollisionBox(topLeftX, topLeftY);
        
        return isWallAt(collisionBox.getLeft(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getLeft(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getCenterX(), collisionBox.getCenterY()) ||
               (arena != null && collidesWithStructuresAt(topLeftX, topLeftY));
    }

    private boolean collidesWithStructuresAt(double topLeftX, double topLeftY) {
        HitboxUtils.Hitbox entityCollisionBox = HitboxUtils.createEntityCollisionBox(topLeftX, topLeftY);

        for (Tour tower : arena.tours()) {
            HitboxUtils.Hitbox towerCollisionBox = HitboxUtils.createTowerCollisionBox(
                tower.position().x(), tower.position().y(), tower.width(), tower.height());
            if (HitboxUtils.aabbIntersects(entityCollisionBox, towerCollisionBox)) {
                return true;
            }
        }

        for (Ancient ancient : arena.ancients()) {
            HitboxUtils.Hitbox ancientCollisionBox = HitboxUtils.createAncientCollisionBox(
                ancient.position().x(), ancient.position().y(), ancient.width(), ancient.height());
            if (HitboxUtils.aabbIntersects(entityCollisionBox, ancientCollisionBox)) {
                return true;
            }
        }

        return false;
    }
}
