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
    
    // Direction column offsets in spritesheet (0-indexed)
    private static final int COL_SOUTH = 0;   // Facing South (front)
    private static final int COL_EAST = 6;    // Facing East (right)
    private static final int COL_NORTH = 12;  // Facing North (back)
    private static final int COL_WEST = 18;   // Facing West (left)
    
    private final int suitRow; // 0-3 (4 rows)

    public SuitSprites(int suitRow) {
        this.suitRow = suitRow;
        sprites = new BufferedImage[4][FRAMES_PER_DIRECTION];
        
        try {
            BufferedImage sheet = ImageIO.read(new File("src/Resource/Characters/Outfits/Suit.png"));
            
            // Extract sprites for each direction from the specified row
            extractDirectionFrames(sheet, suitRow, COL_SOUTH, 0);  // DOWN
            extractDirectionFrames(sheet, suitRow, COL_EAST, 1);   // RIGHT
            extractDirectionFrames(sheet, suitRow, COL_NORTH, 2); // UP
            extractDirectionFrames(sheet, suitRow, COL_WEST, 3);  // LEFT
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
    
    /** 
     * Check if this suit includes headwear that covers the hair.
     * Row 2 (Guard/Royal Attendant) and Row 4 (Worker) have hats/headwear.
     * Row 1 (Police) has a peaked cap. Row 3 (Formal Suit) has no hat.
     */
    public boolean hasHeadwear() {
        return suitRow != 2; // Only row 2 (index 2, Formal Suit) has no headwear
    }
}
