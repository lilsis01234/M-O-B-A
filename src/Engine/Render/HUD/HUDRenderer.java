package Engine.Render.HUD;

import Core.Entity.Player;
import Core.Moba.World.Arena;
import Core.Tile.TileMap;
import Engine.Render.Camera;

import java.awt.*;

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
    private BuffRenderer buffRenderer;
    private Camera camera;
    private java.util.function.Consumer<Point> moveTargetConsumer;

    public HUDRenderer(Player player, Arena arena, TileMap tileMap, int screenWidth, int screenHeight) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Minimap - top right with small margin
        int minimapSize = 180;
        this.minimap = new MinimapRenderer(player, arena, tileMap, 
            screenWidth - minimapSize - 5, 5, minimapSize);
        
        // Scoreboard - top left
        this.scoreboard = new ScoreboardRenderer(arena, player, 0, 0, 140, 50);
        
        // Gold - below minimap
        this.goldDisplay = new GoldDisplayRenderer(player, screenWidth - 90, minimapSize + 10, 80, 18);
        
        // Character Panel - bottom left
        this.characterPanel = new CharacterPanelRenderer(player, 0, screenHeight - 125, 200);
        
        // Ability Bar - bottom center, stuck to bottom edge
        int abilityBarWidth = 300;
        int abilityBarX = (screenWidth - abilityBarWidth) / 2;
        this.abilityBar = new AbilityBarRenderer(player, abilityBarX, screenHeight - 54, abilityBarWidth, 54);
        
        // Item Bar - bottom right, stuck to bottom edge
        int itemBarWidth = 210;
        this.itemBar = new ItemBarRenderer(player, screenWidth - itemBarWidth, screenHeight - 55, itemBarWidth, 55);
        
        // Target Info - left side
        this.targetInfo = new TargetInfoRenderer(player, 0, screenHeight - 260, 180, 90);
        
        this.buffRenderer = null;
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

    public boolean handleRightClick(int clickX, int clickY) {
        return minimap.handleRightClick(clickX, clickY);
    }

    public int getMinimapX() {
        return minimap.getX();
    }

    public int getMinimapY() {
        return minimap.getY();
    }

    public int getMinimapSize() {
        return minimap.getSize();
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