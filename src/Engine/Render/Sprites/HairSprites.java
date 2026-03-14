package Engine.Render.Sprites;

import Core.Entity.Direction;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HairSprites {
    private final BufferedImage[][] sprites; // [direction][frame]
    private static final int SPRITE_SIZE = 32;
    private static final int FRAMES_PER_DIRECTION = 6;
    
    // Direction column offsets in spritesheet (0-indexed)
    private static final int COL_SOUTH = 0;   // Facing South (front)
    private static final int COL_EAST = 6;    // Facing East (right)
    private static final int COL_NORTH = 12;  // Facing North (back)
    private static final int COL_WEST = 18;   // Facing West (left)
    
    private final int hairRow;

    public HairSprites(int hairRow) {
        this.hairRow = hairRow;
        sprites = new BufferedImage[4][FRAMES_PER_DIRECTION];
        
        try {
            BufferedImage sheet = ImageIO.read(new File("src/Resource/Characters/Hair/Hairs.png"));
            
            // Extract sprites for each direction
            // Directions: DOWN, RIGHT, UP, LEFT (matching game's Direction enum)
            extractDirectionFrames(sheet, hairRow, COL_SOUTH, 0);  // DOWN
            extractDirectionFrames(sheet, hairRow, COL_EAST, 1);   // RIGHT
            extractDirectionFrames(sheet, hairRow, COL_NORTH, 2); // UP
            extractDirectionFrames(sheet, hairRow, COL_WEST, 3);  // LEFT
        } catch (IOException e) {
            throw new RuntimeException("Failed to load hair spritesheet", e);
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
