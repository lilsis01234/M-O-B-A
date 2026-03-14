package Engine.Render.Sprites;

import Core.Entity.Direction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SuitSprites {
    private final BufferedImage[][] sprites; // [direction][frame]
    private static final int SPRITE_SIZE = 32;
    private static final int FRAMES_PER_DIRECTION = 6;
    
// Décalages des colonnes par direction dans la spritesheet (indexés à partir de 0)
private static final int COL_SOUTH = 0;   // Orientation Sud (avant)
private static final int COL_EAST = 6;    // Orientation Est (droite)
private static final int COL_NORTH = 12;  // Orientation Nord (arrière)
private static final int COL_WEST = 18;   // Orientation Ouest (gauche)
    
    private final int suitRow; // 0-3 (4 rows)

    public SuitSprites(int suitRow) {
        this.suitRow = suitRow;
        sprites = new BufferedImage[4][FRAMES_PER_DIRECTION];
        
        try {
            BufferedImage sheet = ImageIO.read(new File("src/Resource/Characters/Outfits/Suit.png"));
            
            // Extraire les sprites pour chaque direction à partir de la ligne spécifiée
            extractDirectionFrames(sheet, suitRow, COL_SOUTH, 0);  // bas
            extractDirectionFrames(sheet, suitRow, COL_EAST, 1);   // droite
            extractDirectionFrames(sheet, suitRow, COL_NORTH, 2); // haut
            extractDirectionFrames(sheet, suitRow, COL_WEST, 3);  // gauche
        } catch (IOException e) {
            throw new RuntimeException("Failed to load suit spritesheet", e);
        }
    }
    
    private void extractDirectionFrames(BufferedImage sheet, int row, int startCol, int dirIndex) {
        for (int frame = 0; frame < FRAMES_PER_DIRECTION; frame++) {
            int x = (startCol + frame) * SPRITE_SIZE;
            int y = row * SPRITE_SIZE;
            sprites[dirIndex][frame] = sheet.getSubimage(x, y, SPRITE_SIZE, SPRITE_SIZE);
        }
    }

    public BufferedImage get(Direction direction, int spriteNum) {
        int frameIndex = (spriteNum - 1) % FRAMES_PER_DIRECTION;
        int dirIndex = switch (direction) {
            case DOWN -> 0;
            case RIGHT -> 1;
            case UP -> 2;
            case LEFT -> 3;
        };
        return sprites[dirIndex][frameIndex];
    }
    
/*** Vérifie si ce costume inclut un couvre-chef qui recouvre les cheveux.
 * Ligne 2 (Garde / Assistant Royal) et ligne 4 (Ouvrier) ont des chapeaux/couvre-chefs.
 * Ligne 1 (Police) a une casquette à visiere. Ligne 3 (Costume formel) n’a pas de chapeau*/
public boolean hasHeadwear() {
    return suitRow != 2; // Seule la ligne 2 (index 2, Costume formel) n’a pas de couvre-chef
}
}
