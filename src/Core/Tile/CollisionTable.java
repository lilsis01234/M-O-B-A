package Core.Tile;

public class CollisionTable {
    private final boolean[] collisionByTileId;

    public CollisionTable(boolean[] collisionByTileId) {
        this.collisionByTileId = collisionByTileId;
    }

    public boolean hasCollision(int tileId) {
        return tileId >= 0
                && tileId < collisionByTileId.length
                && collisionByTileId[tileId];
    }
}

