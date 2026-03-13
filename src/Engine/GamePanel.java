package Engine;

import Core.Config;
import Core.Database.model.Hero;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

/**
 * Panel principal du jeu.
 * Gère l'affichage (rendering) et initialise tous les composants.
 */
public class GamePanel extends JPanel {
    
    private enum GameState {
        HERO_SELECTION,
        PLAYING
    }
    
    private GameState currentState = GameState.HERO_SELECTION;
    
    private final KeyHandler keyHandler;
    private final MouseHandler mouseHandler;
    private Player player;
    private TileRenderer tileRenderer;
    private PlayerRenderer playerRenderer;
    private TowerRenderer towerRenderer;
    private ProjectileRenderer projectileRenderer;
    private final Camera camera;
    private Arena arena;
    private GameEngine gameEngine;
    
    private HeroSelectionPanel heroSelectionPanel;
    private Hero selectedHero;
    
    public GamePanel() {
        setPreferredSize(new Dimension(Config.getScreenWidth(), Config.getScreenHeight()));
        setBackground(Color.black);
        setDoubleBuffered(true);
        setLayout(null); // Use absolute positioning
        
        // Initialisation des handlers d'input
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        camera = new Camera(Config.getScreenWidth(), Config.getScreenHeight());
        mouseHandler.setCamera(camera);
        
        setupInputListeners();
        setupResizeListener();
        
        // Initialize hero selection panel as a proper Swing component
        heroSelectionPanel = new HeroSelectionPanel(new Dimension(Config.getScreenWidth(), Config.getScreenHeight()));
        heroSelectionPanel.setBounds(0, 0, Config.getScreenWidth(), Config.getScreenHeight());
        heroSelectionPanel.setVisible(true);
        heroSelectionPanel.setSelectionListener(hero -> {
            selectedHero = hero;
            startGame();
        });
        add(heroSelectionPanel);
        
        // Initially hide game components
        currentState = GameState.HERO_SELECTION;
    }

    private TileMap loadTileMap() {
        MapParser mapParser = new MapParser();
        MapParser.MapData mapData = mapParser.parse(Config.getMapFilePath());
        return new TileMap(mapData.tileNumbers(), mapData.columns(), mapData.rows());
    }

    private void setupCamera(TileMap tileMap) {
        camera.setWorldSize(tileMap.getColumns() * Config.getTileSize(), tileMap.getRows() * Config.getTileSize());
        camera.setX(0);
        camera.setY((float) (camera.getWorldHeight() - camera.getViewportHeight() / camera.getZoom()));
    }

    private Tile[] loadTiles() {
        TileLoader tileLoader = new TileLoader();
        return tileLoader.load(Config.getMapFilePath(), Config.getMaxTiles());
    }

    private boolean[] buildCollisionTable(Tile[] tiles) {
        TileLoader tileLoader = new TileLoader();
        return tileLoader.buildCollisionTable(tiles);
    }

    private Arena createArena(TileMap tileMap) {
        Arena arena = new Arena();
        
        // Équipe bleue (Radiant)
        Base blueBase = new Base(5000);
        Fontaine blueFontaine = new Fontaine(new Vec2(5, 95), 100, 50);
        Equipe blueTeam = new Equipe("Radiant", TeamColor.BLUE, blueBase, blueFontaine);
        
        // Équipe rouge (Dire)
        Base redBase = new Base(5000);
        Fontaine redFontaine = new Fontaine(new Vec2(95, 5), 100, 50);
        Equipe redTeam = new Equipe("Dire", TeamColor.RED, redBase, redFontaine);
        
        arena.initializeFromMap(tileMap, blueTeam, redTeam);
        
        return arena;
    }

    private Player createPlayerWithHero(TileMap tileMap, CollisionTable collisionTable, Arena arena, Hero hero) {
        Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);
        arena.ajouterUnite(player);
        return player;
    }

    private TowerRenderer createTowerRenderer(Tile[] tiles) {
        TowerRenderer renderer = new TowerRenderer();
        renderer.setTiles(tiles);
        return renderer;
    }

    private void setupInputListeners() {
        addKeyListener(keyHandler);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        setFocusable(true);
    }

    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                handleResize();
            }
        });
    }
    
    private void handleSelectionConfirm() {
        if (currentState == GameState.HERO_SELECTION) {
            Hero hero = heroSelectionPanel.getSelectedHero();
            if (hero != null) {
                selectedHero = hero;
                startGame();
            }
        }
    }
    
    private void startGame() {
        try {
            // Load the game components with the selected hero
            TileMap tileMap = loadTileMap();
            setupCamera(tileMap);
            
            Tile[] tiles = loadTiles();
            CollisionTable collisionTable = new CollisionTable(buildCollisionTable(tiles));
            
            tileRenderer = new TileRenderer(tileMap, tiles);
            arena = createArena(tileMap);
            
            // Create player with selected hero
            player = createPlayerWithHero(tileMap, collisionTable, arena, selectedHero);
            playerRenderer = new PlayerRenderer(player);
            towerRenderer = createTowerRenderer(tiles);
            projectileRenderer = new ProjectileRenderer();
            
            // Initialize game engine
            gameEngine = new GameEngine(player, camera, mouseHandler, arena);
            
            // Remove hero selection panel and change state
            remove(heroSelectionPanel);
            currentState = GameState.PLAYING;
            revalidate();
            repaint();
            
            // Start the game thread
            startGameThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleResize() {
        int width = getWidth();
        int height = getHeight();
        
        int cols = width / Config.getTileSize();
        int rows = height / Config.getTileSize();
        
        Config.updateScreenSize(cols * Config.getTileSize(), rows * Config.getTileSize());
        camera.setViewportSize(width, height);
        
        // Update hero selection panel size if it's still visible
        if (currentState == GameState.HERO_SELECTION) {
            heroSelectionPanel.setSize(width, height);
            heroSelectionPanel.revalidate();
        }
        
        repaint();
    }
    
    public void startGameThread() {
        gameEngine.start();
        
        Thread renderThread = new Thread(this::renderLoop);
        renderThread.start();
    }
    
    private void renderLoop() {
        while (true) {
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (currentState == GameState.PLAYING) {
            Graphics2D g2 = (Graphics2D) g;
            drawGameWorld(g2);
            drawUI(g2);
            g2.dispose();
        }
        // Hero selection panel handles its own painting when visible
    }

    private void drawGameWorld(Graphics2D g2) {
        // Application des transformations caméra (zoom, pan)
        AffineTransform oldTransform = g2.getTransform();
        g2.scale(camera.getZoom(), camera.getZoom());
        g2.translate(-camera.getX(), -camera.getY());

        // Dessin du monde
        tileRenderer.draw(g2, camera, getWidth(), getHeight());
        
        // Update tower animations
        for (Tour tour : arena.tours()) {
            tour.updateAnimation();
        }
        
        // Depth-sorted rendering: draw entities based on Y position
        drawDepthSorted(g2);
        
        drawProjectiles(g2);
        drawClickEffects(g2);

        g2.setTransform(oldTransform);
    }
    
    private void drawDepthSorted(Graphics2D g2) {
        java.util.List<RenderableEntity> entities = new java.util.ArrayList<>();
        
        int tileSize = Config.getTileSize();
        
        // Add towers
        for (Tour tour : arena.tours()) {
            double towerBaseY = (tour.position().y() + tour.height()) * tileSize;
            entities.add(new RenderableEntity(towerBaseY, RenderableEntity.Type.TOWER, tour));
        }
        
        // Add ancients
        for (Ancient ancient : arena.ancients()) {
            double ancientBaseY = (ancient.position().y() + ancient.height()) * tileSize;
            entities.add(new RenderableEntity(ancientBaseY, RenderableEntity.Type.ANCIENT, ancient));
        }
        
        // Add player
        if (player.isAlive()) {
            double playerBaseY = player.getY() + tileSize;
            entities.add(new RenderableEntity(playerBaseY, RenderableEntity.Type.PLAYER, player));
        }
        
        // Sort by Y position (lower Y = behind, higher Y = in front)
        entities.sort(java.util.Comparator.comparingDouble(e -> e.renderY));
        
        // Draw in sorted order
        for (RenderableEntity entity : entities) {
            switch (entity.type) {
                case TOWER -> towerRenderer.draw(g2, (Tour) entity.entity, camera);
                case ANCIENT -> towerRenderer.drawAncient(g2, (Ancient) entity.entity, camera);
                case PLAYER -> playerRenderer.draw(g2, player);
            }
        }
    }
    
    private static class RenderableEntity {
        enum Type { TOWER, ANCIENT, PLAYER }
        final double renderY;
        final Type type;
        final Object entity;
        
        RenderableEntity(double renderY, Type type, Object entity) {
            this.renderY = renderY;
            this.type = type;
            this.entity = entity;
        }
    }

    private void drawTowers(Graphics2D g2) {
        for (Tour tour : arena.tours()) {
            towerRenderer.draw(g2, tour, camera);
        }
    }

    private void drawAncients(Graphics2D g2) {
        for (Ancient ancient : arena.ancients()) {
            towerRenderer.drawAncient(g2, ancient, camera);
        }
    }

    private void drawProjectiles(Graphics2D g2) {
        for (var projectile : java.util.List.copyOf(gameEngine.getProjectiles())) {
            projectileRenderer.draw(g2, projectile, camera);
        }
    }

    private void drawClickEffects(Graphics2D g2) {
        for (ClickEffect effect : java.util.List.copyOf(gameEngine.getClickEffects())) {
            effect.draw(g2);
        }
    }

    private void drawUI(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.drawString("FPS: " + (int) (1_000_000_000.0 / Config.getNanosecondsPerFrame()), 10, 20);
        
        if (!player.isAlive()) {
            double timeLeft = player.getRespawnTimeRemaining();
            g2.setColor(Color.RED);
            g2.drawString(String.format("Respawn in: %.1f s", timeLeft), 10, 40);
        }
        
        if (player.isInFountain()) {
            g2.setColor(Color.CYAN);
            g2.drawString("In Fountain - Healing/Mana Regen", 10, 80);
        }
        
        if (player.isOnEnemyWood()) {
            g2.setColor(Color.RED);
            g2.drawString("ENEMY TERRITORY - Taking Damage!", 10, 100);
        }
    }
}
