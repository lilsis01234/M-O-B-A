package Engine.Render.HUD;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.World.Arena;
import Core.Moba.World.TeamColor;
import Core.Tile.TileMap;
import Engine.Render.Camera;

import java.awt.*;

public class MinimapRenderer {
    private final Arena arena;
    private final TileMap tileMap;
    private final Player player;
    private final int x, y, size;
    private Camera camera;
    private java.util.function.Consumer<Point> onClickCallback;
    private TeamColor playerTeam;

    public MinimapRenderer(Player player, Arena arena, TileMap tileMap, int x, int y, int size) {
        this.player = player;
        this.arena = arena;
        this.tileMap = tileMap;
        this.x = x;
        this.y = y;
        this.size = size;
        this.playerTeam = player.equipe() != null ? player.equipe().couleur() : TeamColor.BLUE;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setOnClickCallback(java.util.function.Consumer<Point> callback) {
        this.onClickCallback = callback;
    }

    public boolean handleClick(int clickX, int clickY) {
        if (clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size) {
            if (camera != null) {
                float mapWidth = tileMap.getColumns() * Config.getTileSize();
                float mapHeight = tileMap.getRows() * Config.getTileSize();
                float scaleX = size / mapWidth;
                float scaleY = size / mapHeight;
                
                float worldX = (clickX - x) / scaleX;
                float worldY = (clickY - y) / scaleY;
                
                camera.setPosition(worldX, worldY);
            }
            return true;
        }
        return false;
    }

    public boolean handleRightClick(int clickX, int clickY) {
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void render(Graphics2D g2) {
        g2.setColor(new Color(10, 10, 15, 240));
        g2.fillRoundRect(x - 3, y - 3, size + 6, size + 6, 12, 12);
        
        g2.setColor(new Color(30, 30, 40));
        g2.fillRoundRect(x, y, size, size, 8, 8);

        float mapWidth = tileMap.getColumns() * Config.getTileSize();
        float mapHeight = tileMap.getRows() * Config.getTileSize();
        float scaleX = size / mapWidth;
        float scaleY = size / mapHeight;

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int col = 0; col < tileMap.getColumns(); col++) {
                int tileId = tileMap.getTileAt(row, col);
                Color tileColor = getTileColor(tileId);
                if (tileColor != null) {
                    int px = x + (int) (col * Config.getTileSize() * scaleX);
                    int py = y + (int) (row * Config.getTileSize() * scaleY);
                    int w = (int) (Config.getTileSize() * scaleX) + 1;
                    int h = (int) (Config.getTileSize() * scaleY) + 1;
                    g2.setColor(tileColor);
                    g2.fillRect(px, py, w, h);
                }
            }
        }

        drawLanes(g2, scaleX, scaleY);

        for (var ancient : arena.ancients()) {
            int px = x + (int) (ancient.position().x() * Config.getTileSize() * scaleX);
            int py = y + (int) (ancient.position().y() * Config.getTileSize() * scaleY);
            boolean isAlly = ancient.equipe().couleur() == playerTeam;
            g2.setColor(isAlly ? new Color(60, 100, 255) : new Color(200, 60, 60));
            g2.fillRect(px - 6, py - 6, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(px - 6, py - 6, 12, 12);
        }

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

        int playerX = x + (int) (player.getX() * scaleX);
        int playerY = y + (int) (player.getY() * scaleY);
        g2.setColor(new Color(100, 255, 100));
        g2.fillOval(playerX - 5, playerY - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(playerX - 5, playerY - 5, 10, 10);

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

    private void drawLanes(Graphics2D g2, float scaleX, float scaleY) {
        g2.setColor(new Color(90, 80, 60, 60));
        
        int midX = tileMap.getColumns() / 2;
        int midY = tileMap.getRows() / 2;
        
        int midColX = x + (int)(midX * Config.getTileSize() * scaleX);
        g2.fillRect(midColX - 2, y, 4, size);
        
        int midRowY = y + (int)(midY * Config.getTileSize() * scaleY);
        g2.fillRect(x, midRowY - 2, size, 4);
    }

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
        return switch (tileId) {
            case 0 -> new Color(45, 95, 45);
            case 1 -> new Color(60, 80, 50);
            case 2 -> new Color(40, 80, 150);
            case 3 -> new Color(100, 90, 70);
            case 4 -> new Color(160, 140, 100);
            case 5 -> new Color(35, 85, 35);
            case 6 -> new Color(45, 95, 45);
            case 7 -> new Color(50, 100, 50);
            case 8 -> new Color(40, 80, 140);
            case 9 -> new Color(30, 70, 30);
            case 10 -> new Color(55, 105, 55);
            case 11 -> new Color(55, 105, 55);
            case 12 -> new Color(40, 90, 40);
            case 13 -> new Color(45, 95, 45);
            case 14 -> new Color(45, 95, 45);
            case 18 -> new Color(50, 100, 50);
            case 20, 21 -> new Color(70, 70, 80);
            case 22, 23 -> new Color(60, 60, 70);
            default -> new Color(45, 45, 55);
        };
    }
}