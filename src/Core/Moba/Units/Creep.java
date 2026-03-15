package Core.Moba.Units;

import Core.Moba.Combat.Stats;
import Core.Moba.World.Vec2;
/**
 * Monstres neutres juste pour le farming
 */
public final class Creep extends Unite {
    private final int goldOnKill;
    private final int xpOnKill;

    public Creep(Vec2 position, Stats stats, int goldOnKill, int xpOnKill) {
        super(position, stats);
        this.goldOnKill = Math.max(0, goldOnKill);
        this.xpOnKill = Math.max(0, xpOnKill);
    }

    public int goldOnKill() {
        return goldOnKill;
    }

    public int xpOnKill() {
        return xpOnKill;
    }
}

