package Engine.Render.HUD;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.World.Arena;
import Core.Moba.World.TeamColor;
import Core.Tile.TileMap;
import Engine.Render.Camera;
import Engine.Tile.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MinimapRenderer {
    private final Arena arena;
    private final TileMap tileMap;
    private final Tile[] tiles;
    private final Player player;
    private int x, y;
    private final int size;
    private Camera camera;
    private java.util.function.Consumer<Point> onClickCallback;
    private TeamColor playerTeam;
    private BufferedImage cachedMinimap;

    public MinimapRenderer(Player player, Arena arena, TileMap tileMap, Tile[] tiles, int x, int y, int size) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.tiles = tiles;
        this.x = x;
        this.y = y;
        this.size = size;
        this.playerTeam = player.equipe() != null ? player.equipe().couleur() : TeamColor.BLUE;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setOnClickCallback(java.util.function.Consumer<Point> callback) {
        this.onClickCallback = callback;
    }

    public boolean handleClick(int clickX, int clickY) {
        if (clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size) {
            float mapWidth = tileMap.getColumns() * Config.getTileSize();
            float mapHeight = tileMap.getRows() * Config.getTileSize();
            float scaleX = size / mapWidth;
            float scaleY = size / mapHeight;
            
            float worldX = (clickX - x) / scaleX;
            float worldY = (clickY - y) / scaleY;
            
            if (onClickCallback != null) {
                onClickCallback.accept(new Point((int) worldX, (int) worldY));
            }
            return true;
        }
        return false;
    }

    public boolean handleRightClick(int clickX, int clickY) {
        if (clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size) {
            if (camera != null) {
                float mapWidth = tileMap.getColumns() * Config.getTileSize();
                float mapHeight = tileMap.getRows() * Config.getTileSize();
                float scaleX = size / mapWidth;
                float scaleY = size / mapHeight;
                
                float worldX = (clickX - x) / scaleX;
                float worldY = (clickY - y) / scaleY;
                
                worldX -= camera.getViewportWidth() / 2f;
                worldY -= camera.getViewportHeight() / 2f;
                
                camera.setPosition(worldX, worldY);
            }
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
// rendu complet du minimap
    public void render(Graphics2D g2) {
        g2.setColor(new Color(10, 10, 15, 240));
        g2.fillRoundRect(x - 3, y - 3, size + 6, size + 6, 12, 12);
        
        g2.setColor(new Color(30, 30, 40));
        g2.fillRoundRect(x, y, size, size, 8, 8);
         // création d'une image minimap statique
        if (cachedMinimap == null) {
            cachedMinimap = createMinimapBackground();
        }
        
        g2.drawImage(cachedMinimap, x, y, null);
 // échelle entre monde et minimap
        float mapWidth = tileMap.getColumns() * Config.getTileSize();
        float mapHeight = tileMap.getRows() * Config.getTileSize();
        float scaleX = size / mapWidth;
        float scaleY = size / mapHeight;

        drawLanes(g2, scaleX, scaleY);

        for (var coreBase : arena.coreBases()) {
            int px = x + (int) (coreBase.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (coreBase.position().y() * Config.getTileSize() * scaleY);
            boolean isAlly = coreBase.equipe().couleur() == playerTeam;
            g2.setColor(isAlly ? new Color(60, 100, 255) : new Color(200, 60, 60));
            g2.fillRect(px - 6, py - 6, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(px - 6, py - 6, 12, 12);
        }
//TOURS
        for (var tour : arena.tours()) {
            int px = x + (int) (tour.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (tour.position().y() * Config.getTileSize() * scaleY);
            boolean isAlly = tour.equipe().couleur() == playerTeam;
            g2.setColor(isAlly ? new Color(80, 180, 255) : new Color(255, 80, 80));
            if (tour.tier() == 1) {
                g2.fillRect(px - 3, py - 3, 6, 6);
            } else if (tour.tier() == 2) {
                g2.fillRect(px - 4, py - 4, 8, 8);
            } else {
                g2.fillRect(px - 5, py - 5, 10, 10);
            }
        }

        drawFountains(g2, scaleX, scaleY);
//joueurs
        int playerX = x + (int) (player.getX() * scaleX);
        int playerY = y + (int) (player.getY() * scaleY);
        g2.setColor(new Color(100, 255, 100));
        g2.fillOval(playerX - 5, playerY - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(playerX - 5, playerY - 5, 10, 10);
//subrillance si bleu
        if (playerTeam == TeamColor.BLUE) {
            for (var ally : arena.tours()) {
                if (ally.equipe().couleur() == playerTeam) {
                    int px = x + (int) (ally.position().x() * Config.getTileSize() * scaleX);
                    int py = y + (int) (ally.position().y() * Config.getTileSize() * scaleY);
                    g2.setColor(new Color(80, 180, 255, 150));
                    g2.fillRect(px - 2, py - 2, 4, 4);
                }
            }
        }
  // rectangle de la camera visible
        if (camera != null) {
            int camX = x + (int) (camera.getX() * scaleX);
            int camY = y + (int) (camera.getY() * scaleY);
            int camW = (int) (camera.getViewportWidth() * scaleX);
            int camH = (int) (camera.getViewportHeight() * scaleY);
            
            camX = Math.max(x, Math.min(camX, x + size - 1));
            camY = Math.max(y, Math.min(camY, y + size - 1));
            camW = Math.min(camW, x + size - camX);
            camH = Math.min(camH, y + size - camY);
            
            if (camW > 0 && camH > 0) {
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(camX, camY, camW, camH);
            }
        }

        g2.setColor(new Color(150, 150, 170));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("RMB: Move", x + 5, y + size - 8);
    }
  // dessine les voies principales
    private void drawLanes(Graphics2D g2, float scaleX, float scaleY) {
        g2.setColor(new Color(90, 80, 60, 60));
        
        int midX = tileMap.getColumns() / 2;
        int midY = tileMap.getRows() / 2;
        
        int midColX = x + (int)(midX * Config.getTileSize() * scaleX);
        g2.fillRect(midColX - 2, y, 4, size);
        
        int midRowY = y + (int)(midY * Config.getTileSize() * scaleY);
        g2.fillRect(x, midRowY - 2, size, 4);
    }
 // dessine les fontaines
    private void drawFountains(Graphics2D g2, float scaleX, float scaleY) {
        for (var tour : arena.tours()) {
            if (tour.tier() == 1) {
                int px = x + (int) (tour.position().x() * Config.getTileSize() * scaleX);
                int py = y + (int) (tour.position().y() * Config.getTileSize() * scaleY);
                g2.setColor(new Color(100, 255, 200, 180));
                g2.fillOval(px - 4, py - 4, 8, 8);
            }
        }
    }

    private Color getTileColor(int tileId) {
        if (tiles != null && tileId >= 0 && tileId < tiles.length && tiles[tileId] != null) {
            return tiles[tileId].getColor();
        }
        return new Color(45, 45, 55);
    }
    
    private BufferedImage getTileImage(int tileId, int row, int col) {
        if (tiles == null || tileId < 0 || tileId >= tiles.length || tiles[tileId] == null) {
            return null;
        }
        
        Tile tile = tiles[tileId];
        
        if (tileId == 5 && tile.getImages().size() > 1) {
            long timeInSeconds = System.currentTimeMillis() / 3000;
            int seed = row * 73 + col * 37 + (int) timeInSeconds;
            int index = Math.abs(new java.util.Random(seed).nextInt()) % tile.getImages().size();
            return tile.getImages().get(index);
        }
        
        return tile.getImage();
    }
//creation img stats minimap
    private BufferedImage createMinimapBackground() {
        BufferedImage background = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = background.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        int mapCols = tileMap.getColumns();
        int mapRows = tileMap.getRows();
        float tileScaleX = size / (float) mapCols;
        float tileScaleY = size / (float) mapRows;
        
        for (int row = 0; row < mapRows; row++) {
            for (int col = 0; col < mapCols; col++) {
                int tileId = tileMap.getTileAt(row, col);
                BufferedImage tileImg = getTileImage(tileId, row, col);
                
                int px = (int) (col * tileScaleX);
                int py = (int) (row * tileScaleY);
                int w = Math.max(1, (int) ((col + 1) * tileScaleX) - px);
                int h = Math.max(1, (int) ((row + 1) * tileScaleY) - py);
                
                if (tileImg != null) {
                    g2.drawImage(tileImg, px, py, w, h, null);
                } else {
                    g2.setColor(getTileColor(tileId));
                    g2.fillRect(px, py, w, h);
                }
            }
        }
        
        g2.dispose();
        return background;
    }
}