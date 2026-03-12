package Core.Moba.Units;

import Core.Config;
import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;
import Core.Moba.Combat.Stats;
import Core.Moba.World.Voie;

import java.util.ArrayList;
import java.util.List;

public class Tour extends Unite {
    private int tier;
    private Voie lane;
    private int width = 1;
    private int height = 1;
    private int portee;
    private TowerAI ai;
    private final List<TowerProjectile> projectiles;

    public Tour(Equipe equipe, Vec2 position, int hp, int armure, int attaque, int portee) {
        super(position, new Stats(hp, 0, attaque, armure, 0.0));
        setEquipe(equipe);
        this.portee = portee;
        this.tier = 3;
        this.lane = Voie.MID;
        this.projectiles = new ArrayList<>();
        this.ai = new TowerAI(this);
    }

    public Tour(Equipe equipe, Vec2 position, int hp, int armure, int attaque, int portee, int tier, Voie lane, int w, int h) {
        this(equipe, position, hp, armure, attaque, portee);
        this.tier = tier;
        this.lane = lane;
        this.width = w;
        this.height = h;
    }

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
}

