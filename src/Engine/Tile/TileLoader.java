package Engine.Tile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import Core.Moba.World.TeamColor;

public class TileLoader {

    private static final String DATA_PREFIX = "data:image/png;base64,";

    public Tile[] load(String filePath, int maxTiles) {
        Tile[] tiles = new Tile[maxTiles];

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inTilesSection = false;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                if (trimmed.equals("TILES")) {
                    inTilesSection = true;
                    continue;
                }

                if (inTilesSection) {
                    Tile tile = parseTileLine(trimmed);
                    if (tile != null && tile.getId() >= 0 && tile.getId() < tiles.length) {
                        tiles[tile.getId()] = tile;
                        
                        // If it's the specific water tile (ID 5), load extra textures
                        if (tile.getId() == 5) {
                            loadExtraWaterTextures(tile);
                        }
                        // Load ancient textures
                        if (tile.getId() == 22) {
                            loadAncientBlueTextures(tile);
                        }
                        if (tile.getId() == 23) {
                            loadAncientRedTextures(tile);
                        }
                        // Load tower textures from spritesheet
                        if (tile.getId() == 20) {
                            loadTowerBlueTextures(tile);
                        }
                        if (tile.getId() == 21) {
                            loadTowerRedTextures(tile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tiles;
    }

    private void loadExtraWaterTextures(Tile tile) {
        String[] extras = {"src/Resource/Tiles/water.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tile.addImage(img);
            } catch (IOException e) {
                System.err.println("Could not load extra water texture: " + path);
            }
        }
    }

    private void loadAncientBlueTextures(Tile tile) {
        String[] extras = {"src/Resource/Tiles/Ancient_Blue.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tile.addImage(img);
            } catch (IOException e) {
                System.err.println("Could not load ancient blue texture: " + path);
            }
        }
    }

    private void loadAncientRedTextures(Tile tile) {
        String[] extras = {"src/Resource/Tiles/Ancient_Red.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tile.addImage(img);
            } catch (IOException e) {
                System.err.println("Could not load ancient red texture: " + path);
            }
        }
    }

    private void loadTowerBlueTextures(Tile tile) {
        // Load the new Tower-sheet.png spritesheet and cache team-colored frames
        String[] extras = {"src/Resource/Tower/Tower-sheet.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                // Cache pre-processed blue team frames
                tile.setUserData(cacheTowerFrames(img, TeamColor.BLUE));
            } catch (IOException e) {
                System.err.println("Could not load tower sheet texture: " + path);
            }
        }
    }

    private void loadTowerRedTextures(Tile tile) {
        // Load the new Tower-sheet.png spritesheet and cache team-colored frames
        String[] extras = {"src/Resource/Tower/Tower-sheet.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                // Cache pre-processed red team frames
                tile.setUserData(cacheTowerFrames(img, TeamColor.RED));
            } catch (IOException e) {
                System.err.println("Could not load tower sheet texture: " + path);
            }
        }
    }

    /**
     * Cache tower animation frames from the spritesheet.
     * Spritesheet structure (isometric 64x96 or 64x128):
     * Row 0: Activation/Spawning (frames 1-6) - 6 frames
     * Row 1: Idle/Hover cycle (8 frames)
     * Row 2: Charging/Firing (7 frames) - total 21 frames
     */
    private BufferedImage[] cacheTowerFrames(BufferedImage spritesheet, TeamColor teamColor) {
        if (spritesheet == null) return new BufferedImage[0];
        
        int frameWidth = 64;  // Isometric width
        int frameHeight = 96; // Base height
        int cols = spritesheet.getWidth() / frameWidth;
        
        // Total frames: Row 0 (6), Row 1 (8), Row 2 (7) = 21 frames
        BufferedImage[] frames = new BufferedImage[21];
        
        for (int row = 0; row < 3; row++) {
            int framesInRow = (row == 0) ? 6 : (row == 1) ? 8 : 7;
            for (int col = 0; col < framesInRow; col++) {
                int frameIndex = (row == 0) ? col : (row == 1) ? 6 + col : 14 + col;
                int x = col * frameWidth;
                int y = row * frameHeight;
                
                // Extract frame
                BufferedImage frame = spritesheet.getSubimage(x, y, frameWidth, frameHeight);
                
                // Apply team color mask
                frames[frameIndex] = applyTeamColorMask(frame, teamColor);
            }
        }
        
        return frames;
    }
    
    /**
     * Apply team color mask to tower sprite with transparency.
     * Uses a subtle tint (30% team color) to preserve original shading.
     */
    private BufferedImage applyTeamColorMask(BufferedImage frame, TeamColor teamColor) {
        BufferedImage colored = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Color teamColorRGB = (teamColor == TeamColor.BLUE) 
            ? new Color(0x42, 0x99, 0xe1)  // Blue
            : new Color(0xf5, 0x65, 0x65); // Red
        
        // Blend factor: 0.3 = 30% team color, 70% original
        final double BLEND_FACTOR = 0.3;
            
        for (int y = 0; y < frame.getHeight(); y++) {
            for (int x = 0; x < frame.getWidth(); x++) {
                int argb = frame.getRGB(x, y);
                int alpha = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                
                if (alpha > 0) {
                    // Detect dark sphere area (tower orb) - apply subtle team color tint
                    int brightness = (r + g + b) / 3;
                    if (brightness < 100) { // Dark pixels are the orb
                        // Blend team color with original
                        int newR = (int)(r * (1 - BLEND_FACTOR) + teamColorRGB.getRed() * BLEND_FACTOR);
                        int newG = (int)(g * (1 - BLEND_FACTOR) + teamColorRGB.getGreen() * BLEND_FACTOR);
                        int newB = (int)(b * (1 - BLEND_FACTOR) + teamColorRGB.getBlue() * BLEND_FACTOR);
                        colored.setRGB(x, y, (alpha << 24) | (newR << 16) | (newG << 8) | newB);
                    } else {
                        // Keep original for highlights and base
                        colored.setRGB(x, y, argb);
                    }
                }
            }
        }
        return colored;
    }

    public boolean[] buildCollisionTable(Tile[] tiles) {
        boolean[] collision = new boolean[tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            Tile t = tiles[i];
            collision[i] = t != null && t.isCollision();
        }
        return collision;
    }

    private Tile parseTileLine(String line) {
        String[] parts = line.split(":");
        if (parts.length < 3) return null;

        try {
            int tileId = Integer.parseInt(parts[0]);
            String colorHex = parts[1];
            String name = parts[2];
            String base64Data = extractBase64(parts);

            BufferedImage image = null;
            if (!base64Data.isEmpty()) {
                image = decodeImage(base64Data);
            }
            
            String lowerName = name.toLowerCase();
            boolean hasCollision = lowerName.contains("wall")
                    || (lowerName.contains("water") && lowerName.contains("_"));

            Tile tile = new Tile(tileId, name, image, hasCollision);
            if (colorHex.startsWith("#")) {
                tile.setColor(java.awt.Color.decode(colorHex));
            }
            return tile;

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractBase64(String[] parts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 3; i < parts.length; i++) {
            if (i > 3) builder.append(":");
            builder.append(parts[i]);
        }
        String data = builder.toString();
        if (data.startsWith(DATA_PREFIX)) {
            data = data.substring(DATA_PREFIX.length());
        }
        return data;
    }

    private BufferedImage decodeImage(String base64Data) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
            return ImageIO.read(input);
        }
    }
}