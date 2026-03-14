package Engine.Render.World;

import Core.Config;
import Core.Entity.Player;
import Core.Moba.Units.Ancient;
import Core.Moba.Units.Tour;
import Core.Moba.Units.TowerProjectile;
import Core.Moba.World.Arena;
import Core.Tile.TileMap;
import Engine.Render.Camera;
import Engine.Render.ClickEffect;
import Engine.Render.DebugRenderer;
import Engine.Tile.Tile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldRenderer {

    private final TileRenderer tileRenderer;
    private final PlayerRenderer playerRenderer;
    private final TowerRenderer towerRenderer;
    private final ProjectileRenderer projectileRenderer;
    private final DebugRenderer debugRenderer;

    private final Player player;
    private final Arena arena;
    private final Camera camera;
    private final GameEngineRef gameEngineRef;
    private int panelWidth;
    private int panelHeight;

    public interface GameEngineRef {
        List<TowerProjectile> getProjectiles();
        List<ClickEffect> getClickEffects();
    }

    public WorldRenderer(TileMap tileMap, Tile[] tiles, Player player, 
                        Arena arena, Camera camera, GameEngineRef gameEngineRef) {
        this.player = player;
        this.arena = arena;
        this.camera = camera;
        this.gameEngineRef = gameEngineRef;

        this.tileRenderer = new TileRenderer(tileMap, tiles);
        this.playerRenderer = new PlayerRenderer(player);
        this.towerRenderer = createTowerRenderer(tiles);
        this.projectileRenderer = new ProjectileRenderer();
        this.debugRenderer = new DebugRenderer();
    }

    public void setPanelSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
    }

    private TowerRenderer createTowerRenderer(Tile[] tiles) {
        TowerRenderer renderer = new TowerRenderer();
        renderer.setTiles(tiles);
        return renderer;
    }

    public void render(Graphics2D g2) {
        AffineTransform oldTransform = applyCameraTransform(g2);

        renderTiles(g2);
        renderDepthSorted(g2);
        renderProjectiles(g2);
        renderClickEffects(g2);
        renderDebug(g2);

        g2.setTransform(oldTransform);
    }

    private AffineTransform applyCameraTransform(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.scale(camera.getZoom(), camera.getZoom());
        g2.translate(-camera.getX(), -camera.getY());
        return oldTransform;
    }

    private void renderTiles(Graphics2D g2) {
        tileRenderer.draw(g2, camera, panelWidth, panelHeight);
    }

    private void renderDepthSorted(Graphics2D g2) {
        List<RenderableEntity> entities = new ArrayList<>();

        int tileSize = Config.getTileSize();

        for (Tour tower : arena.tours()) {
            double towerPixelY = tower.position().y() * tileSize;
            double towerBaseY = towerPixelY + (tower.height() * tileSize);
            entities.add(new RenderableEntity(towerBaseY, RenderableEntity.Type.TOWER, tower));
        }

        for (Ancient ancient : arena.ancients()) {
            double ancientPixelY = ancient.position().y() * tileSize;
            double ancientBaseY = ancientPixelY + (ancient.height() * tileSize);
            entities.add(new RenderableEntity(ancientBaseY, RenderableEntity.Type.ANCIENT, ancient));
        }

        if (player.isAlive()) {
            double playerBaseY = player.getY() + tileSize;
            entities.add(new RenderableEntity(playerBaseY, RenderableEntity.Type.PLAYER, player));
        }

        entities.sort(Comparator.comparingDouble(e -> e.renderY));

        for (RenderableEntity entity : entities) {
            switch (entity.type) {
                case TOWER -> towerRenderer.draw(g2, (Tour) entity.entity, camera);
                case ANCIENT -> towerRenderer.drawAncient(g2, (Ancient) entity.entity, camera);
                case PLAYER -> playerRenderer.draw(g2, player);
            }
        }
    }

    private static class RenderableEntity {
        enum Type { TOWER, ANCIENT, PLAYER }
        
        final double renderY;
        final Type type;
        final Object entity;

        RenderableEntity(double renderY, Type type, Object entity) {
            this.renderY = renderY;
            this.type = type;
            this.entity = entity;
        }
    }

    private void renderProjectiles(Graphics2D g2) {
        if (gameEngineRef != null) {
            for (TowerProjectile projectile : gameEngineRef.getProjectiles()) {
                projectileRenderer.draw(g2, projectile, camera);
            }
        }
    }

    private void renderClickEffects(Graphics2D g2) {
        if (gameEngineRef != null) {
            for (ClickEffect effect : gameEngineRef.getClickEffects()) {
                effect.draw(g2);
            }
        }
    }

    private void renderDebug(Graphics2D g2) {
        debugRenderer.render(g2, player, arena.tours(), arena.ancients());
    }

    public DebugRenderer getDebugRenderer() {
        return debugRenderer;
    }
}
