package Core.Moba.Units;

import Core.Moba.Combat.Stats;
import Core.Moba.Ids.GameId;
import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;

import java.util.Objects;

public abstract class Unite {
    private final GameId id;
    private Vec2 position;
    private final Stats stats;
    private Equipe equipe;

    protected Unite(Vec2 position, Stats stats) {
        this.id = GameId.random();
        this.position = Objects.requireNonNull(position, "position");
        this.stats = Objects.requireNonNull(stats, "stats");
    }

    public GameId id() {
        return id;
    }

    public Vec2 position() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = Objects.requireNonNull(position, "position");
    }

    public Stats stats() {
        return stats;
    }

    public Equipe equipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public boolean estMorte() {
        return stats.isDead();
    }

    public void subirDegats(int rawDamage) {
        // Simple damage model; can be expanded later to use defense, damage types, etc.
        stats.takeDamage(Math.max(0, rawDamage));
    }
}

