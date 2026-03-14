package Core.Tile;

/**
 * Cette classe représente les collisions
 * @author RAHARIMANANA Tianantenaina
 * @version 1.0
 */
public class CollisionTable {
    private final boolean[] collisionByTileId;

    public CollisionTable(boolean[] collisionByTileId) {
        this.collisionByTileId = collisionByTileId;
    }

    /**
     * Vérifie si une tuile bloque
     * @param tileId la tuile à tester
     * @return true si la tuile possède une collision, false sinon
     */
    public boolean hasCollision(int tileId) {
        return tileId >= 0
                && tileId < collisionByTileId.length
                && collisionByTileId[tileId];
    }
}