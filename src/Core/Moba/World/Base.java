package Core.Moba.World;

import Core.Moba.Combat.Stats;

/**
 * Cette classe représente la base
 * @author RAHARIMANANA Tianantenaina
 * @version 1.0
 */
public final class Base {
    private final Stats stats;

    public Base(int maxHp) {
        this.stats = new Stats(maxHp, 0, 0, 0, 0);
    }

    public Stats stats() {
        return stats;
    }

    public boolean estDetruite() {
        return stats.isDead();
    }
}

