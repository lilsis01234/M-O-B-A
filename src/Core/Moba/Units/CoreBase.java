package Core.Moba.Units;

import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;
import Core.Moba.Combat.Stats;
import Core.Moba.World.Voie;

public class CoreBase extends Unite {
    private int width;
    private int height;

    public CoreBase(Equipe equipe, Vec2 position, int hp, int armure, int w, int h) {
        super(position, new Stats(hp, 0, 0, armure, 0.0));
        setEquipe(equipe);
        this.width = w;
        this.height = h;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
