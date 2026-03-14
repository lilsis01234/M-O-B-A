package Engine.Render.World;

import Core.Config;
import Core.Moba.Units.Tour;
import Core.Moba.Units.Ancient;
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
    
    public void draw(Graphics2D g2, Tour tour, Camera camera) {
        int tileSize = Config.getTileSize();
        // Convert from top-left tile position to center-bottom of base
        double centerTileX = tour.position().x() + tour.width() / 2.0;
        double baseTileY = tour.position().y() + tour.height();
        int x = (int) (centerTileX * tileSize);
        int y = (int) (baseTileY * tileSize);
        
        // Calculate tower dimensions for health bar (2 tiles tall)
        int structureWidth = tour.width() * tileSize;
        int scaledH = 2 * tileSize;
        
        // Get tower tile (20 for blue, 21 for red)
        int tileId = tour.equipe().couleur() == TeamColor.RED ? 21 : 20;
        Tile tile = (tileId >= 0 && tileId < tiles.length) ? tiles[tileId] : null;
        
        // Get animated frame from tower's current animation state
        BufferedImage towerImg = getTowerFrame(tile, tour.getCurrentFrame());
        
        if (towerImg != null) {
            // Isometric tower rendering (64x96 sprites scaled to tile size)
            // The anchor is center-bottom of the tower base
            int imgWidth = towerImg.getWidth();
            int imgHeight = towerImg.getHeight();
            
            // Scale to make tower 2 tiles tall
            scaledH = 2 * tileSize;
            int scaledW = (int)((double)imgWidth / imgHeight * scaledH);
            structureWidth = scaledW; // Use sprite width for health bar
            
            // Position: x and y from tower position is the base center
            int drawX = x - scaledW / 2;
            int drawY = y - scaledH;
            
            g2.drawImage(towerImg, drawX, drawY, scaledW, scaledH, null);
        } else {
            // Fallback to simple rectangle with adjusted team colors
            g2.setColor(tour.equipe().couleur() == TeamColor.BLUE 
                ? new Color(0x2a, 0x5a, 0x9e)  // Darker, less bright blue
                : new Color(0xff, 0x45, 0x45)); // More saturated red
            int structureHeight = tour.height() * tileSize;
            g2.fillRect(x - structureWidth/2, y - structureHeight, structureWidth, structureHeight);
        }
        
        // Health bar - positioned on top of tower, smaller
        int healthBarWidth = structureWidth - 20; // 10px padding on each side
        int healthBarHeight = 4; // thinner
        int healthBarY = y - scaledH - 8; // above the tower top
        g2.setColor(Color.RED);
        g2.fillRect(x - healthBarWidth/2, healthBarY, healthBarWidth, healthBarHeight);
        g2.setColor(Color.GREEN);
        double healthPct = (double)tour.stats().hp() / tour.stats().maxHp();
        g2.fillRect(x - healthBarWidth/2, healthBarY, (int)(healthBarWidth * healthPct), healthBarHeight);
    }
    
    public void drawAncient(Graphics2D g2, Ancient ancient, Camera camera) {
         int tileSize = Config.getTileSize();
         // Convert from top-left tile position to center-bottom of base
         double centerTileX = ancient.position().x() + ancient.width() / 2.0;
         double baseTileY = ancient.position().y() + ancient.height();
         int x = (int) (centerTileX * tileSize);
         int y = (int) (baseTileY * tileSize);
         int w = ancient.width() * tileSize;
         int h = ancient.height() * tileSize;
        
        // Draw team-colored fill (no wood floor background)
        Color teamColor = ancient.equipe().couleur() == TeamColor.BLUE 
            ? new Color(0x42, 0x99, 0xe1) 
            : new Color(0xf5, 0x65, 0x65);
        g2.setColor(teamColor);
        g2.fillRect(x - w/2, y - h, w, h);
    }
}