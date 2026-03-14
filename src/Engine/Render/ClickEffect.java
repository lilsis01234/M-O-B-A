package Engine.Render;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ClickEffect {

    private static final int LIFESPAN = 25;
    private static final int PARTICLE_COUNT = 6;
    
    private final int worldX;
    private final int worldY;
    private final Particle[] particles;
    private int currentLife = 0;

    // constructeur avec position dans le monde
    public ClickEffect(int worldX, int worldY) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.particles = createParticles();
    }

    // creer les particules initiales
    private Particle[] createParticles() {
        Particle[] particles = new Particle[PARTICLE_COUNT];
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float angle = (float) (i * (2 * Math.PI / PARTICLE_COUNT));
            float speed = 1.5f + rand.nextFloat() * 2.0f;
            particles[i] = new Particle(angle, speed);
        }
        return particles;
    }

    // mettre a jour les particules et la vie
    public void update() {
        currentLife++;
        for (Particle particle : particles) {
            particle.distance += particle.speed;
        }
    }

    // verifier si effet termine
    public boolean isDead() {
        return currentLife >= LIFESPAN;
    }

    // dessiner l'effet
    public void draw(Graphics2D g2) {
        float progress = (float) currentLife / LIFESPAN;
        int alpha = (int) (200 * (1.0f - progress));
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color baseColor = new Color(50, 255, 50, alpha);
        drawExpandingRing(g2, progress, baseColor);
        drawFadingRing(g2, progress, baseColor);
        drawParticles(g2, baseColor);
    }

    // dessiner anneau qui s'agrandit
    private void drawExpandingRing(Graphics2D g2, float progress, Color color) {
        int size = (int) (5 + progress * 40);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.0f));
        g2.draw(new Ellipse2D.Float(worldX - size / 2, worldY - size / 2, size, size));
    }

    // dessiner anneau qui s'efface
    private void drawFadingRing(Graphics2D g2, float progress, Color color) {
        if (progress < 0.5f) {
            float innerProgress = progress * 2.0f;
            int size = (int) (5 + innerProgress * 30);
            int alpha = (int) (150 * (1.0f - innerProgress));
            Color innerColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g2.setColor(innerColor);
            g2.draw(new Ellipse2D.Float(worldX - size / 2, worldY - size / 2, size, size));
        }
    }

    // dessiner les lignes des particules
    private void drawParticles(Graphics2D g2, Color color) {
        g2.setStroke(new BasicStroke(1.5f));
        int lineLen = 4;
        
        for (Particle particle : particles) {
            int px = (int) (worldX + Math.cos(particle.angle) * particle.distance);
            int py = (int) (worldY + Math.sin(particle.angle) * particle.distance);
            int px2 = (int) (worldX + Math.cos(particle.angle) * (particle.distance + lineLen));
            int py2 = (int) (worldY + Math.sin(particle.angle) * (particle.distance + lineLen));
            
            g2.setColor(color);
            g2.drawLine(px, py, px2, py2);
        }
    }

    // classe interne pour particule
    private static class Particle {
        final float angle;
        final float speed;
        float distance = 0;

        Particle(float angle, float speed) {
            this.angle = angle;
            this.speed = speed;
        }
    }
}