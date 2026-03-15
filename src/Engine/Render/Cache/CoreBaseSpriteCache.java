package Engine.Render.Cache;

import Core.Moba.World.TeamColor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class CoreBaseSpriteCache {
    private final Map<String, BufferedImage> cache = new HashMap<>();

    public BufferedImage getSprite(TeamColor teamColor, int width, int height) {
        String key = teamColor + "_" + width + "_" + height;
        BufferedImage cached = cache.get(key);
        if (cached != null) return cached;

        BufferedImage composite = composeCoreBaseSprite(teamColor, width, height);
        if (composite != null) {
            cache.put(key, composite);
        }
        return composite;
    }

    private BufferedImage composeCoreBaseSprite(TeamColor teamColor, int width, int height) {
        BufferedImage composite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = composite.createGraphics();
        
        // Blue: 0x42, 0x99, 0xe1 | Red: 0xf5, 0x65, 0x65
        java.awt.Color color = teamColor == TeamColor.BLUE 
            ? new java.awt.Color(0x42, 0x99, 0xe1, 180)
            : new java.awt.Color(0xf5, 0x65, 0x65, 180);
        
        g2.setColor(color);
        g2.fillRect(0, 0, width, height);
        
        g2.dispose();
        return composite;
    }
}
