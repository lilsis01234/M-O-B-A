package Engine.Render;

import Core.Entity.Direction;
import Core.Entity.Player;

import java.awt.*;

public class CharacterPanelRenderer {
    private final Player player;
    private final int x, y, width;
    private final HeroSpriteCache spriteCache;
    private int calculatedHeight;

    public CharacterPanelRenderer(Player player, int x, int y, int width) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.spriteCache = new HeroSpriteCache();
        this.calculatedHeight = 140;
    }

    public int getHeight() {
        return calculatedHeight;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(x, y, width, calculatedHeight, 8, 8);
        g2.setColor(new Color(80, 80, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, calculatedHeight, 8, 8);

        g2.setColor(new Color(30, 30, 50));
        g2.fillRect(x + 10, y + 10, 44, 44);

        if (player.getHero() != null) {
            var heroSprite = spriteCache.getSprite(player.getHero(), Direction.DOWN, 1);
            if (heroSprite != null) {
                g2.drawImage(heroSprite, x + 12, y + 12, 40, 40, null);
            }
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString(player.getHero().getName(), x + 60, y + 18);
            
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            String category = switch (player.getHero().getCategoryId()) {
                case 1 -> "Force";
                case 2 -> "Agilite";
                case 3 -> "Intelligence";
                default -> "Unknown";
            };
            g2.setColor(new Color(180, 180, 200));
            g2.drawString("Lv." + player.level() + " " + category, x + 60, y + 30);
        }

        int barX = x + 10;
        int barY = y + 60;
        int barW = width - 20;
        int barH = 12;

        drawBar(g2, barX, barY, barW, barH, player.stats().hp(), player.stats().maxHp(), 
            new Color(50, 180, 50), new Color(40, 60, 40), "HP");
        
        barY += 18;
        drawBar(g2, barX, barY, barW, barH, player.stats().mana(), player.stats().maxMana(), 
            new Color(50, 100, 200), new Color(40, 80, 180), "MANA");

        int statsX = x + 10;
        int statsY = y + 105;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        String atk = "ATK:" + player.stats().attack();
        String def = "DEF:" + player.stats().defense();
        String spd = "SPD:" + String.format("%.0f", player.stats().moveSpeed());
        g2.drawString(atk, statsX, statsY);
        g2.drawString(def, statsX + 60, statsY);
        g2.drawString(spd, statsX + 120, statsY);
    }

    private void drawBar(Graphics2D g2, int x, int y, int width, int height, int current, int max, 
                        Color fillColor, Color bgColor, String label) {
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, width, height, 2, 2);
        
        if (max > 0) {
            int fillWidth = (int) ((double) current / max * width);
            g2.setColor(fillColor);
            g2.fillRoundRect(x, y, fillWidth, height, 2, 2);
        }
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 8));
        String text = current + "/" + max;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + height - 2);
        
        g2.setColor(new Color(200, 200, 220));
        g2.setFont(new Font("Arial", Font.PLAIN, 7));
        g2.drawString(label, x + 1, y - 1);
    }
}