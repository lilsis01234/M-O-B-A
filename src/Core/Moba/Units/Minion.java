package Core.Moba.Units;

import Core.Moba.Combat.Stats;
import Core.Moba.World.Vec2;
import Core.Moba.World.Voie;

public final class Minion extends Unite {
    private final Voie voie;
    private final int goldOnKill;
    private final int xpOnKill;

    public Minion(Vec2 position, Voie voie, Stats stats, int goldOnKill, int xpOnKill) {
        super(position, stats);
        this.voie = voie;
        this.goldOnKill = Math.max(0, goldOnKill);
        this.xpOnKill = Math.max(0, xpOnKill);
    }

    public Voie voie() {
        return voie;
    }

    public int goldOnKill() {
        return goldOnKill;
    }

    public int xpOnKill() {
        return xpOnKill;
    }
}

