package Core.Moba.Units;

import Core.Moba.Combat.Stats;
import Core.Moba.World.Vec2;
import Core.Moba.World.Voie;

/**
 * Classe pour les creeps
 * */
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
    
    /** @return La voie sur laquelle circule le creep. */
    public Voie voie() {
        return voie;
    }

    /** @return gold farming */
    public int goldOnKill() {
        return goldOnKill;
    }

    /** @return xp farming */
    public int xpOnKill() {
        return xpOnKill;
    }
}
