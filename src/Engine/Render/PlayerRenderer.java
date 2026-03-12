package Engine.Render;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.Combat.Stats;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PlayerRenderer {
    private final PlayerSprites sprites;

    public PlayerRenderer(PlayerSprites sprites) {
        this.sprites = sprites;
    }

    public void draw(Graphics2D g2, Player player) {
        BufferedImage image = sprites.get(player.getDirection(), player.getSpriteNum());
        int tileSize = Config.getTileSize();
        g2.drawImage(image, (int) player.getX(), (int) player.getY(), tileSize, tileSize, null);
        
        drawHealthManaBars(g2, player, tileSize);
    }

    private void drawHealthManaBars(Graphics2D g2, Player player, int tileSize) {
        int barWidth = tileSize - 4;
        int barHeight = 4;
        int x = (int) player.getX() + 2;
        int y = (int) player.getY() - 10;
        
        Stats stats = player.stats();
        if (stats != null) {
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(x, y, barWidth, barHeight);
            
            double hpPercent = (double) stats.hp() / stats.maxHp();
            if (hpPercent > 0) {
                g2.setColor(java.awt.Color.GREEN);
                g2.fillRect(x, y, (int) (barWidth * hpPercent), barHeight);
            }
            
            if (stats.maxMana() > 0) {
                int manaY = y + barHeight + 2;
                g2.setColor(java.awt.Color.BLUE);
                g2.fillRect(x, manaY, barWidth, barHeight);
                
                double manaPercent = (double) stats.mana() / stats.maxMana();
                if (manaPercent > 0) {
                    g2.setColor(new java.awt.Color(100, 100, 255));
                    g2.fillRect(x, manaY, (int) (barWidth * manaPercent), barHeight);
                }
            }
        }
    }
}

