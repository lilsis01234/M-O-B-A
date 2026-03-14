package Core.Entity;

import Core.Config;
import Core.Match.PathFinder;
import Core.Moba.Units.Ancient;
import Core.Moba.Units.Tour;
import Core.Moba.World.Arena;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Core.Entity.HitboxUtils;

import java.util.List;

/**
 * Gère le pathfinding (recherche de chemin) et le suivi du chemin.
 * @author RAHARIMANANA Tianantenaina ZEGHBIB Sonia BOUKIRAT Thafat
 */
public class PathFollower {

    private static final double HITBOX_INSET_PX = 6.0;

    private final TileMap tileMap;
    private final CollisionTable collisionTable;
    private final Arena arena;
    private final PathFinder pathFinder;

    private List<int[]> currentPath = null;
    private int currentPathIndex = 0;

    public PathFollower(TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        this.tileMap = tileMap;
        this.collisionTable = collisionTable;
        this.arena = arena;
        this.pathFinder = new PathFinder(tileMap, collisionTable);
        this.pathFinder.setArena(arena);
    }

    /**
     * Trouve un chemin entre deux tuiles.
     */
    public List<int[]> findPath(int startCol, int startRow, int targetCol, int targetRow) {
        return pathFinder.findPath(startCol, startRow, targetCol, targetRow);
    }

    public void setPath(List<int[]> path) {
        this.currentPath = path;
    }

    public void clearPath() {
        this.currentPath = null;
        this.currentPathIndex = 0;
    }

    public boolean hasPath() {
        return currentPath != null && currentPathIndex < currentPath.size();
    }

    /**
     * Lisse le chemin en supprimant les nodes inutiles.
     */
    public void smoothPath(CollisionDetector collisionDetector) {
        if (currentPath == null || currentPath.size() <= 2) return;

        int tileSize = Config.getTileSize();
        int i = 0;
        while (i < currentPath.size() - 2) {
            int[] start = currentPath.get(i);
            int[] nextNext = currentPath.get(i + 2);

            double x1 = start[0] * tileSize;
            double y1 = start[1] * tileSize;
            double x2 = nextNext[0] * tileSize;
            double y2 = nextNext[1] * tileSize;

            if (isPathClear(x1, y1, x2, y2, collisionDetector)) {
                currentPath.remove(i + 1);
            } else {
                i++;
            }
        }
    }

    private boolean isPathClear(double x1, double y1, double x2, double y2, CollisionDetector collisionDetector) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 1) return true;

        double pathClearInset = HITBOX_INSET_PX + 2.0;
        int steps = (int) Math.max(1, distance / (Config.getTileSize() / 8.0));
        
        for (int i = 1; i <= steps; i++) {
            double ratio = (double) i / steps;
            if (collidesAtCustom(x1 + dx * ratio, y1 + dy * ratio, pathClearInset)) {
                return false;
            }
        }
        return true;
    }

    private boolean collidesAtCustom(double topLeftX, double topLeftY, double inset) {
        int tileSize = Config.getTileSize();
        double left = topLeftX + inset;
        double top = topLeftY + inset;
        double right = topLeftX + tileSize - inset;
        double bottom = topLeftY + tileSize - inset;

        double centerX = (left + right) / 2;
        double centerY = (top + bottom) / 2;

        if (isWallAt(left, top) || isWallAt(right, top)
                || isWallAt(left, bottom) || isWallAt(right, bottom)
                || isWallAt(centerX, centerY)) {
            return true;
        }

        return checkTowerCollision(topLeftX, topLeftY, inset);
    }

    private boolean isWallAt(double pixelX, double pixelY) {
        int tileSize = Config.getTileSize();
        int col = (int) Math.floor(pixelX / tileSize);
        int row = (int) Math.floor(pixelY / tileSize);
        int tileId = tileMap.getTileAt(row, col);
        return collisionTable.hasCollision(tileId);
    }

    private boolean checkTowerCollision(double topLeftX, double topLeftY, double inset) {
        int tileSize = Config.getTileSize();
        double left = topLeftX + inset;
        double top = topLeftY + inset;
        double right = topLeftX + tileSize - inset;
        double bottom = topLeftY + tileSize - inset;

        for (Tour tower : arena.tours()) {
            HitboxUtils.Hitbox towerCollisionBox = HitboxUtils.createTowerCollisionBox(
                tower.position().x(), tower.position().y(), tower.width(), tower.height());
            
            if (right > towerCollisionBox.getLeft() && left < towerCollisionBox.getRight()
                    && bottom > towerCollisionBox.getTop() && top < towerCollisionBox.getBottom()) {
                return true;
            }
        }

        for (Ancient ancient : arena.ancients()) {
            HitboxUtils.Hitbox ancientCollisionBox = HitboxUtils.createAncientCollisionBox(
                ancient.position().x(), ancient.position().y(), ancient.width(), ancient.height());

            if (right > ancientCollisionBox.getLeft() && left < ancientCollisionBox.getRight()
                    && bottom > ancientCollisionBox.getTop() && top < ancientCollisionBox.getBottom()) {
                return true;
            }
        }

        return false;
    }

    public void advancePath() {
        if (currentPath != null) {
            currentPathIndex++;
        }
    }

    public void setPathIndex(int index) {
        this.currentPathIndex = index;
    }

    public void setNextPathTarget() {
        if (currentPath != null && currentPathIndex < currentPath.size()) {
            int[] tile = currentPath.get(currentPathIndex);
        }
    }

    public int[] getCurrentPathTarget() {
        if (currentPath != null && currentPathIndex < currentPath.size()) {
            return currentPath.get(currentPathIndex);
        }
        return null;
    }
}
