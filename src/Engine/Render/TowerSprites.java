package Engine.Render;

import Core.Moba.World.TeamColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Gestion des sprites des tours et des bases.
 * Supporte l'animation avec plusieurs frames.
 */
public class TowerSprites {
    
    private static final String TOWER_PATH = "src/Resource/Tiles/";
    private static final int ANIMATION_SPEED = 30;
    
    private final BufferedImage[] blueTowerFrames;
    private final BufferedImage[] redTowerFrames;
    private final BufferedImage[] blueAncientFrames;
    private final BufferedImage[] redAncientFrames;
    
    private int animationCounter = 0;
    private int currentFrame = 0;
    
    public TowerSprites() {
        blueTowerFrames = loadTowerFrames("Tower_Blue");
        redTowerFrames = loadTowerFrames("Tower_Red");
        blueAncientFrames = loadAncientFrames("Ancient_Blue");
        redAncientFrames = loadAncientFrames("Ancient_Red");
    }
    
    private BufferedImage[] loadTowerFrames(String baseName) {
        BufferedImage frame1 = loadImage(baseName + ".png");
        BufferedImage frame2 = loadImage(baseName + "_2.png");
        
        if (frame2 != null) {
            return new BufferedImage[]{frame1, frame2};
        }
        return new BufferedImage[]{frame1};
    }
    
    private BufferedImage[] loadAncientFrames(String baseName) {
        BufferedImage frame1 = loadImage(baseName + ".png");
        BufferedImage frame2 = loadImage(baseName + "_2.png");
        
        if (frame2 != null) {
            return new BufferedImage[]{frame1, frame2};
        }
        return new BufferedImage[]{frame1};
    }
    
    private BufferedImage loadImage(String filename) {
        try {
            return ImageIO.read(new File(TOWER_PATH + filename));
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Met a jour l'animation.
     */
    public void update() {
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            int maxFrames = getMaxFrames();
            currentFrame = (currentFrame + 1) % maxFrames;
        }
    }
    
    private int getMaxFrames() {
        return Math.max(blueTowerFrames.length, 
                Math.max(redTowerFrames.length, 
                        Math.max(blueAncientFrames.length, redAncientFrames.length)));
    }
    
    /**
     * Retourne le sprite de la tour selon l'equipe.
     */
    public BufferedImage getTower(TeamColor team) {
        BufferedImage[] frames = team == TeamColor.BLUE ? blueTowerFrames : redTowerFrames;
        return frames[currentFrame % frames.length];
    }
    
    /**
     * Retourne le sprite de l'ancient selon l'equipe.
     */
    public BufferedImage getAncient(TeamColor team) {
        BufferedImage[] frames = team == TeamColor.BLUE ? blueAncientFrames : redAncientFrames;
        return frames[currentFrame % frames.length];
    }
    
    /**
     * Verifie si l'animation est activee.
     */
    public boolean hasAnimation() {
        return blueTowerFrames.length > 1 || redTowerFrames.length > 1 ||
               blueAncientFrames.length > 1 || redAncientFrames.length > 1;
    }
}
