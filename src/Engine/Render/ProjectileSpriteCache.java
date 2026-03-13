package Engine.Render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ProjectileSpriteCache {

    private final BufferedImage[] fireballFrames;
    private final BufferedImage explosionSprite;

    public ProjectileSpriteCache() {
        this.fireballFrames = loadFireballFrames();
        this.explosionSprite = loadExplosionSprite();
    }

    private BufferedImage[] loadFireballFrames() {
        BufferedImage[] frames = new BufferedImage[3];
        String[] frameNames = {"fireball_1.png", "fireball_2.png", "fireball_3.png"};
        
        for (int i = 0; i < 3; i++) {
            try {
                String path = "src/Resource/Projectile/" + frameNames[i];
                File file = new File(path);
                if (file.exists()) {
                    frames[i] = ImageIO.read(file);
                } else {
                    frames[i] = createFallbackSprite(i);
                }
            } catch (IOException e) {
                frames[i] = createFallbackSprite(i);
            }
        }
        return frames;
    }

    private BufferedImage loadExplosionSprite() {
        try {
            String explosionPath = "src/Resource/Projectile/explosion.png";
            File explosionFile = new File(explosionPath);
            if (explosionFile.exists()) {
                return ImageIO.read(explosionFile);
            }
            return createExplosionFallback();
        } catch (IOException e) {
            return createExplosionFallback();
        }
    }

    private BufferedImage createFallbackSprite(int frame) {
        BufferedImage img = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setColor(new java.awt.Color(255, 100, 0));
        g.fillRect(3, 3, 6, 6);
        g.setColor(new java.awt.Color(255, 200, 0));
        g.fillRect(5, 5, 2, 2);
        g.dispose();
        return img;
    }

    private BufferedImage createExplosionFallback() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setColor(new java.awt.Color(255, 100, 0, 200));
        g.fillOval(8, 8, 48, 48);
        g.setColor(new java.awt.Color(255, 200, 0, 150));
        g.fillOval(16, 16, 32, 32);
        g.setColor(new java.awt.Color(255, 255, 100, 100));
        g.fillOval(24, 24, 16, 16);
        g.dispose();
        return img;
    }

    public BufferedImage getFireballFrame(int frameIndex) {
        if (frameIndex >= 0 && frameIndex < fireballFrames.length) {
            return fireballFrames[frameIndex];
        }
        return fireballFrames[0];
    }

    public BufferedImage getExplosionSprite() {
        return explosionSprite;
    }
}
