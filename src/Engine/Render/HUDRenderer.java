package Engine.Render;

import Core.Config;
import Core.Entity.Direction;
import Core.Entity.Player;
import Core.Moba.World.Arena;
import Core.Moba.World.Equipe;
import Core.Moba.World.TeamColor;
import Core.Tile.TileMap;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class HUDRenderer {

    private final Player player;
    private final Arena arena;
    private final TileMap tileMap;
    private final int screenWidth;
    private final int screenHeight;

    private final MinimapRenderer minimap;
    private final CharacterPanelRenderer characterPanel;
    private final AbilityBarRenderer abilityBar;
    private final ItemBarRenderer itemBar;
    private final GoldDisplayRenderer goldDisplay;
    private final ScoreboardRenderer scoreboard;
    private final TargetInfoRenderer targetInfo;
    private final BuffRenderer buffRenderer;
    private Camera camera;
    private java.util.function.Consumer<Point> moveTargetConsumer;

    public HUDRenderer(Player player, Arena arena, TileMap tileMap, int screenWidth, int screenHeight) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Minimap - top right
        int minimapSize = 180;
        this.minimap = new MinimapRenderer(player, arena, tileMap, 
            screenWidth - minimapSize - 15, 15, minimapSize);
        
        // Scoreboard - top left (above minimap to avoid overlap)
        this.scoreboard = new ScoreboardRenderer(arena, player, 15, 15, 180, 70);
        
        // Character Panel - bottom left
        this.characterPanel = new CharacterPanelRenderer(player, 15, screenHeight - 170, 260, 150);
        
        // Ability Bar - bottom center
        int abilityBarWidth = 380;
        int abilityBarX = (screenWidth - abilityBarWidth) / 2;
        this.abilityBar = new AbilityBarRenderer(player, abilityBarX, screenHeight - 85, abilityBarWidth, 75);
        
        // Item Bar - bottom right (above ability bar)
        int itemBarWidth = 240;
        this.itemBar = new ItemBarRenderer(player, screenWidth - itemBarWidth - 15, screenHeight - 165, itemBarWidth, 65);
        
        // Gold Display - above item bar
        this.goldDisplay = new GoldDisplayRenderer(player, screenWidth - 130, screenHeight - 200, 115, 28);
        
        // Target Info - left side, above character panel
        this.targetInfo = new TargetInfoRenderer(player, 15, screenHeight - 340, 230, 130);
        
        // Buff/Debuff Display - above target info
        this.buffRenderer = new BuffRenderer(player, 15, screenHeight - 380, 230, 35);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        this.minimap.setCamera(camera);
    }

    public void setMoveTargetConsumer(java.util.function.Consumer<Point> consumer) {
        this.moveTargetConsumer = consumer;
        this.minimap.setOnClickCallback(consumer);
    }

    public boolean handleMouseClick(int clickX, int clickY) {
        return minimap.handleClick(clickX, clickY);
    }

    public void render(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        minimap.render(g2);
        characterPanel.render(g2);
        abilityBar.render(g2);
        itemBar.render(g2);
        goldDisplay.render(g2);
        scoreboard.render(g2);
        targetInfo.render(g2);
        buffRenderer.render(g2);

        drawRespawnOverlay(g2);
    }

    private void drawRespawnOverlay(Graphics2D g2) {
        if (!player.isAlive()) {
            double timeLeft = player.getRespawnTimeRemaining();
            if (timeLeft > 0) {
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRect(0, 0, screenWidth, screenHeight);
                
                String text = String.format("RESPAWNING IN %.1f SECONDS", timeLeft);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
            }
        }
    }
}

class MinimapRenderer {
    private final Arena arena;
    private final TileMap tileMap;
    private final Player player;
    private final int x, y, size;
    private Camera camera;
    private java.util.function.Consumer<Point> onClickCallback;

    public MinimapRenderer(Player player, Arena arena, TileMap tileMap, int x, int y, int size) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setOnClickCallback(java.util.function.Consumer<Point> callback) {
        this.onClickCallback = callback;
    }

    public boolean handleClick(int clickX, int clickY) {
        if (clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size) {
            float mapWidth = tileMap.getColumns() * Config.getTileSize();
            float mapHeight = tileMap.getRows() * Config.getTileSize();
            float scaleX = mapWidth / size;
            float scaleY = mapHeight / size;
            
            float worldX = (clickX - x) * scaleX;
            float worldY = (clickY - y) * scaleY;
            
            if (onClickCallback != null) {
                onClickCallback.accept(new Point((int) worldX, (int) worldY));
            }
            return true;
        }
        return false;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 230));
        g2.fillRoundRect(x, y, size, size, 10, 10);
        g2.setColor(new Color(100, 100, 120));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, size, size, 10, 10);

        float mapWidth = tileMap.getColumns() * Config.getTileSize();
        float mapHeight = tileMap.getRows() * Config.getTileSize();
        float scaleX = size / mapWidth;
        float scaleY = size / mapHeight;

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int col = 0; col < tileMap.getColumns(); col++) {
                int tileId = tileMap.getTileAt(row, col);
                Color tileColor = getTileColor(tileId);
                if (tileColor != null) {
                    int px = x + (int) (col * Config.getTileSize() * scaleX);
                    int py = y + (int) (row * Config.getTileSize() * scaleY);
                    int w = (int) (Config.getTileSize() * scaleX) + 1;
                    int h = (int) (Config.getTileSize() * scaleY) + 1;
                    g2.setColor(tileColor);
                    g2.fillRect(px, py, w, h);
                }
            }
        }

        for (var tour : arena.tours()) {
            int px = x + (int) (tour.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (tour.position().y() * Config.getTileSize() * scaleY);
            g2.setColor(tour.equipe().couleur() == TeamColor.BLUE ? Color.CYAN : Color.RED);
            g2.fillRect(px - 2, py - 2, 4, 4);
        }

        for (var ancient : arena.ancients()) {
            int px = x + (int) (ancient.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (ancient.position().y() * Config.getTileSize() * scaleY);
            g2.setColor(ancient.equipe().couleur() == TeamColor.BLUE ? Color.BLUE : new Color(180, 0, 0));
            g2.fillRect(px - 4, py - 4, 8, 8);
        }

        int playerX = x + (int) (player.getX() * scaleX);
        int playerY = y + (int) (player.getY() * scaleY);
        g2.setColor(Color.GREEN);
        g2.fillOval(playerX - 4, playerY - 4, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(playerX - 4, playerY - 4, 8, 8);

        if (camera != null) {
            int camX = x + (int) (camera.getX() * scaleX);
            int camY = y + (int) (camera.getY() * scaleY);
            int camW = (int) (camera.getViewportWidth() * scaleX);
            int camH = (int) (camera.getViewportHeight() * scaleY);
            g2.setColor(new Color(255, 255, 255, 100));
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(camX, camY, camW, camH);
        }
    }

    private Color getTileColor(int tileId) {
        return switch (tileId) {
            case 0 -> new Color(50, 120, 50);
            case 1 -> new Color(90, 70, 40);
            case 2 -> new Color(60, 60, 60);
            case 3 -> new Color(160, 140, 100);
            case 4 -> new Color(80, 150, 200);
            case 5 -> new Color(30, 100, 30);
            default -> new Color(40, 40, 50);
        };
    }
}

class CharacterPanelRenderer {
    private final Player player;
    private final int x, y, width, height;
    private final HeroSpriteCache spriteCache;

    public CharacterPanelRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.spriteCache = new HeroSpriteCache();
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.setColor(new Color(80, 80, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 10, 10);

        g2.setColor(new Color(30, 30, 50));
        g2.fillRect(x + 15, y + 15, 60, 60);

        if (player.getHero() != null) {
            var heroSprite = spriteCache.getSprite(player.getHero(), Direction.DOWN, 1);
            if (heroSprite != null) {
                g2.drawImage(heroSprite, x + 18, y + 18, 54, 54, null);
            }
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(player.getHero().getName(), x + 85, y + 25);
            
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String category = switch (player.getHero().getCategoryId()) {
                case 1 -> "Force";
                case 2 -> "Agilite";
                case 3 -> "Intelligence";
                default -> "Unknown";
            };
            g2.setColor(new Color(180, 180, 200));
            g2.drawString("Lv." + player.level() + " " + category, x + 85, y + 40);
        }

        int barX = x + 15;
        int barY = y + 85;
        int barW = width - 30;
        int barH = 18;

        drawBar(g2, barX, barY, barW, barH, player.stats().hp(), player.stats().maxHp(), 
            new Color(200, 50, 50), new Color(180, 40, 40), "HP");
        
        barY += 25;
        drawBar(g2, barX, barY, barW, barH, player.stats().mana(), player.stats().maxMana(), 
            new Color(50, 100, 200), new Color(40, 80, 180), "MANA");

        int statsX = x + 15;
        int statsY = y + 138;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.drawString("ATK: " + player.stats().attack(), statsX, statsY);
        statsY += 16;
        g2.drawString("DEF: " + player.stats().defense(), statsX, statsY);
        statsY += 16;
        g2.drawString("SPD: " + String.format("%.1f", player.stats().moveSpeed()), statsX, statsY);
    }

    private void drawBar(Graphics2D g2, int x, int y, int width, int height, int current, int max, 
                        Color fillColor, Color bgColor, String label) {
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, width, height, 4, 4);
        
        if (max > 0) {
            int fillWidth = (int) ((double) current / max * width);
            g2.setColor(fillColor);
            g2.fillRoundRect(x, y, fillWidth, height, 4, 4);
        }
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        String text = current + "/" + max;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + height - 4);
        
        g2.setColor(new Color(200, 200, 220));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString(label, x + 2, y - 2);
    }
}

class AbilityBarRenderer {
    private final Player player;
    private final int x, y, width, height;

    public AbilityBarRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRoundRect(x, y, width, height, 10, 10);

        int slotSize = 60;
        int slotCount = 4;
        int spacing = (width - slotCount * slotSize) / (slotCount + 1);
        int startX = x + spacing;

        for (int i = 0; i < slotCount; i++) {
            int slotX = startX + i * (slotSize + spacing);
            int slotY = y + 10;
            
            g2.setColor(new Color(40, 40, 60));
            g2.fillRoundRect(slotX, slotY, slotSize, slotSize, 8, 8);
            g2.setColor(new Color(100, 100, 130));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(slotX, slotY, slotSize, slotSize, 8, 8);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            String key = (i + 1) + "";
            g2.drawString(key, slotX + 5, slotY + 18);

            if (i < 3 && player.getHero() != null) {
                var spells = player.getHero().getSpells();
                if (i < spells.size()) {
                    var spell = spells.get(i);
                    g2.setColor(new Color(180, 180, 200));
                    g2.setFont(new Font("Arial", Font.PLAIN, 9));
                    String name = spell.getName().length() > 8 ? spell.getName().substring(0, 8) : spell.getName();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(name, slotX + (slotSize - fm.stringWidth(name)) / 2, slotY + 50);
                }
            }
        }
    }
}

class ItemBarRenderer {
    private final Player player;
    private final int x, y, width, height;

    public ItemBarRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        g2.setColor(new Color(180, 180, 200));
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("ITEMS", x + 10, y + 15);

        int slotSize = 32;
        int slotCount = 6;
        int spacing = 4;
        int startX = x + 8;
        int startY = y + 25;

        for (int i = 0; i < slotCount; i++) {
            int slotX = startX + i * (slotSize + spacing);
            
            g2.setColor(new Color(50, 50, 70));
            g2.fillRect(slotX, startY, slotSize, slotSize);
            g2.setColor(new Color(100, 100, 120));
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(slotX, startY, slotSize, slotSize);

            g2.setColor(new Color(120, 120, 140));
            g2.setFont(new Font("Arial", Font.PLAIN, 8));
            String num = (i + 1) + "";
            g2.drawString(num, slotX + 2, startY + 10);
        }
    }
}

class GoldDisplayRenderer {
    private final Player player;
    private final int x, y, width, height;

    public GoldDisplayRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(255, 200, 0, 100));
        g2.fillRoundRect(x, y, width, height, 5, 5);
        
        g2.setColor(new Color(255, 215, 0));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String goldText = "" + player.getGold();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(goldText, x + 10, y + 20);
        
        g2.setColor(new Color(180, 180, 100));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("GOLD", x + width - 40, y + 20);
    }
}

class ScoreboardRenderer {
    private final Arena arena;
    private final Player player;
    private final int x, y, width, height;

    public ScoreboardRenderer(Arena arena, Player player, int x, int y, int width, int height) {
        this.arena = arena;
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("RADIANT", x + 20, y + 20);
        
        g2.setColor(new Color(255, 255, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String blueScore = "" + arena.getBlueKills();
        g2.drawString(blueScore, x + 100, y + 22);

        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("DIRE", x + 20, y + 45);
        
        g2.setColor(new Color(255, 255, 255));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String redScore = "" + arena.getRedKills();
        g2.drawString(redScore, x + 100, y + 47);
    }
}

class TargetInfoRenderer {
    private final Player player;
    private final int x, y, width, height;

    public TargetInfoRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        if (!player.hasSelectedTarget()) return;

        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.setColor(new Color(100, 100, 130));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, height, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("TARGET", x + 10, y + 20);

        var target = player.getSelectedTarget();
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Type: " + target.getClass().getSimpleName(), x + 10, y + 45);

        int hp = 100;
        int maxHp = 100;
        
        if (target instanceof Core.Moba.Units.Tour) {
            var tour = (Core.Moba.Units.Tour) target;
            hp = tour.stats().hp();
            maxHp = tour.stats().maxHp();
        } else if (target instanceof Core.Moba.Units.Ancient) {
            var ancient = (Core.Moba.Units.Ancient) target;
            hp = ancient.stats().hp();
            maxHp = ancient.stats().maxHp();
        }

        g2.setColor(new Color(200, 50, 50));
        g2.fillRect(x + 10, y + 55, width - 20, 10);
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(x + 10, y + 55, (int) ((double) hp / maxHp * (width - 20)), 10);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString(hp + "/" + maxHp, x + 10, y + 75);
    }
}

class BuffRenderer {
    private final Player player;
    private final int x, y, width, height;

    public BuffRenderer(Player player, int x, int y, int width, int height) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 180));
        g2.fillRoundRect(x, y, width, height, 8, 8);
        
        g2.setColor(new Color(180, 180, 200));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString("BUFFS", x + 10, y + 15);
        
        if (player.isInFountain()) {
            g2.setColor(Color.CYAN);
            g2.fillRect(x + 10, y + 22, 20, 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 8));
            g2.drawString("HEAL", x + 12, y + 35);
        }
        
        if (player.isOnEnemyWood()) {
            g2.setColor(new Color(200, 50, 50));
            g2.fillRect(x + 35, y + 22, 20, 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 8));
            g2.drawString("DMG", x + 37, y + 35);
        }
    }
}