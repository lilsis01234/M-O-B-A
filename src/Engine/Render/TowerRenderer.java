package Engine.Render;

import Core.Config;
import Core.Moba.Units.Tour;
import Core.Moba.Units.Ancient;
import Core.Moba.World.TeamColor;
import Engine.Tile.Tile;
import java.awt.*;

public class TowerRenderer {
    
    private Tile[] tiles;
    
    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
    
    public void draw(Graphics2D g2, Tour tour, Camera camera) {
        int tileSize = Config.getTileSize();
        int x = (int) (tour.position().x() * tileSize);
        int y = (int) (tour.position().y() * tileSize);
        int structureWidth = tour.width() * tileSize;
        int structureHeight = tour.height() * tileSize;

        // Try to draw the tile image first
        boolean drewImage = false;
        if (tiles != null) {
            int tileId = 20;
            if (tour.equipe().couleur() == TeamColor.RED) {
                tileId = 21;
            }
            if (tileId >= 0 && tileId < tiles.length && tiles[tileId] != null) {
                Tile tile = tiles[tileId];
                Image img = tile.getImage();
                if (img != null) {
                    g2.drawImage(img, x, y, structureWidth, structureHeight, null);
                    drewImage = true;
                }
            }
        }

        // Only draw shapes if no image available (fallback)
        if (!drewImage) {
            double scaleX = (double)structureWidth / 64.0;
            double scaleY = (double)structureHeight / 64.0;

            g2.setColor(new Color(0x4a, 0x55, 0x68));
            g2.fillRect(x + (int)(15 * scaleX), y + (int)(44 * scaleY), (int)(34 * scaleX), (int)(12 * scaleY));

            g2.setColor(new Color(0x71, 0x80, 0x96));
            g2.fillRect(x + (int)(20 * scaleX), y + (int)(20 * scaleY), (int)(24 * scaleX), (int)(24 * scaleY));

            g2.setColor(new Color(0x2d, 0x37, 0x48));
            g2.fillRect(x + (int)(15 * scaleX), y + (int)(10 * scaleY), (int)(34 * scaleX), (int)(10 * scaleY));

            if (tour.equipe().couleur() == TeamColor.BLUE) {
                g2.setColor(new Color(0x42, 0x99, 0xe1));
            } else {
                g2.setColor(new Color(0xf5, 0x65, 0x65));
            }
            g2.fillRect(x + (int)(22 * scaleX), y + (int)(14 * scaleY), (int)(20 * scaleX), (int)(6 * scaleY));

            int[] rx = {x + (int)(15 * scaleX), x + (int)(32 * scaleX), x + (int)(49 * scaleX)};
            int[] ry = {y + (int)(10 * scaleY), y + (int)(2 * scaleY), y + (int)(10 * scaleY)};
            g2.setColor(new Color(0x2d, 0x37, 0x48));
            g2.fillPolygon(rx, ry, 3);
        }
        
        // Health bar always drawn on top
        g2.setColor(Color.RED);
        g2.fillRect(x + 5, y - 10, structureWidth - 10, 6);
        g2.setColor(Color.GREEN);
        double healthPct = (double)tour.stats().hp() / tour.stats().maxHp();
        g2.fillRect(x + 5, y - 10, (int)((structureWidth - 10) * healthPct), 6);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        String teamName = tour.equipe().couleur() == TeamColor.BLUE ? "Blue" : "Red";
        String label = teamName + " " + tour.lane().name() + " T" + tour.tier();
        
        FontMetrics fm = g2.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        g2.drawString(label, x + (structureWidth - labelWidth) / 2, y - 15);
    }

    public void drawAncient(Graphics2D g2, Ancient ancient, Camera camera) {
        int tileSize = Config.getTileSize();
        int x = (int) (ancient.position().x() * tileSize);
        int y = (int) (ancient.position().y() * tileSize);
        int w = ancient.width() * tileSize;
        int h = ancient.height() * tileSize;

        // Use tile color from Map.txt
        if (tiles != null) {
            int tileId = 22;
            if (ancient.equipe().couleur() == TeamColor.RED) {
                tileId = 23;
            }
            if (tileId >= 0 && tileId < tiles.length && tiles[tileId] != null) {
                Tile tile = tiles[tileId];
                Color color = tile.getColor();
                if (color != null) {
                    g2.setColor(color);
                    g2.fillRect(x, y, w, h);
                }
            }
        }
    }
}
