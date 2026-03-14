package Core.Moba.Units;

import Core.Moba.World.Equipe;
import Core.Moba.World.Vec2;
import Core.Moba.Combat.Stats;

/**
 * Bâtiment principal (Base) d'une équipe.
 */
public class Ancient extends Unite {
    private int width;  
    private int height; 

    public Ancient(Equipe equipe, Vec2 position, int hp, int armure, int w, int h) {
        // Une base n'a pas de mana et ne se déplace pas 
        super(position, new Stats(hp, 0, 0, armure, 0.0));
        setEquipe(equipe);
        this.width = w;
        this.height = h;
    }

    public int width() { return width; }
    public int height() { return height; }

    /**
     * Vérifie la collision entre cette base et une autre entité.
     * @param x Position X de l'autre entité 
     * @param y Position Y de l'autre entité 
     * @param width Largeur de l'autre entité 
     * @param height Hauteur de l'autre entité 
     * @return true s'il y a chevauchement.
     */
    public boolean collidesWith(double x, double y, double width, double height) {
        int tileSize = Core.Config.getTileSize();

        double px = position().x() * tileSize;
        double py = position().y() * tileSize;
        double pw = this.width * tileSize;
        double ph = this.height * tileSize;

        return x < px + pw && 
               x + width > px && 
               y < py + ph && 
               y + height > py;
    }
}