package Engine.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

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
                        // Load tower textures
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
        String[] extras = {"src/Resource/Tiles/Tower_Blue.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tile.addImage(img);
            } catch (IOException e) {
                System.err.println("Could not load tower blue texture: " + path);
            }
        }
    }

    private void loadTowerRedTextures(Tile tile) {
        String[] extras = {"src/Resource/Tiles/Tower_Red.png"};
        for (String path : extras) {
            try {
                BufferedImage img = ImageIO.read(new File(path));
                tile.addImage(img);
            } catch (IOException e) {
                System.err.println("Could not load tower red texture: " + path);
            }
        }
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

