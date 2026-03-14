package Core.Moba.Units;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.Ids.GameId;
import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;

public final class TowerProjectile {
    private final GameId id;
    private final Tour tour;
    private Object cible;
    private Vec2 position;
    private Vec2 velocity;
    private int degats;
    private boolean aFini;
    private boolean enExplosion;
    private double timerExplosion;
    private Vec2 positionImpact;
    private static final double VITESSE = 400.0;
    private static final double TEMPS_EXPLOSION = 0.2;

    public TowerProjectile(Tour tour, Object cible, int degats) {
        this.id = GameId.random();
        this.tour = tour;
        this.cible = cible;
        this.degats = degats;
        this.aFini = false;
        this.enExplosion = false;
        this.timerExplosion = 0.0;
        this.positionImpact = null;

        int tileSize = Config.getTileSize();
        Vec2 posTourTiles = tour.position();
        // Spawn from top middle of tower, offset down by 12.5 pixels
        this.position = new Vec2(
            posTourTiles.x() * tileSize + (tour.width() * tileSize) / 2.0,
            posTourTiles.y() * tileSize + 12.5
        );

        if (cible != null) {
            Vec2 posCible = getPosition(cible);
            if (posCible != null) {
                Vec2 direction = posCible.sub(position).normalized();
                this.velocity = new Vec2(direction.x() * VITESSE, direction.y() * VITESSE);
            } else {
                this.velocity = new Vec2(0, 0);
            }
        } else {
            this.velocity = new Vec2(0, 0);
        }
    }

    public GameId id() {
        return id;
    }

    public Tour tour() {
        return tour;
    }

    public Object cible() {
        return cible;
    }

    public Vec2 position() {
        return position;
    }

    public int degats() {
        return degats;
    }

    public boolean aFini() {
        return aFini;
    }

    public boolean enExplosion() {
        return enExplosion;
    }

    public void mettreAJour(double deltaSeconds) {
        if (aFini) return;

        if (enExplosion) {
            timerExplosion += deltaSeconds;
            if (timerExplosion >= TEMPS_EXPLOSION) {
                aFini = true;
            }
            return;
        }

        position = new Vec2(
            position.x() + velocity.x() * deltaSeconds,
            position.y() + velocity.y() * deltaSeconds
        );

        if (cible != null && !estMort(cible)) {
            Vec2 posCible = getPosition(cible);
            if (posCible != null) {
                Vec2 direction = posCible.sub(position).normalized();
                velocity = new Vec2(direction.x() * VITESSE, direction.y() * VITESSE);
            }
        }

        if (cible != null && !estMort(cible) && estARrivee()) {
            subirDegats(cible, degats);
            enExplosion = true;
            positionImpact = new Vec2(position.x(), position.y());
            velocity = new Vec2(0, 0);
        }

        if (estHorsPortee()) {
            aFini = true;
        }
    }

    private boolean estARrivee() {
        if (cible == null) return false;
        Vec2 posCible = getPosition(cible);
        if (posCible == null) return false;
        double dist = position.distanceTo(posCible);
        return dist < 16.0; // Explode when within 16 pixels of target
    }

    private boolean estHorsPortee() {
        int tileSize = Config.getTileSize();
        Vec2 posTourTiles = tour.position();
        Vec2 posTourPixels = new Vec2(posTourTiles.x() * tileSize, posTourTiles.y() * tileSize);
        double dist = position.distanceTo(posTourPixels);
        return dist > tour.portee() * 1.5;
    }

    public Equipe equipe() {
        return tour.equipe();
    }

    private Vec2 getPosition(Object obj) {
        int tileSize = Config.getTileSize();
        if (obj instanceof Unite unite) {
            Vec2 pos = unite.position();
            return new Vec2(pos.x() * tileSize + tileSize / 2.0, pos.y() * tileSize + tileSize / 2.0);
        } else if (obj instanceof Player player) {
            // Return center of player sprite
            return new Vec2(player.getX() + tileSize / 2.0, player.getY() + tileSize / 2.0);
        }
        return null;
    }

    private boolean estMort(Object obj) {
        if (obj instanceof Unite unite) {
            return unite.estMorte();
        } else if (obj instanceof Player player) {
            return player.estMorte();
        }
        return true;
    }

    private void subirDegats(Object obj, int dmg) {
        if (obj instanceof Unite unite) {
            unite.subirDegats(dmg);
        } else if (obj instanceof Player player) {
            player.subirDegats(dmg);
        }
    }
}
