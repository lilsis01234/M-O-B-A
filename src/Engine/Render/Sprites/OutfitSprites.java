package Engine.Render.Sprites;

import Core.Entity.Direction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class OutfitSprites {
    private final BufferedImage[][] sprites; // [direction][frame]
    private static final int SPRITE_SIZE = 32;
    private static final int FRAMES_PER_DIRECTION = 6;
    
   // Décalages des colonnes par direction dans la spritesheet (indexés à partir de 0)
    private static final int COL_SOUTH = 0;   // Face au Sud (avant)
    private static final int COL_EAST = 6;    // Face à l'Est (droite)
    private static final int COL_NORTH = 12;  // Face au Nord (arrière)
    private static final int COL_WEST = 18;   // Face à l'Ouest (gauche)

    public OutfitSprites(String outfitFileName) {
        sprites = new BufferedImage[4][FRAMES_PER_DIRECTION];
        
        try {
            BufferedImage sheet = ImageIO.read(new File("src/Resource/Characters/Outfits/" + outfitFileName));
            
            // Extraire les sprites pour chaque direction à partir de la ligne 0 
            extractDirectionFrames(sheet, 0, COL_SOUTH, 0);  // bas
            extractDirectionFrames(sheet, 0, COL_EAST, 1);   // droite
            extractDirectionFrames(sheet, 0, COL_NORTH, 2); // haut
            extractDirectionFrames(sheet, 0, COL_WEST, 3);  // gauche
        } catch (IOException e) {
            throw new RuntimeException("Failed to load outfit spritesheet: " + outfitFileName, e);
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
}
