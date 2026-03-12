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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

/**
 * Panel principal du jeu.
 * Gère l'affichage (rendering) et initialise tous les composants.
 */
public class GamePanel extends JPanel {
    
    private final KeyHandler keyHandler;
    private final MouseHandler mouseHandler;
    private final Player player;
    private final TileRenderer tileRenderer;
    private final PlayerRenderer playerRenderer;
    private final TowerRenderer towerRenderer;
    private final ProjectileRenderer projectileRenderer;
    private final Camera camera;
    private final Arena arena;
    private final GameEngine gameEngine;
    
    public GamePanel() {
        setPreferredSize(new Dimension(Config.getScreenWidth(), Config.getScreenHeight()));
        setBackground(Color.black);
        setDoubleBuffered(true);
        
        // Initialisation des handlers d'input
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        camera = new Camera(Config.getScreenWidth(), Config.getScreenHeight());
        mouseHandler.setCamera(camera);

        // Chargement de la carte
        TileMap tileMap = loadTileMap();
        setupCamera(tileMap);
        
        // Chargement des tuiles
        Tile[] tiles = loadTiles();
        CollisionTable collisionTable = new CollisionTable(buildCollisionTable(tiles));

        tileRenderer = new TileRenderer(tileMap, tiles);
        
        // Création de l'arène (tours, bases)
        arena = createArena(tileMap);
        
        // Création du joueur
        player = createPlayer(tileMap, collisionTable, arena);
        playerRenderer = new PlayerRenderer(new PlayerSprites());
        towerRenderer = createTowerRenderer(tiles);
        projectileRenderer = new ProjectileRenderer();
        
        setupInputListeners();
        setupResizeListener();
        
        // Initialisation du moteur de jeu
        gameEngine = new GameEngine(player, camera, mouseHandler, arena);
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

    private Player createPlayer(TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena);
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

    private void handleResize() {
        int width = getWidth();
        int height = getHeight();
        
        int cols = width / Config.getTileSize();
        int rows = height / Config.getTileSize();
        
        Config.updateScreenSize(cols * Config.getTileSize(), rows * Config.getTileSize());
        camera.setViewportSize(width, height);
        
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
        
        Graphics2D g2 = (Graphics2D) g;
        drawGameWorld(g2);
        drawUI(g2);
        g2.dispose();
    }

    private void drawGameWorld(Graphics2D g2) {
        // Application des transformations caméra (zoom, pan)
        AffineTransform oldTransform = g2.getTransform();
        g2.scale(camera.getZoom(), camera.getZoom());
        g2.translate(-camera.getX(), -camera.getY());

        // Dessin du monde
        tileRenderer.draw(g2, camera, getWidth(), getHeight());
        drawTowers(g2);
        drawAncients(g2);
        drawProjectiles(g2);
        drawClickEffects(g2);
        if (player.isAlive()) {
            playerRenderer.draw(g2, player);
        }

        g2.setTransform(oldTransform);
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
    }
}
