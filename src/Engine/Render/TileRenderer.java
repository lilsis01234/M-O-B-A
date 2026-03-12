package Engine.Render;

import Core.Config;
import Core.Tile.TileMap;
import Engine.Tile.Tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

public class TileRenderer {

    private final TileMap tileMap;
    private final Tile[] tiles;

    public TileRenderer(TileMap tileMap, Tile[] tiles) {
        this.tileMap = tileMap;
        this.tiles = tiles;
    }

    public void draw(Graphics2D g2, Camera camera, int panelWidth, int panelHeight) {
        int[] range = calculateVisibleRange(camera, panelWidth, panelHeight);
        int startCol = range[0];
        int startRow = range[1];
        int endCol = range[2];
        int endRow = range[3];

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                drawTile(g2, row, col);
            }
        }
    }

    private int[] calculateVisibleRange(Camera camera, int panelWidth, int panelHeight) {
        int tileSize = Config.getTileSize();
        float zoom = camera.getZoom();
        float startX = camera.getX();
        float startY = camera.getY();
        float endX = startX + (panelWidth / zoom);
        float endY = startY + (panelHeight / zoom);

        int startCol = Math.max(0, (int) (startX / tileSize));
        int startRow = Math.max(0, (int) (startY / tileSize));
        int endCol = Math.min(tileMap.getColumns(), (int) (endX / tileSize) + 1);
        int endRow = Math.min(tileMap.getRows(), (int) (endY / tileSize) + 1);

        return new int[]{startCol, startRow, endCol, endRow};
    }

    private void drawTile(Graphics2D g2, int row, int col) {
        int tileSize = Config.getTileSize();
        int tileId = tileMap.getTileAt(row, col);
        int x = col * tileSize;
        int y = row * tileSize;

        if (tileId < 0 || tileId >= tiles.length || tiles[tileId] == null) {
            drawDefaultTile(g2, x, y, tileSize);
            return;
        }

        Tile tile = tiles[tileId];
        Image image = getTileImage(tile, tileId, row, col);
        
        if (image != null) {
            g2.drawImage(image, x, y, tileSize, tileSize, null);
        } else if (tile.getColor() != null) {
            g2.setColor(tile.getColor());
            g2.fillRect(x, y, tileSize, tileSize);
        } else {
            drawDefaultTile(g2, x, y, tileSize);
        }
    }

    private Image getTileImage(Tile tile, int tileId, int row, int col) {
        if (tileId == 5 && tile.getImages().size() > 1) {
            return getAnimatedWaterTile(tile, row, col);
        }
        return tile.getImage();
    }

    private Image getAnimatedWaterTile(Tile tile, int row, int col) {
        long timeInSeconds = System.currentTimeMillis() / 3000;
        int seed = row * 73 + col * 37 + (int) timeInSeconds;
        int index = Math.abs(new java.util.Random(seed).nextInt()) % tile.getImages().size();
        return tile.getImages().get(index);
    }

    private void drawDefaultTile(Graphics2D g2, int x, int y, int tileSize) {
        g2.setColor(Color.black);
        g2.fillRect(x, y, tileSize, tileSize);
    }
}
