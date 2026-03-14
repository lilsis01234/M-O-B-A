package Core.Entity;

import Core.Config;

/**
 * Utilitaires pour les conversions entre pixels et tuiles.
 */
public class TileUtils {

    public static int pixelToTileX(double pixelX) {
        return (int) (pixelX / Config.getTileSize());
    }

    public static int pixelToTileY(double pixelY) {
        return (int) (pixelY / Config.getTileSize());
    }

    public static double tileToPixelX(int tileX) {
        return tileX * Config.getTileSize();
    }

    public static double tileToPixelY(int tileY) {
        return tileY * Config.getTileSize();
    }

    public static double getTileCenterX(int tileX) {
        return tileX * Config.getTileSize() + Config.getTileSize() / 2.0;
    }

    public static double getTileCenterY(int tileY) {
        return tileY * Config.getTileSize() + Config.getTileSize() / 2.0;
    }

    public static double getPixelCenter(double pixel) {
        int tile = (int) (pixel / Config.getTileSize());
        return tile * Config.getTileSize() + Config.getTileSize() / 2.0;
    }

    public static boolean isValidTile(int col, int row, int columns, int rows) {
        return col >= 0 && col < columns && row >= 0 && row < rows;
    }

    public static int getTileFromMap(double pixel, int mapSize) {
        int tile = (int) (pixel / Config.getTileSize());
        return Math.max(0, Math.min(tile, mapSize - 1));
    }

    private TileUtils() {}
}
