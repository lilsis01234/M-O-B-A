package Core.Tile;

/**
 * Cette classe représente la grille de tuiles qui compose la carte du jeu
 * @author BOUKIRAT Thafat
 * @version 1.0
 */
public class TileMap {
    private final int[][] tileNumbers;
    private final int columns;
    private final int rows;

    public TileMap(int[][] tileNumbers, int columns, int rows) {
        this.tileNumbers = tileNumbers;
        this.columns = columns;
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getTileAt(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < columns) {
            return tileNumbers[row][col];
        }
        return -1;
    }

    public void setTileAt(int row, int col, int tileId) {
        if (row >= 0 && row < rows && col >= 0 && col < columns) {
            tileNumbers[row][col] = tileId;
        }
    }
}

