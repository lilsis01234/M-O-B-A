package Engine.Render.HUD;

import Core.Entity.Player;
import Core.Moba.World.Arena;
import Core.Tile.TileMap;
import Engine.Render.Camera;
import Engine.Tile.Tile;

import java.awt.*;
//Classe principale de rendu du HUD 
public class HUDRenderer {
//attributs
    private final Player player;
    private final Arena arena;
    private final TileMap tileMap;
    private int screenWidth;
    private int screenHeight;

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
//contructeur
    public HUDRenderer(Player player, Arena arena, TileMap tileMap, Tile[] tiles, int screenWidth, int screenHeight) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        int minimapSize = 180;
        this.minimap = new MinimapRenderer(player, arena, tileMap, tiles, 0, 0, minimapSize);
     // Initialisation des autres composants hud
        this.scoreboard = new ScoreboardRenderer(arena, player, 0, 0, 120, 50);
        this.goldDisplay = new GoldDisplayRenderer(player, 0, 0, 80, 18);
        this.characterPanel = new CharacterPanelRenderer(player, 0, 0, 200);
        this.abilityBar = new AbilityBarRenderer(player, 0, 0, 300, 54);
        this.itemBar = new ItemBarRenderer(player, 0, 0, 210, 55);
        this.targetInfo = new TargetInfoRenderer(player, 0, 0, 180, 90);
        
        this.buffRenderer = null;
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
// Associe une caméra au HUD pour suivre le joueur et la minimap.
    public void setCamera(Camera camera) {
        this.camera = camera;
        this.minimap.setCamera(camera);
    }
 //Dé
 // definit le callback a exécuter lorsqu’un clic est effectue sur la minimap.
    public void setMoveTargetConsumer(java.util.function.Consumer<Point> consumer) {
        this.moveTargetConsumer = consumer;
        this.minimap.setOnClickCallback(consumer);
    }
//gestion clic
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
 // Activation de l’antialiasing pour texte et formes
    public void render(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int margin = 5;
        int minimapSize = 180;
        
        minimap.setPosition(screenWidth - minimapSize - margin, margin);
        minimap.render(g2);
        
        if (camera != null) {
            camera.setMinimapBounds(minimap.getX(), minimap.getY(), minimap.getSize());
        }
        
        scoreboard.render(g2, margin, margin);
        
        goldDisplay.render(g2, screenWidth - 90, minimapSize + margin * 2);
        
        characterPanel.setPosition(margin, screenHeight - 125);
        characterPanel.render(g2);
         // Barre de compétences
        int abilityBarWidth = 300;
        int abilityBarX = (screenWidth - abilityBarWidth) / 2;
        abilityBar.setPosition(abilityBarX, screenHeight - 54);
        abilityBar.render(g2);
         // Barre deobjet 
        int itemBarWidth = 210;
        itemBar.setPosition(screenWidth - itemBarWidth, screenHeight - 55);
        itemBar.render(g2);
        
        targetInfo.setPosition(margin, screenHeight - 260);
        targetInfo.render(g2, 0, 0);
 // Overlay si le joueur est en respawn
        drawRespawnOverlay(g2);
    }
//Affiche un overlay semi transparent avec le temps de respawn si le joueur est mort
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