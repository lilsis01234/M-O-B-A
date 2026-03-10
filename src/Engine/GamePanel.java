package Engine;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.Units.Tour;
import Core.Moba.Units.Ancient;
import Core.Moba.World.*;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Engine.Input.KeyHandler;
import Engine.Input.MouseHandler;
import Engine.Render.*;
import Engine.Tile.MapParser;
import Engine.Tile.Tile;
import Engine.Tile.TileLoader;
import Engine.Tile.TileRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    
    private final KeyHandler keyHandler;
    private final MouseHandler mouseHandler;
    private final Player player;
    private final TileRenderer tileRenderer;
    private final PlayerRenderer playerRenderer;
    private final TowerRenderer towerRenderer;
    private final Camera camera;
    private final List<ClickEffect> clickEffects = new ArrayList<>();
    private final Arena arena;
    private Thread gameThread;
    
    public GamePanel() {
        setPreferredSize(new Dimension(EngineConfig.getScreenWidth(), EngineConfig.getScreenHeight()));
        setBackground(Color.black);
        setDoubleBuffered(true);
        
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        camera = new Camera(EngineConfig.getScreenWidth(), EngineConfig.getScreenHeight());
        mouseHandler.setCamera(camera);

        MapParser mapParser = new MapParser();
        MapParser.MapData mapData = mapParser.parse(EngineConfig.getMapFilePath());
        TileMap tileMap = new TileMap(mapData.tileNumbers(), mapData.columns(), mapData.rows());
        camera.setWorldSize(tileMap.getColumns() * Config.getTileSize(), tileMap.getRows() * Config.getTileSize());
        
        // Initialize camera at bottom-left
        camera.setX(0);
        camera.setY((float) (camera.getWorldHeight() - camera.getViewportHeight() / camera.getZoom()));

        TileLoader tileLoader = new TileLoader();
        Tile[] tiles = tileLoader.load(EngineConfig.getMapFilePath(), EngineConfig.getMaxTiles());
        CollisionTable collisionTable = new CollisionTable(tileLoader.buildCollisionTable(tiles));

        tileRenderer = new TileRenderer(tileMap, tiles);
        
        // --- Initialize MOBA Elements ---
        arena = new Arena();
        
        // Define Team 1 (Blue)
        Base blueBase = new Base(5000);
        Fontaine blueFontaine = new Fontaine(new Vec2(5, 95), 100, 50);
        Equipe blueTeam = new Equipe("Radiant", TeamColor.BLUE, blueBase, blueFontaine);
        
        // Define Team 2 (Red)
        Base redBase = new Base(5000);
        Fontaine redFontaine = new Fontaine(new Vec2(95, 5), 100, 50);
        Equipe redTeam = new Equipe("Dire", TeamColor.RED, redBase, redFontaine);
        
        arena.initializeFromMap(tileMap, blueTeam, redTeam);
        
        // Player must be created AFTER arena is initialized
        player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena);
        playerRenderer = new PlayerRenderer(new PlayerSprites());
        towerRenderer = new TowerRenderer();
        towerRenderer.setTiles(tiles);
        
        addKeyListener(keyHandler);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        setFocusable(true);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                
                int cols = width / Config.getTileSize();
                int rows = height / Config.getTileSize();
                
                EngineConfig.updateScreenSize(cols * Config.getTileSize(), rows * Config.getTileSize());
                camera.setViewportSize(width, height);
                
                repaint();
            }
        });
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        
        while (gameThread != null) {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastTime;
            
            if (elapsedTime >= Config.getNanosecondsPerFrame()) {
                lastTime = currentTime;
                
                update();
                repaint();
            }
        }
    }
    
    private void update() {
        camera.update(mouseHandler.getCurrentX(), mouseHandler.getCurrentY());
        camera.zoom(mouseHandler.getWheelRotation());
        
        if (mouseHandler.hasNewClick()) {
            clickEffects.add(new ClickEffect(mouseHandler.getLastClickWorldX(), mouseHandler.getLastClickWorldY()));
            mouseHandler.clearNewClick();
        }

        Iterator<ClickEffect> it = clickEffects.iterator();
        while (it.hasNext()) {
            ClickEffect effect = it.next();
            effect.update();
            if (effect.isDead()) {
                it.remove();
            }
        }

        player.update();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;

        // Apply camera transformations
        AffineTransform oldTransform = g2.getTransform();
        g2.scale(camera.getZoom(), camera.getZoom());
        g2.translate(-camera.getX(), -camera.getY());

        // Draw tiles
        tileRenderer.draw(g2, camera, getWidth(), getHeight());

        // Draw towers
        for (Tour tour : arena.tours()) {
            towerRenderer.draw(g2, tour, camera);
        }

        // Draw ancients
        for (Ancient ancient : arena.ancients()) {
            towerRenderer.drawAncient(g2, ancient, camera);
        }

        // Draw click effects
        for (ClickEffect effect : clickEffects) {
            effect.draw(g2);
        }

        // Draw player
        playerRenderer.draw(g2, player);

        g2.setTransform(oldTransform);
        
        // UI overlay (untransformed)
        g2.setColor(Color.white);
        g2.drawString("FPS: " + (int) (1_000_000_000.0 / Config.getNanosecondsPerFrame()), 10, 20);

        g2.dispose();
    }
}
