package Engine.Render;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.Combat.Stats;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class PlayerRenderer {
    private final PlayerSprites sprites;
    private final BufferedImage shadow;
    private final OutfitSprites outfitSprites;
    private final SuitSprites suitSprites;
    private final HairSprites hairSprites;
    private final boolean useSuit;

    public PlayerRenderer(PlayerSprites sprites) {
        this.sprites = sprites;
        this.shadow = loadShadow();
        this.useSuit = shouldUseSuit();
        if (useSuit) {
            this.suitSprites = createRandomSuitSprites();
            this.outfitSprites = null;
        } else {
            this.outfitSprites = createRandomOutfitSprites();
            this.suitSprites = null;
        }
        this.hairSprites = createRandomHairSprites();
    }
    
    private boolean shouldUseSuit() {
        Random rand = new Random();
        return rand.nextDouble() < 0.4; // 40% chance for suit, 60% for regular outfit
    }
    
    private BufferedImage loadShadow() {
        try {
            return ImageIO.read(new File("src/Resource/Characters/MetroCity/CharacterModel/Shadow.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shadow image", e);
        }
    }
    
    private OutfitSprites createRandomOutfitSprites() {
        Random rand = new Random();
        int outfitNum = rand.nextInt(6) + 1; // 1-6 inclusive
        return new OutfitSprites("Outfit" + outfitNum + ".png");
    }
    
    private SuitSprites createRandomSuitSprites() {
        Random rand = new Random();
        int suitRow = rand.nextInt(4); // 0-3 inclusive (4 rows)
        return new SuitSprites(suitRow);
    }
    
    private HairSprites createRandomHairSprites() {
        Random rand = new Random();
        int hairRow = rand.nextInt(8); // 0-7 inclusive (8 rows)
        return new HairSprites(hairRow);
    }

    public void draw(Graphics2D g2, Player player) {
        drawShadow(g2, player);
        BufferedImage image = sprites.get(player.getDirection(), player.getSpriteNum());
        int tileSize = Config.getTileSize();
        g2.drawImage(image, (int) player.getX(), (int) player.getY(), tileSize, tileSize, null);
        
        if (useSuit) {
            drawSuit(g2, player, tileSize);
        } else {
            drawOutfit(g2, player, tileSize);
        }
        
        // Hair: only show if suit has no headwear, or if using regular outfit
        boolean showHair = !useSuit || (useSuit && suitSprites != null && !suitSprites.hasHeadwear());
        if (showHair) {
            drawHair(g2, player, tileSize);
        }
        
        drawLevel(g2, player, tileSize);
        drawHealthManaBars(g2, player, tileSize);
    }
    
    private void drawShadow(Graphics2D g2, Player player) {
        int tileSize = Config.getTileSize();
        int shadowWidth = shadow.getWidth();
        int shadowHeight = shadow.getHeight();
        int shadowX = (int) player.getX() + (tileSize - shadowWidth) / 2;
        int shadowY = (int) player.getY() + tileSize - shadowHeight;
        g2.drawImage(shadow, shadowX, shadowY, shadowWidth, shadowHeight, null);
    }
    
    private void drawOutfit(Graphics2D g2, Player player, int tileSize) {
        BufferedImage outfit = outfitSprites.get(player.getDirection(), player.getSpriteNum());
        g2.drawImage(outfit, (int) player.getX(), (int) player.getY(), tileSize, tileSize, null);
    }
    
    private void drawSuit(Graphics2D g2, Player player, int tileSize) {
        BufferedImage suit = suitSprites.get(player.getDirection(), player.getSpriteNum());
        g2.drawImage(suit, (int) player.getX(), (int) player.getY(), tileSize, tileSize, null);
    }
    
    private void drawHair(Graphics2D g2, Player player, int tileSize) {
        BufferedImage hair = hairSprites.get(player.getDirection(), player.getSpriteNum());
        g2.drawImage(hair, (int) player.getX(), (int) player.getY(), tileSize, tileSize, null);
    }
    
    private void drawLevel(Graphics2D g2, Player player, int tileSize) {
        int x = (int) player.getX() + 2;
        int y = (int) player.getY() - 20;
        g2.setColor(java.awt.Color.YELLOW);
        java.awt.Font originalFont = g2.getFont();
        g2.setFont(originalFont.deriveFont(java.awt.Font.BOLD, 14));
        g2.drawString("Lv " + player.level(), x, y);
        g2.setFont(originalFont);
    }

    private void drawHealthManaBars(Graphics2D g2, Player player, int tileSize) {
        int barWidth = tileSize - 4;
        int barHeight = 4;
        int x = (int) player.getX() + 2;
        int y = (int) player.getY() - 10;
        
        Stats stats = player.stats();
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
