package Core.Moba.World;

import Core.Moba.Combat.Stats;

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

