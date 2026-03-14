package Core.Entity;

/**
 * Responsable de la détection des collisions entre les entités et l'environnement.
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia
 * */

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

    /**
     * Vérifie si une collision existe à une position donnée pour une entité.
     * @param topLeftX Coordonnée X du coin supérieur gauche.
     * @param topLeftY Coordonnée Y du coin supérieur gauche.
     * @return true si la position est occupée par un obstacle ou une structure.
     */
    public boolean collidesAt(double topLeftX, double topLeftY) {
        return collidesWithTile(topLeftX, topLeftY) || 
               (arena != null && collidesWithStructures(topLeftX, topLeftY));
    }

    /**
     * Vérifie les collisions avec les tuiles de la carte en testant les coins et le centre de la hitbox.
     * @param topLeftX Coordonnée X.
     * @param topLeftY Coordonnée Y.
     * @return true si un des points de contrôle touche un mur.
     */
    private boolean collidesWithTile(double topLeftX, double topLeftY) {
        HitboxUtils.Hitbox collisionBox = HitboxUtils.createEntityCollisionBox(topLeftX, topLeftY);
        
        return isWallAt(collisionBox.getLeft(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getLeft(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getCenterX(), collisionBox.getCenterY());
    }

    /**
     * Détermine si un point précis est un mur
     * @param pixelX Position X en pixels.
     * @param pixelY Position Y en pixels.
     * @return true si la tuile est solide ou hors des limites de la carte.
     */
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

    /**
     * Vérifie si l'entité entre en collision avec les Tours ou l'Ancien.
     * @param topLeftX Coordonnée X.
     * @param topLeftY Coordonnée Y.
     * @return true si une collision est détectée avec une structure.
     */
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

    /**
     * Vérifie si le chemin ne contient pas d'obstacle
     * @param x1 Point de départ X.
     * @param y1 Point de départ Y.
     * @param x2 Point d'arrivée X.
     * @param y2 Point d'arrivée Y.
     * @return true si aucun obstacle n'est détecté sur le trajet.
     */
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
    /**
     * Vérifier le trajet.
     */
    private boolean collidesAtPathCheck(double topLeftX, double topLeftY, double inset) {
        HitboxUtils.Hitbox collisionBox = HitboxUtils.createEntityCollisionBox(topLeftX, topLeftY);
        
        return isWallAt(collisionBox.getLeft(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getTop()) ||
               isWallAt(collisionBox.getLeft(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getRight(), collisionBox.getBottom()) ||
               isWallAt(collisionBox.getCenterX(), collisionBox.getCenterY()) ||
               (arena != null && collidesWithStructuresAt(topLeftX, topLeftY));
    }

    /**
     * Vérifie les structures lors du test de trajet.
     */
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
