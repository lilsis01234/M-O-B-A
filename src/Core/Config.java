package Core;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Config {
    
    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    
    private static final int MAX_FPS = 60;
    private static final double NANOSECONDS_PER_FRAME = 1_000_000_000.0 / MAX_FPS;
    
    private static final int PLAYER_DEFAULT_SPEED = 2;
    private static final int PLAYER_DEFAULT_X = 5 * TILE_SIZE;
    private static final int PLAYER_DEFAULT_Y = 95 * TILE_SIZE;
    
    private static final int SPRITE_ANIMATION_SPEED = 10;
    
    private static final int CAMERA_EDGE_THRESHOLD = 80;
    private static final int CAMERA_SPEED = 20;
    private static final float MIN_ZOOM = 1.2f;
    private static final float MAX_ZOOM = 2.0f;
    private static final float ZOOM_STEP = 0.1f;

    private static final String PLAYER_IMAGE_PATH = "src/Resource/Characters/MetroCity/CharacterModel/";
    private static final String MAP_FILE_PATH = "src/Data/Map.txt";
    private static final int MAX_TILES = 50;

    private static int screenWidth;
    private static int screenHeight;

    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxScreenCol = screenSize.width / TILE_SIZE;
        int maxScreenRow = screenSize.height / TILE_SIZE;
        screenWidth = maxScreenCol * TILE_SIZE;
        screenHeight = maxScreenRow * TILE_SIZE;
    }
    
    public static int getTileSize() {
        return TILE_SIZE;
    }
    
    public static double getNanosecondsPerFrame() {
        return NANOSECONDS_PER_FRAME;
    }
    
    public static int getPlayerDefaultSpeed() {
        return PLAYER_DEFAULT_SPEED;
    }
    
    public static int getPlayerDefaultX() {
        return PLAYER_DEFAULT_X;
    }
    
    public static int getPlayerDefaultY() {
        return PLAYER_DEFAULT_Y;
    }
    
    public static int getSpriteAnimationSpeed() {
        return SPRITE_ANIMATION_SPEED;
    }
    
    public static int getCameraEdgeThreshold() {
        return CAMERA_EDGE_THRESHOLD;
    }
    
    public static int getCameraSpeed() {
        return CAMERA_SPEED;
    }
    
    public static float getMinZoom() {
        return MIN_ZOOM;
    }
    
    public static float getMaxZoom() {
        return MAX_ZOOM;
    }
    
    public static float getZoomStep() {
        return ZOOM_STEP;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static void updateScreenSize(int width, int height) {
        int maxScreenCol = width / TILE_SIZE;
        int maxScreenRow = height / TILE_SIZE;
        screenWidth = maxScreenCol * TILE_SIZE;
        screenHeight = maxScreenRow * TILE_SIZE;
    }

    public static String getPlayerImagePath() {
        return PLAYER_IMAGE_PATH;
    }

    public static String getMapFilePath() {
        return MAP_FILE_PATH;
    }

    public static int getMaxTiles() {
        return MAX_TILES;
    }
    
    private Config() {}
}
