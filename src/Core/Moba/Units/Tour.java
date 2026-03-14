package Core.Moba.Units;

import Core.Config;
import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;
import Core.Moba.Combat.Stats;
import Core.Moba.World.Voie;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe représente la Tour
 */

public class Tour extends Unite {
    private int tier;
    private Voie lane;
    private int width = 1;
    private int height = 1;
    private int portee;
    private TowerAI ai;
    private final List<TowerProjectile> projectiles;
    
    // Animation state
    private AnimationState animationState = AnimationState.IDLE;
    private int animationFrame = 0;
    private long animationTimer = 0;
    private static final int FRAME_DELAY_MS = 100;
    private boolean attackReady = false;
    
    private enum AnimationState {
        IDLE,
        ATTACK
    }

    public Tour(Equipe equipe, Vec2 position, int hp, int armure, int attaque, int portee) {
        super(position, new Stats(hp, 0, attaque, armure, 0.0));
        setEquipe(equipe);
        this.portee = portee;
        this.tier = 3;
        this.lane = Voie.MID;
        this.projectiles = new ArrayList<>();
        this.ai = new TowerAI(this);
        // Initialize animation state
        this.animationState = AnimationState.IDLE;
        this.animationFrame = 6; // Start at first idle frame
        this.animationTimer = System.currentTimeMillis();
        this.attackReady = false;
    }

    public Tour(Equipe equipe, Vec2 position, int hp, int armure, int attaque, int portee, int tier, Voie lane, int w, int h) {
        this(equipe, position, hp, armure, attaque, portee);
        this.tier = tier;
        this.lane = lane;
        this.width = w;
        this.height = h;
    }
    /**
     * Logique de Collision (AABB)
    */
    public boolean collidesWith(double x, double y, double width, double height) {
        int tileSize = Config.getTileSize();
        double towerPixelX = position().x() * tileSize;
        double towerPixelY = position().y() * tileSize;
        double towerWidth = this.width * tileSize;
        double towerHeight = this.height * tileSize;

        return x < towerPixelX + towerWidth
                && x + width > towerPixelX
                && y < towerPixelY + towerHeight
                && y + height > towerPixelY;
    }

    public boolean collidesWithPixelBounds(double left, double top, double right, double bottom) {
        int tileSize = Config.getTileSize();
        double towerPixelX = position().x() * tileSize;
        double towerPixelY = position().y() * tileSize;
        double towerWidth = this.width * tileSize;
        double towerHeight = this.height * tileSize;

        return right > towerPixelX && left < towerPixelX + towerWidth
                && bottom > towerPixelY && top < towerPixelY + towerHeight;
    }

    public int portee() {
        return portee;
    }

    public void setPortee(int portee) {
        this.portee = portee;
    }

    public int tier() {
        return tier;
    }

    public Voie lane() {
        return lane;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setLane(Voie lane) {
        this.lane = lane;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public TowerAI ai() {
        return ai;
    }

    public List<TowerProjectile> projectiles() {
        return projectiles;
    }

    public void ajouterProjectile(TowerProjectile projectile) {
        projectiles.add(projectile);
    }

    public void mettreAJourProjectiles(double deltaSeconds) {
        projectiles.removeIf(p -> {
            p.mettreAJour(deltaSeconds);
            return p.aFini();
        });
    }
    
    // Animation methods
    public void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - animationTimer >= FRAME_DELAY_MS) {
            animationTimer = currentTime;
            
            switch (animationState) {
                case IDLE:
                    // Idle frames: 6-13 (8 frames)
                    animationFrame = (animationFrame + 1) % 8 + 6;
                    break;
                case ATTACK:
                    // Attack frames: 14-20 (7 frames)
                    if (animationFrame < 20) {
                        animationFrame++;
                        // Fire projectile at frame 17 (middle of animation)
                        if (animationFrame == 17 && !attackReady) {
                            attackReady = true; // Signal to fire
                        }
                    } else {
                        // Animation complete, return to idle
                        setIdle();
                    }
                    break;
            }
        }
    }
    
    public int getCurrentFrame() {
        return animationFrame;
    }
    
    public void startAttackAnimation() {
        if (animationState == AnimationState.IDLE) {
            animationState = AnimationState.ATTACK;
            animationFrame = 14; // Start at first attack frame
            attackReady = false;
        }
    }
    
    public boolean isAttackReady() {
        return attackReady;
    }
    
    public void resetAttackReady() {
        attackReady = false;
    }
    
    public void setIdle() {
        animationState = AnimationState.IDLE;
        animationFrame = 6; // Start idle at frame 6
        attackReady = false;
    }
}

