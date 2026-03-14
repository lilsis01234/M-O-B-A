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
        
        // Character Panel - bottom left (auto-height)
        this.characterPanel = new CharacterPanelRenderer(player, 15, screenHeight - 145, 250);
        
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
    private TeamColor playerTeam;

    public MinimapRenderer(Player player, Arena arena, TileMap tileMap, int x, int y, int size) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.x = x;
        this.y = y;
        this.size = size;
        this.playerTeam = player.equipe() != null ? player.equipe().couleur() : TeamColor.BLUE;
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

    public boolean handleRightClick(int clickX, int clickY) {
        if (clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size) {
            if (camera != null) {
                float mapWidth = tileMap.getColumns() * Config.getTileSize();
                float mapHeight = tileMap.getRows() * Config.getTileSize();
                float scaleX = mapWidth / size;
                float scaleY = mapHeight / size;
                
                float worldX = (clickX - x) * scaleX;
                float worldY = (clickY - y) * scaleY;
                
                camera.setPosition(worldX, worldY);
            }
            return true;
        }
        return false;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(10, 10, 15, 240));
        g2.fillRoundRect(x - 3, y - 3, size + 6, size + 6, 12, 12);
        
        g2.setColor(new Color(30, 30, 40));
        g2.fillRoundRect(x, y, size, size, 8, 8);

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

        drawLanes(g2, scaleX, scaleY);

        for (var ancient : arena.ancients()) {
            int px = x + (int) (ancient.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (ancient.position().y() * Config.getTileSize() * scaleY);
            boolean isAlly = ancient.equipe().couleur() == playerTeam;
            g2.setColor(isAlly ? new Color(60, 100, 255) : new Color(200, 60, 60));
            g2.fillRect(px - 6, py - 6, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(px - 6, py - 6, 12, 12);
        }

        for (var tour : arena.tours()) {
            int px = x + (int) (tour.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (tour.position().y() * Config.getTileSize() * scaleY);
            boolean isAlly = tour.equipe().couleur() == playerTeam;
            g2.setColor(isAlly ? new Color(80, 180, 255) : new Color(255, 80, 80));
            if (tour.tier() == 1) {
                g2.fillRect(px - 3, py - 3, 6, 6);
            } else if (tour.tier() == 2) {
                g2.fillRect(px - 4, py - 4, 8, 8);
            } else {
                g2.fillRect(px - 5, py - 5, 10, 10);
            }
        }

        drawFountains(g2, scaleX, scaleY);

        int playerX = x + (int) (player.getX() * scaleX);
        int playerY = y + (int) (player.getY() * scaleY);
        g2.setColor(new Color(100, 255, 100));
        g2.fillOval(playerX - 5, playerY - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(playerX - 5, playerY - 5, 10, 10);

        if (playerTeam == TeamColor.BLUE) {
            for (var ally : arena.tours()) {
                if (ally.equipe().couleur() == playerTeam) {
                    int px = x + (int) (ally.position().x() * Config.getTileSize() * scaleX);
                    int py = y + (int) (ally.position().y() * Config.getTileSize() * scaleY);
                    g2.setColor(new Color(80, 180, 255, 150));
                    g2.fillRect(px - 2, py - 2, 4, 4);
                }
            }
        }

        if (camera != null) {
            int camX = x + (int) (camera.getX() * scaleX);
            int camY = y + (int) (camera.getY() * scaleY);
            int camW = (int) (camera.getViewportWidth() * scaleX);
            int camH = (int) (camera.getViewportHeight() * scaleY);
            
            camX = Math.max(x, Math.min(camX, x + size - 1));
            camY = Math.max(y, Math.min(camY, y + size - 1));
            camW = Math.min(camW, x + size - camX);
            camH = Math.min(camH, y + size - camY);
            
            if (camW > 0 && camH > 0) {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(camX, camY, camW, camH);
            }
        }

        g2.setColor(new Color(150, 150, 170));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("RMB: Move", x + 5, y + size - 8);
    }

    private void drawLanes(Graphics2D g2, float scaleX, float scaleY) {
        g2.setColor(new Color(90, 80, 60, 60));
        
        int midX = tileMap.getColumns() / 2;
        int midY = tileMap.getRows() / 2;
        
        int midColX = x + (int)(midX * Config.getTileSize() * scaleX);
        g2.fillRect(midColX - 2, y, 4, size);
        
        int midRowY = y + (int)(midY * Config.getTileSize() * scaleY);
        g2.fillRect(x, midRowY - 2, size, 4);
    }

    private void drawFountains(Graphics2D g2, float scaleX, float scaleY) {
        for (var tour : arena.tours()) {
            if (tour.tier() == 1) {
                int px = x + (int) (tour.position().x() * Config.getTileSize() * scaleX);
                int py = y + (int) (tour.position().y() * Config.getTileSize() * scaleY);
                g2.setColor(new Color(100, 255, 200, 180));
                g2.fillOval(px - 4, py - 4, 8, 8);
            }
        }
    }

    private Color getTileColor(int tileId) {
        return switch (tileId) {
            case 0 -> new Color(45, 95, 45);
            case 1 -> new Color(60, 80, 50);
            case 2 -> new Color(40, 80, 150);
            case 3 -> new Color(100, 90, 70);
            case 4 -> new Color(160, 140, 100);
            case 5 -> new Color(35, 85, 35);
            case 6 -> new Color(45, 95, 45);
            case 7 -> new Color(50, 100, 50);
            case 8 -> new Color(40, 80, 140);
            case 9 -> new Color(30, 70, 30);
            case 10 -> new Color(55, 105, 55);
            case 11 -> new Color(55, 105, 55);
            case 12 -> new Color(40, 90, 40);
            case 13 -> new Color(45, 95, 45);
            case 14 -> new Color(45, 95, 45);
            case 18 -> new Color(50, 100, 50);
            case 20, 21 -> new Color(70, 70, 80);
            case 22, 23 -> new Color(60, 60, 70);
            default -> new Color(45, 45, 55);
        };
    }
}

class CharacterPanelRenderer {
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
        this.calculatedHeight = 145;
    }

    public int getHeight() {
        return calculatedHeight;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 220));
        g2.fillRoundRect(x, y, width, calculatedHeight, 10, 10);
        g2.setColor(new Color(80, 80, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, width, calculatedHeight, 10, 10);

        g2.setColor(new Color(30, 30, 50));
        g2.fillRect(x + 12, y + 12, 48, 48);

        if (player.getHero() != null) {
            var heroSprite = spriteCache.getSprite(player.getHero(), Direction.DOWN, 1);
            if (heroSprite != null) {
                g2.drawImage(heroSprite, x + 14, y + 14, 44, 44, null);
            }
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(player.getHero().getName(), x + 70, y + 20);
            
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String category = switch (player.getHero().getCategoryId()) {
                case 1 -> "Force";
                case 2 -> "Agilite";
                case 3 -> "Intelligence";
                default -> "Unknown";
            };
            g2.setColor(new Color(180, 180, 200));
            g2.drawString("Lv." + player.level() + " " + category, x + 70, y + 34);
        }

        int barX = x + 12;
        int barY = y + 68;
        int barW = width - 24;
        int barH = 14;

        drawBar(g2, barX, barY, barW, barH, player.stats().hp(), player.stats().maxHp(), 
            new Color(50, 180, 50), new Color(40, 60, 40), "HP");
        
        barY += 22;
        drawBar(g2, barX, barY, barW, barH, player.stats().mana(), player.stats().maxMana(), 
            new Color(50, 100, 200), new Color(40, 80, 180), "MANA");

        int statsX = x + 12;
        int statsY = y + 115;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        String atk = "ATK: " + player.stats().attack();
        String def = "DEF: " + player.stats().defense();
        String spd = "SPD: " + String.format("%.0f", player.stats().moveSpeed());
        g2.drawString(atk, statsX, statsY);
        g2.drawString(def, statsX + 70, statsY);
        g2.drawString(spd, statsX + 140, statsY);
    }

    private void drawBar(Graphics2D g2, int x, int y, int width, int height, int current, int max, 
                        Color fillColor, Color bgColor, String label) {
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, width, height, 3, 3);
        
        if (max > 0) {
            int fillWidth = (int) ((double) current / max * width);
            g2.setColor(fillColor);
            g2.fillRoundRect(x, y, fillWidth, height, 3, 3);
        }
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 9));
        String text = current + "/" + max;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + height - 2);
        
        g2.setColor(new Color(200, 200, 220));
        g2.setFont(new Font("Arial", Font.PLAIN, 8));
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
    private long matchStartTime;

    public ScoreboardRenderer(Arena arena, Player player, int x, int y, int width, int height) {
        this.arena = arena;
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.matchStartTime = System.currentTimeMillis();
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(20, 20, 30, 200));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        long elapsed = System.currentTimeMillis() - matchStartTime;
        int minutes = (int) (elapsed / 60000);
        int seconds = (int) ((elapsed % 60000) / 1000);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(timerText, x + (width - fm.stringWidth(timerText)) / 2, y + 22);

        g2.setColor(new Color(80, 180, 255));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        String blueScore = "Blue: " + arena.getBlueKills();
        g2.drawString(blueScore, x + 10, y + 45);

        g2.setColor(new Color(255, 80, 80));
        String redScore = "Red: " + arena.getRedKills();
        g2.drawString(redScore, x + 10, y + 62);
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