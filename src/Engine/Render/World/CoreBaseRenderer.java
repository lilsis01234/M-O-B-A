package Engine.Render.World;

import Core.Moba.Units.CoreBase;
import Core.Moba.World.TeamColor;
import Core.Config;
import Engine.Tile.Tile;
import Engine.Render.Cache.CoreBaseSpriteCache;
import Engine.Render.Camera;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CoreBaseRenderer {
     
    private Tile[] tiles;
    private CoreBaseSpriteCache spriteCache;
    
    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
        this.spriteCache = new CoreBaseSpriteCache();
    }
    
    public void draw(Graphics2D g2, CoreBase coreBase, Camera camera) {
        int tileSize = Config.getTileSize();
        double centerTileX = coreBase.position().x() + coreBase.width() / 2.0;
        double baseTileY = coreBase.position().y() + coreBase.height();
        int centerX = (int) (centerTileX * tileSize);
        int baseY = (int) (baseTileY * tileSize);
        int targetW = coreBase.width() * tileSize;
        int targetH = coreBase.height() * tileSize;
        
        // Get cached sprite for this core base
        BufferedImage sprite = spriteCache.getSprite(
            coreBase.equipe().couleur(), 
            targetW, 
            targetH
        );
        
        if (sprite != null) {
            // Draw the sprite centered at the base position
            int drawX = centerX - targetW / 2;
            int drawY = baseY - targetH;
            g2.drawImage(sprite, drawX, drawY, targetW, targetH, null);
            
            // Draw health bar above the sprite
            int healthBarWidth = targetW - 20;
            int healthBarHeight = 4;
            int healthBarY = drawY - 8; // Position above the sprite
            
            g2.setColor(Color.RED);
            g2.fillRect(drawX + (targetW - healthBarWidth) / 2, healthBarY, healthBarWidth, healthBarHeight);
            g2.setColor(Color.GREEN);
            double healthPct = (double)coreBase.stats().hp() / coreBase.stats().maxHp();
            g2.fillRect(drawX + (targetW - healthBarWidth) / 2, healthBarY, 
                       (int)(healthBarWidth * healthPct), healthBarHeight);
        } else {
            drawFallbackCoreBase(g2, centerX, baseY, targetW, targetH, coreBase.equipe().couleur());
        }
    }
    
    private void drawFallbackCoreBase(Graphics2D g2, int x, int y, int w, int h, TeamColor teamColor) {
        Color color = teamColor == TeamColor.BLUE 
            ? new Color(0x42, 0x99, 0xe1) 
            : new Color(0xf5, 0x65, 0x65);
        g2.setColor(color);
        g2.fillRect(x - w/2, y - h, w, h);
    }
}
