package Core.Moba.Units;

import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;
import Core.Moba.Combat.Stats;
import Core.Moba.World.Voie;

public class Ancient extends Unite {
    private int width;
    private int height;

    public Ancient(Equipe equipe, Vec2 position, int hp, int armure, int w, int h) {
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
  
    public boolean collidesWith(double x, double y, double width, double height) {
        int tileSize = Core.Config.getTileSize();
        double px = position().x() * tileSize;
        double py = position().y() * tileSize;
        double pw = this.width * tileSize;
        double ph = this.height * tileSize;

        return x < px + pw && x + width > px && y < py + ph && y + height > py;
    }
}
