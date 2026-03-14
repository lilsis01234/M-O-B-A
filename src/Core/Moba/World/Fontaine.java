package Core.Moba.World;

import Core.Moba.Combat.Stats;
/**
 * Cette classe représente la fontaine
 * @author BOUKIRAT Thafat
 * @version 1.0
 */

public final class Fontaine {
    private final Vec2 position;
    private final int healPerSecond;
    private final int manaPerSecond;

    public Fontaine(Vec2 position, int healPerSecond, int manaPerSecond) {
        this.position = position;
        this.healPerSecond = Math.max(0, healPerSecond);
        this.manaPerSecond = Math.max(0, manaPerSecond);
    }

    public Vec2 position() {
        return position;
    }

    public void regen(Stats stats, double deltaSeconds) {
        if (stats == null) return;
        if (deltaSeconds <= 0) return;
        stats.heal((int) Math.floor(healPerSecond * deltaSeconds));
        stats.restoreMana((int) Math.floor(manaPerSecond * deltaSeconds));
    }
}

