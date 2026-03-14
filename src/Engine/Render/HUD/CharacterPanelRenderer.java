package Engine.Render.HUD;

import Core.Entity.Direction;
import Core.Entity.Player;
import Engine.Render.Cache.HeroSpriteCache;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CharacterPanelRenderer {
    private final Player player;
    private int x, y;
    private final int width;
    private final HeroSpriteCache spriteCache;
    private final int calculatedHeight;
    private BufferedImage background;

    public CharacterPanelRenderer(Player player, int x, int y, int width) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.spriteCache = new HeroSpriteCache();
        this.calculatedHeight = 140;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return calculatedHeight;
    }

    public void render(Graphics2D g2) {
        if (background == null) {
            background = HUDBackgrounds.getPanelBackground(width, calculatedHeight);
        }
        g2.drawImage(background, x, y, null);
        drawBorder(g2, x, y, width, calculatedHeight);

        FlexContainer portraitContainer = new FlexContainer()
            .setBounds(x + 10, y + 10, 44, 44)
            .direction(FlexContainer.FlexDirection.ROW)
            .alignItems(FlexContainer.AlignItems.CENTER)
            .justifyContent(FlexContainer.JustifyContent.CENTER);
        portraitContainer.addItem(44, 44);
        portraitContainer.layout();
        Rectangle portraitBounds = portraitContainer.getItem(0).bounds;

        g2.setColor(new Color(30, 30, 50));
        g2.fillRect(portraitBounds.x, portraitBounds.y, portraitBounds.width, portraitBounds.height);

        if (player.getHero() != null) {
            var heroSprite = spriteCache.getSprite(player.getHero(), Direction.DOWN, 1);
            if (heroSprite != null) {
                g2.drawImage(heroSprite, portraitBounds.x + 2, portraitBounds.y + 2, 
                    portraitBounds.width - 4, portraitBounds.height - 4, null);
            }
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(player.getHero().getName(), x + 60, y + 24);
            
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            String category = switch (player.getHero().getCategoryId()) {
                case 1 -> "Force";
                case 2 -> "Agilite";
                case 3 -> "Intelligence";
                default -> "Unknown";
            };
            g2.setColor(new Color(200, 200, 220));
            g2.drawString("Lv." + player.level() + " " + category, x + 60, y + 38);
        }

        FlexContainer barsContainer = new FlexContainer()
            .setBounds(x + 10, y + 60, width - 20, 30)
            .direction(FlexContainer.FlexDirection.COLUMN)
            .gap(6);
        barsContainer.addItem(0, 12);
        barsContainer.addItem(0, 12);
        barsContainer.layout();
        
        Rectangle hpBarBounds = barsContainer.getItem(0).bounds;
        Rectangle manaBarBounds = barsContainer.getItem(1).bounds;

        drawBar(g2, hpBarBounds.x, hpBarBounds.y, hpBarBounds.width, hpBarBounds.height, 
            player.stats().hp(), player.stats().maxHp(), 
            new Color(50, 180, 50), new Color(40, 60, 40), "HP");
        
        drawBar(g2, manaBarBounds.x, manaBarBounds.y, manaBarBounds.width, manaBarBounds.height, 
            player.stats().mana(), player.stats().maxMana(), 
            new Color(50, 100, 200), new Color(40, 80, 180), "MANA");

        FlexContainer statsContainer = new FlexContainer()
            .setBounds(x + 10, y + 100, width - 20, 20)
            .direction(FlexContainer.FlexDirection.ROW)
            .gap(10);
        statsContainer.addItem(60, 16);
        statsContainer.addItem(60, 16);
        statsContainer.addItem(60, 16);
        statsContainer.layout();
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        
        Rectangle atkBounds = statsContainer.getItem(0).bounds;
        Rectangle defBounds = statsContainer.getItem(1).bounds;
        Rectangle spdBounds = statsContainer.getItem(2).bounds;
        
        g2.drawString("ATK:" + player.stats().attack(), atkBounds.x, atkBounds.y + 12);
        g2.drawString("DEF:" + player.stats().defense(), defBounds.x, defBounds.y + 12);
        g2.drawString("SPD:" + String.format("%.0f", player.stats().moveSpeed()), spdBounds.x, spdBounds.y + 12);
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
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        String text = current + "/" + max;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + height - 1);
        
        g2.setColor(new Color(220, 220, 240));
        g2.setFont(new Font("Arial", Font.BOLD, 9));
        g2.drawString(label, x + 2, y - 1);
    }
    
    private void drawBorder(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(new Color(80, 80, 100));
        g2.drawRect(x, y, width - 1, height - 1);
        g2.setColor(new Color(40, 40, 60));
        g2.drawRect(x + 1, y + 1, width - 3, height - 3);
    }
}