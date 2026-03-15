package Engine.Render.World;

import Core.Config;
import Core.Moba.Units.Tour;
import Core.Moba.World.TeamColor;
import Engine.Render.Camera;
import Engine.Tile.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TowerRenderer {
    
    private Tile[] tiles;
    
    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
    
    public void draw(Graphics2D g2, Tour tour, Camera camera) {
        int tileSize = Config.getTileSize();
        double centerTileX = tour.position().x() + tour.width() / 2.0;
        double baseTileY = tour.position().y() + tour.height();
        int x = (int) (centerTileX * tileSize);
        int y = (int) (baseTileY * tileSize);
        
        int structureWidth = tour.width() * tileSize;
        int scaledH = 2 * tileSize;
        
        int tileId = tour.equipe().couleur() == TeamColor.RED ? 21 : 20;
        Tile tile = (tileId >= 0 && tileId < tiles.length) ? tiles[tileId] : null;
        
        BufferedImage towerImg = getTowerFrame(tile, tour.getCurrentFrame());
        
        if (towerImg != null) {
            int imgWidth = towerImg.getWidth();
            int imgHeight = towerImg.getHeight();
            
            scaledH = 2 * tileSize;
            int scaledW = (int)((double)imgWidth / imgHeight * scaledH);
            structureWidth = scaledW;
            
            int drawX = x - scaledW / 2;
            int drawY = y - scaledH;
            
            g2.drawImage(towerImg, drawX, drawY, scaledW, scaledH, null);
        } else {
            g2.setColor(tour.equipe().couleur() == TeamColor.BLUE 
                ? new Color(0x2a, 0x5a, 0x9e)
                : new Color(0xff, 0x45, 0x45));
            int structureHeight = tour.height() * tileSize;
            g2.fillRect(x - structureWidth/2, y - structureHeight, structureWidth, structureHeight);
        }
        
        int healthBarWidth = structureWidth - 20;
        int healthBarHeight = 4;
        int healthBarY = y - scaledH - 8;
        g2.setColor(Color.RED);
        g2.fillRect(x - healthBarWidth/2, healthBarY, healthBarWidth, healthBarHeight);
        g2.setColor(Color.GREEN);
        double healthPct = (double)tour.stats().hp() / tour.stats().maxHp();
        g2.fillRect(x - healthBarWidth/2, healthBarY, (int)(healthBarWidth * healthPct), healthBarHeight);
    }
    
    private BufferedImage getTowerFrame(Tile tile, int frameIndex) {
        if (tile == null) return null;
        Object userData = tile.getUserData();
        if (userData instanceof BufferedImage[]) {
            BufferedImage[] frames = (BufferedImage[]) userData;
            if (frameIndex >= 0 && frameIndex < frames.length) {
                return frames[frameIndex];
            }
        }
        return null;
    }
}
