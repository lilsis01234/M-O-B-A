package Engine.Render.World;

import Core.Config;
import Core.Moba.Units.TowerProjectile;
import Core.Moba.Units.Tour;
import Engine.Render.Camera;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ProjectileRenderer {
    private BufferedImage[] fireballFrames;
    private BufferedImage explosionSprite;
    private int currentFrame = 0;
    private double frameTimer = 0;
    private static final double FRAME_DURATION = 0.1;
    
    public ProjectileRenderer() {
        loadSprites();
    }
    
    private void loadSprites() {
        fireballFrames = new BufferedImage[3];
        String[] frameNames = {"fireball_1.png", "fireball_2.png", "fireball_3.png"};
        
        for (int i = 0; i < 3; i++) {
            try {
                String path = "src/Resource/Projectile/" + frameNames[i];
                File file = new File(path);
                if (file.exists()) {
                    fireballFrames[i] = ImageIO.read(file);
                } else {
                    fireballFrames[i] = createFallbackSprite(i);
                }
            } catch (IOException e) {
                fireballFrames[i] = createFallbackSprite(i);
            }
        }
        
        // Load explosion sprite
        try {
            String explosionPath = "src/Resource/Projectile/explosion.png";
            File explosionFile = new File(explosionPath);
            if (explosionFile.exists()) {
                explosionSprite = ImageIO.read(explosionFile);
            } else {
                explosionSprite = createExplosionFallback();
            }
        } catch (IOException e) {
            explosionSprite = createExplosionFallback();
        }
    }
    
    private BufferedImage createFallbackSprite(int frame) {
        BufferedImage img = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new java.awt.Color(255, 100, 0));
        g.fillRect(3, 3, 6, 6);
        g.setColor(new java.awt.Color(255, 200, 0));
        g.fillRect(5, 5, 2, 2);
        g.dispose();
        return img;
    }
    
    private BufferedImage createExplosionFallback() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        // Draw orange/red explosion circle
        g.setColor(new java.awt.Color(255, 100, 0, 200));
        g.fillOval(8, 8, 48, 48);
        g.setColor(new java.awt.Color(255, 200, 0, 150));
        g.fillOval(16, 16, 32, 32);
        g.setColor(new java.awt.Color(255, 255, 100, 100));
        g.fillOval(24, 24, 16, 16);
        g.dispose();
        return img;
    }
    
    public void draw(Graphics2D g2, TowerProjectile projectile, Camera camera) {
        int x = (int) projectile.position().x();
        int y = (int) projectile.position().y();
        
        if (projectile.enExplosion()) {
            drawExplosion(g2, x, y);
        } else {
            drawFireball(g2, x, y);
        }
    }
    
    private void drawFireball(Graphics2D g2, int x, int y) {
        frameTimer += 1.0 / 60.0;
        if (frameTimer >= FRAME_DURATION) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % 3;
        }
        
        BufferedImage frame = fireballFrames[currentFrame];
        if (frame != null) {
            int size = 48;
            g2.drawImage(frame, x - size/2, y - size/2, size, size, null);
        }
    }
    
    private void drawExplosion(Graphics2D g2, int x, int y) {
        if (explosionSprite != null) {
            int size = 64;
            g2.drawImage(explosionSprite, x - size/2, y - size/2, size, size, null);
        }
    }
}