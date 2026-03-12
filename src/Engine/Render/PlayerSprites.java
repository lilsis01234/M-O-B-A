package Engine.Render;

import Core.Entity.Direction;
import Core.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlayerSprites {
    private final BufferedImage up1;
    private final BufferedImage up2;
    private final BufferedImage down1;
    private final BufferedImage down2;
    private final BufferedImage left1;
    private final BufferedImage left2;
    private final BufferedImage right1;
    private final BufferedImage right2;

    public PlayerSprites() {
        String path = Config.getPlayerImagePath();
        try {
            up1 = ImageIO.read(new File(path + "boy_up_1.png"));
            up2 = ImageIO.read(new File(path + "boy_up_2.png"));
            down1 = ImageIO.read(new File(path + "boy_down_1.png"));
            down2 = ImageIO.read(new File(path + "boy_down_2.png"));
            left1 = ImageIO.read(new File(path + "boy_left_1.png"));
            left2 = ImageIO.read(new File(path + "boy_left_2.png"));
            right1 = ImageIO.read(new File(path + "boy_right_1.png"));
            right2 = ImageIO.read(new File(path + "boy_right_2.png"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load player sprites from " + path, e);
        }
    }

    public BufferedImage get(Direction direction, int spriteNum) {
        int frame = spriteNum == 1 ? 1 : 2;
        return switch (direction) {
            case UP -> frame == 1 ? up1 : up2;
            case DOWN -> frame == 1 ? down1 : down2;
            case LEFT -> frame == 1 ? left1 : left2;
            case RIGHT -> frame == 1 ? right1 : right2;
        };
    }
}

