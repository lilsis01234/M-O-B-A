package Engine.Render.World;

import Core.Config;
import Core.Moba.Units.Tour;
import Core.Moba.Units.Ancient;
import Core.Moba.World.TeamColor;
import Engine.Render.Camera;
import Engine.Tile.Tile;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TowerRenderer {
    
    private Tile[] tiles;
    
    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }
    
    private BufferedImage getTowerFrame(Tile tile, int frameIndex) {
        if (tile == null) return null;
        Object userData = tile.getUserData();
        if (userData instanceof BufferedImage[]) {
            BufferedImage[] frames = (BufferedImage[]) userData;
            if (frameIndex >= 0 && frameIndex < frames.length) {
                return frames[frameIndex];
            }
        }
        return null;
    }
    
    public void draw(Graphics2D g2, Tour tour, Camera camera) {
        int tileSize = Config.getTileSize();
        // Conversion de la position du coin supérieur gauche de la tuile au centre-bas de la base
        double centerTileX = tour.position().x() + tour.width() / 2.0;
        double baseTileY = tour.position().y() + tour.height();
        int x = (int) (centerTileX * tileSize);
        int y = (int) (baseTileY * tileSize);
        
        // Calcul des dimensions de la tour pour la barre de vie (hauteur = 2 tuiles)
        int structureWidth = tour.width() * tileSize;
        int scaledH = 2 * tileSize;
        
        // Récupération de la tuile de la tour 
        int tileId = tour.equipe().couleur() == TeamColor.RED ? 21 : 20;
        Tile tile = (tileId >= 0 && tileId < tiles.length) ? tiles[tileId] : null;
        
        // Récupération de l'image animée correspondant à l'état actuel de la tour
        BufferedImage towerImg = getTowerFrame(tile, tour.getCurrentFrame());
        
        if (towerImg != null) {
            // Rendu isométrique de la tour 
            // L'ancre est le centre-bas de la base de la tour
            int imgWidth = towerImg.getWidth();
            int imgHeight = towerImg.getHeight();
            
            // Redimensionnement pour que la tour fasse 2 tuiles de hauteur
            scaledH = 2 * tileSize;
            int scaledW = (int)((double)imgWidth / imgHeight * scaledH);
            structureWidth = scaledW; 
            
            // Position : x et y correspondent au centre de la base de la tour
            int drawX = x - scaledW / 2;
            int drawY = y - scaledH;
            
            g2.drawImage(towerImg, drawX, drawY, scaledW, scaledH, null);
        } else {
            // Solution de secours : rectangle simple avec couleurs de l'équipe
            g2.setColor(tour.equipe().couleur() == TeamColor.BLUE 
                ? new Color(0x2a, 0x5a, 0x9e)  
                : new Color(0xff, 0x45, 0x45)); 
            int structureHeight = tour.height() * tileSize;
            g2.fillRect(x - structureWidth/2, y - structureHeight, structureWidth, structureHeight);
        }
        
        // Barre de vie - positionnée au-dessus de la tour, plus petite
        int healthBarWidth = structureWidth - 20; // 10px de marge de chaque côté
        int healthBarHeight = 4; // plus fine
        int healthBarY = y - scaledH - 8; // au-dessus du sommet de la tour
        g2.setColor(Color.RED);
        g2.fillRect(x - healthBarWidth/2, healthBarY, healthBarWidth, healthBarHeight);
        g2.setColor(Color.GREEN);
        double healthPct = (double)tour.stats().hp() / tour.stats().maxHp();
        g2.fillRect(x - healthBarWidth/2, healthBarY, (int)(healthBarWidth * healthPct), healthBarHeight);
        
        // Affichage d'une bordure autour de la barre de vie
        g2.setColor(Color.BLACK);
        g2.drawRect(x - healthBarWidth/2, healthBarY, healthBarWidth, healthBarHeight);
        // Logique future pour effets spéciaux de la tour (ex. étincelles, feu)
        // A FAIREEE : ajouter des animations visuelles lors des attaques
        // Débogage de position de la tour si besoin
        // System.out.println("Tour position x=" + x + ", y=" + y);
    }
    
    public void drawAncient(Graphics2D g2, Ancient ancient, Camera camera) {
         int tileSize = Config.getTileSize();
         // Conversion de la position du coin supérieur gauche de la tuile au centre-bas de la base
         double centerTileX = ancient.position().x() + ancient.width() / 2.0;
         double baseTileY = ancient.position().y() + ancient.height();
         int x = (int) (centerTileX * tileSize);
         int y = (int) (baseTileY * tileSize);
         int w = ancient.width() * tileSize;
         int h = ancient.height() * tileSize;
        
        // Dessin du remplissage aux couleurs de l'équipe 
        Color teamColor = ancient.equipe().couleur() == TeamColor.BLUE 
            ? new Color(0x42, 0x99, 0xe1) 
            : new Color(0xf5, 0x65, 0x65);
        g2.setColor(teamColor);
        g2.fillRect(x - w/2, y - h, w, h);
    }
}