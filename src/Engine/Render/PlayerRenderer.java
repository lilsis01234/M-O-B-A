package Engine.Render;

import Core.Config;
import Core.Entity.Player;
import Core.Entity.Direction;
import Engine.Render.HeroSpriteCache;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PlayerRenderer {
    private final HeroSpriteCache spriteCache;
    private final BufferedImage shadow;

    public PlayerRenderer(Player player) {
        this.spriteCache = new HeroSpriteCache();
        this.shadow = loadShadow();
    }

    private BufferedImage loadShadow() {
        try {
            return ImageIO.read(new File("src/Resource/Characters/MetroCity/CharacterModel/Shadow.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shadow image", e);
        }
    }

    public void draw(Graphics2D g2, Player player) {
        drawShadow(g2, player);
        BufferedImage sprite = spriteCache.getSprite(player.getHero(), player.getDirection(), player.getSpriteNum());
        if (sprite != null) {
            int tileSize = Config.getTileSize();
            g2.drawImage(sprite, (int) player.getX(), (int) player.getY(), tileSize, tileSize, null);
        }

        drawLevel(g2, player);
        drawHealthManaBars(g2, player);
    }

    private void drawShadow(Graphics2D g2, Player player) {
        int tileSize = Config.getTileSize();
        int shadowWidth = shadow.getWidth();
        int shadowHeight = shadow.getHeight();
        int shadowX = (int) player.getX() + (tileSize - shadowWidth) / 2;
        int shadowY = (int) player.getY() + tileSize - shadowHeight;
        g2.drawImage(shadow, shadowX, shadowY, shadowWidth, shadowHeight, null);
    }

    private void drawLevel(Graphics2D g2, Player player) {
        int x = (int) player.getX() + 2;
        int y = (int) player.getY() - 20;
        g2.setColor(java.awt.Color.YELLOW);
        java.awt.Font originalFont = g2.getFont();
        g2.setFont(originalFont.deriveFont(java.awt.Font.BOLD, 14));
        g2.drawString("Lv " + player.level(), x, y);
        g2.setFont(originalFont);
    }

    private void drawHealthManaBars(Graphics2D g2, Player player) {
        int tileSize = Config.getTileSize();
        int barWidth = tileSize - 4;
        int barHeight = 4;
        int x = (int) player.getX() + 2;
        int y = (int) player.getY() - 10;

        var stats = player.stats();
        if (stats != null) {
            g2.setColor(java.awt.Color.LIGHT_GRAY);
            g2.fillRect(x, y, barWidth, barHeight);

            double hpPercent = (double) stats.hp() / stats.maxHp();
            if (hpPercent > 0) {
                g2.setColor(java.awt.Color.GREEN);
                g2.fillRect(x, y, (int) (barWidth * hpPercent), barHeight);
            }

            if (stats.maxMana() > 0) {
                int manaY = y + barHeight + 2;
                g2.setColor(java.awt.Color.LIGHT_GRAY);
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
