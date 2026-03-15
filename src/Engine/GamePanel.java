package Engine;

import Core.Config;
import Core.Database.model.Hero;
import Core.Entity.Player;
import Core.Moba.Units.Tour;
import Core.Moba.Units.CoreBase;
import Core.Moba.World.*;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Engine.Input.KeyHandler;
import Engine.Input.MouseHandler;
import Engine.Render.Camera;
import Engine.Render.ClickEffect;
import Engine.Render.DebugRenderer;
import Engine.Render.HUD.HUDRenderer;
import Engine.Render.World.*;
import Engine.Tile.MapParser;
import Engine.Tile.Tile;
import Engine.Tile.TileLoader;
import Engine.UI.MainPanel;
import Engine.UI.PauseMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Panel principal du jeu.
 * Gère l'affichage (rendering) et initialise tous les composants.
 */
public class GamePanel extends JPanel {
    
    private enum GameState {
        MAIN_MENU,
        HERO_SELECTION,
        PLAYING,
        PAUSED
    }
    
    private GameState currentState = GameState.MAIN_MENU;
    
    private final KeyHandler keyHandler;
    private final MouseHandler mouseHandler;
    private Player player;
    private TileRenderer tileRenderer;
    private PlayerRenderer playerRenderer;
    private TowerRenderer towerRenderer;
    private CoreBaseRenderer coreBaseRenderer;
    private ProjectileRenderer projectileRenderer;
    private HUDRenderer hudRenderer;
    private final Camera camera;
    private Arena arena;
    private GameEngine gameEngine;
    
    private HeroSelectionPanel heroSelectionPanel;
    private MainPanel mainPanel;
    private PauseMenu pauseMenu;
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
        
        mainPanel = new MainPanel(new Dimension(Config.getScreenWidth(), Config.getScreenHeight()));
        mainPanel.setBounds(0, 0, Config.getScreenWidth(), Config.getScreenHeight());
        mainPanel.setVisible(true);
        mainPanel.setMenuListener(new MainPanel.MainMenuListener() {
            @Override
            public void onStartGame() {
                showHeroSelection();
            }

            @Override
            public void onSettings() {
                System.out.println("Settings clicked");
            }

            @Override
            public void onExit() {
                System.exit(0);
            }
        });
        add(mainPanel);
        
        pauseMenu = new PauseMenu();
        pauseMenu.setBounds(0, 0, Config.getScreenWidth(), Config.getScreenHeight());
        pauseMenu.setPauseMenuListener(new PauseMenu.PauseMenuListener() {
            @Override
            public void onResume() {
                resumeGame();
            }

            @Override
            public void onReturnToMain() {
                returnToMainMenu();
            }

            @Override
            public void onSettings() {
                System.out.println("Settings from pause menu");
            }
        });
        add(pauseMenu);
        
        heroSelectionPanel = new HeroSelectionPanel(new Dimension(Config.getScreenWidth(), Config.getScreenHeight()));
        heroSelectionPanel.setBounds(0, 0, Config.getScreenWidth(), Config.getScreenHeight());
        heroSelectionPanel.setVisible(true);  // Keep visible but will be hidden initially
        heroSelectionPanel.setSelectionListener(new HeroSelectionPanel.SelectionListener() {
            @Override
            public void onHeroSelected(Hero hero) {
                selectedHero = hero;
                startGame();
            }

            @Override
            public void onGoBack() {
                showMainMenu();
            }
        });
        // Don't add heroSelectionPanel here - we'll add it when needed
        // add(heroSelectionPanel);
        
        currentState = GameState.MAIN_MENU;
        // Initially hide heroSelectionPanel and mainPanel is visible
        heroSelectionPanel.setVisible(false);
    }

    private void showHeroSelection() {
        if (mainPanel != null) {
            mainPanel.setVisible(false);
        }
        // Hide pause menu if visible
        if (pauseMenu != null) {
            pauseMenu.hide();
        }
        // Remove the GamePanel's mouse handler from heroSelectionPanel if it was added
        heroSelectionPanel.removeMouseListener(mouseHandler);
        heroSelectionPanel.removeMouseMotionListener(mouseHandler);
        
        if (getComponentZOrder(heroSelectionPanel) < 0) {
            add(heroSelectionPanel);
        }
        heroSelectionPanel.setSize(getWidth(), getHeight());
        heroSelectionPanel.setVisible(true);
        currentState = GameState.HERO_SELECTION;
        revalidate();
        repaint();
        heroSelectionPanel.requestFocusInWindow();
    }

    private void showMainMenu() {
        if (gameEngine != null) {
            gameEngine.stop();
            gameEngine = null;
        }
        
        if (heroSelectionPanel != null) {
            heroSelectionPanel.setVisible(false);
        }
        if (mainPanel != null) {
            mainPanel.setSize(getWidth(), getHeight());
            mainPanel.setVisible(true);
        }
        currentState = GameState.MAIN_MENU;
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        revalidate();
        repaint();
        if (mainPanel != null) {
            mainPanel.requestFocusInWindow();
        }
    }

    private void returnToMainMenu() {
        showMainMenu();
    }

    private void pauseGame() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
            if (gameEngine != null) {
                gameEngine.pause();
            }
            pauseMenu.setSize(getWidth(), getHeight());
            pauseMenu.showAt(mouseHandler.getCurrentX(), mouseHandler.getCurrentY(), getWidth(), getHeight());
            pauseMenu.requestFocusInWindow();
            setComponentZOrder(pauseMenu, 0);
            updateCursor();
            repaint();
        }
    }

    private void resumeGame() {
        if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
            if (gameEngine != null) {
                gameEngine.resume();
            }
            pauseMenu.hide();
            hudRenderer.resetPauseButtonHover();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            repaint();
        }
    }
    
    private void updateCursor() {
        boolean showPointer = false;
        if (currentState == GameState.PLAYING) {
            showPointer = hudRenderer.shouldShowPointerCursor();
        } else if (currentState == GameState.PAUSED) {
            showPointer = pauseMenu.isAnyButtonHovered();
        }
        
        if (showPointer) {
            if (getCursor().getType() != Cursor.HAND_CURSOR) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else {
            if (getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
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
        
        mouseHandler.setLeftClickCallback(point -> {
            if (currentState == GameState.MAIN_MENU) {
                mainPanel.handleMouseClick(point.x, point.y);
            } else if (currentState == GameState.PAUSED && pauseMenu.isPauseMenuVisible()) {
                pauseMenu.handleMouseClick(point.x, point.y);
            } else if (currentState == GameState.HERO_SELECTION) {
                heroSelectionPanel.handleMouseClick(point.x, point.y, java.awt.event.MouseEvent.BUTTON1);
            } else if (currentState == GameState.PLAYING) {
                if (!hudRenderer.handleMouseClick(point.x, point.y)) {
                    mouseHandler.setTargetFromScreen(point.x, point.y);
                }
            }
        });
        
        mouseHandler.setRightClickCallback(point -> {
            if (currentState == GameState.MAIN_MENU) {
                return false;
            }
            if (currentState == GameState.PAUSED && pauseMenu.isPauseMenuVisible()) {
                return false;
            }
            if (currentState == GameState.HERO_SELECTION) {
                heroSelectionPanel.handleMouseClick(point.x, point.y, java.awt.event.MouseEvent.BUTTON3);
                return true;
            }
            if (currentState == GameState.PLAYING) {
                return hudRenderer.handleRightClick(point.x, point.y);
            }
            return false;
        });
        
        mouseHandler.setMouseMoveCallback(point -> {
            switch (currentState) {
                case MAIN_MENU -> {
                    mainPanel.handleMouseMove(point.x, point.y, this);
                }
                case HERO_SELECTION -> {
                    heroSelectionPanel.handleMouseMove(point.x, point.y, this);
                }
                case PLAYING -> {
                    hudRenderer.handleMouseMove(point.x, point.y);
                    boolean showPointer = hudRenderer.shouldShowPointerCursor();
                    if (showPointer) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                case PAUSED -> {
                    if (pauseMenu.isPauseMenuVisible()) {
                        pauseMenu.handleMouseMove(point.x, point.y);
                    }
                    boolean showPointer = pauseMenu.isAnyButtonHovered();
                    if (showPointer) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        });
        
        mouseHandler.setMouseWheelCallback(rotation -> {
            if (currentState == GameState.HERO_SELECTION) {
                heroSelectionPanel.handleMouseWheel(rotation);
            }
        });
        
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
            coreBaseRenderer = new CoreBaseRenderer();
            coreBaseRenderer.setTiles(tiles);
            projectileRenderer = new ProjectileRenderer();
            
            // Create HUD renderer
            hudRenderer = new HUDRenderer(player, arena, tileMap, tiles, getWidth(), getHeight());
            hudRenderer.setCamera(camera);
            camera.setMinimapBounds(
                hudRenderer.getMinimapX(), 
                hudRenderer.getMinimapY(), 
                hudRenderer.getMinimapSize()
            );
            hudRenderer.setMoveTargetConsumer(target -> {
                mouseHandler.setTarget(target.x, target.y);
            });
            
            hudRenderer.setPauseCallback(v -> {
                if (currentState == GameState.PLAYING) {
                    pauseGame();
                } else if (currentState == GameState.PAUSED) {
                    resumeGame();
                }
            });
            
            // Initialize game engine
            gameEngine = new GameEngine(player, camera, mouseHandler, arena);
            
            // Tab key to center camera on player
            keyHandler.setTabCallback(v -> {
                gameEngine.centerCameraOnPlayer();
            });
            
            // ESC key to toggle pause
            keyHandler.setEscapeCallback(v -> {
                if (currentState == GameState.PLAYING) {
                    pauseGame();
                } else if (currentState == GameState.PAUSED) {
                    resumeGame();
                }
            });
            
            // Remove hero selection panel and change state
            remove(heroSelectionPanel);
            currentState = GameState.PLAYING;
            revalidate();
            repaint();
            
            // Request focus for keyboard input
            requestFocusInWindow();
            
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
        
        // Update HUD renderer with new screen size
        if (hudRenderer != null) {
            hudRenderer.setScreenSize(width, height);
        }
        
        // Update main panel size if visible
        if (mainPanel != null && currentState == GameState.MAIN_MENU) {
            mainPanel.setSize(width, height);
            mainPanel.revalidate();
        }
        
        // Update hero selection panel size if it's still visible
        if (currentState == GameState.HERO_SELECTION) {
            heroSelectionPanel.setSize(width, height);
            heroSelectionPanel.revalidate();
        }
        
        // Update pause menu size if visible
        if (pauseMenu != null && pauseMenu.isPauseMenuVisible()) {
            pauseMenu.setSize(width, height);
            pauseMenu.revalidate();
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
            updateCursor();
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
        
        if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
            Graphics2D g2 = (Graphics2D) g;
            drawGameWorld(g2);
            drawUI(g2);
            
            if (currentState == GameState.PAUSED && pauseMenu != null && pauseMenu.isPauseMenuVisible()) {
                pauseMenu.paint(g);
            }
            
            g2.dispose();
        }
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
        
        // Add core bases
        for (CoreBase coreBase : arena.coreBases()) {
            double coreBaseBaseY = (coreBase.position().y() + coreBase.height()) * tileSize;
            entities.add(new RenderableEntity(coreBaseBaseY, RenderableEntity.Type.CORE_BASE, coreBase));
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
                case CORE_BASE -> coreBaseRenderer.draw(g2, (CoreBase) entity.entity, camera);
                case PLAYER -> playerRenderer.draw(g2, player);
            }
        }
    }
    
    private static class RenderableEntity {
        enum Type { TOWER, CORE_BASE, PLAYER }
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

    private void drawCoreBases(Graphics2D g2) {
        for (CoreBase coreBase : arena.coreBases()) {
            coreBaseRenderer.draw(g2, coreBase, camera);
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
        if (hudRenderer != null) {
            hudRenderer.render(g2);
        }
    }
}