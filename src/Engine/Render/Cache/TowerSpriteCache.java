package Engine.Render.Cache;

import Core.Moba.Units.Tour;
import Core.Moba.World.TeamColor;
import Engine.Tile.Tile;

import java.awt.image.BufferedImage;
/*
**Cette classe sert à stocker en mémoire les visuels (sprites) des tours 
*/

public class TowerSpriteCache {

    private final Tile[] tiles;

    public TowerSpriteCache(Tile[] tiles) {
        this.tiles = tiles;
    }

    public BufferedImage getTowerFrame(TeamColor teamColor, int frameIndex) {
        int tileId = teamColor == TeamColor.RED ? 21 : 20;
        Tile tile = (tileId >= 0 && tileId < tiles.length) ? tiles[tileId] : null;
        return getFrameFromTile(tile, frameIndex);
    }

    public BufferedImage getCoreBaseFrame(TeamColor teamColor) {
        int tileId = teamColor == TeamColor.RED ? 23 : 22;
        Tile tile = (tileId >= 0 && tileId < tiles.length) ? tiles[tileId] : null;
        return tile != null ? tile.getImage() : null;
    }

    private BufferedImage getFrameFromTile(Tile tile, int frameIndex) {
        if (tile == null) return null;
        Object userData = tile.getUserData();
        if (userData instanceof BufferedImage[]) {
            BufferedImage[] frames = (BufferedImage[]) userData;
                if (frameIndex >= 0 && frameIndex < frames.length) {
                return frames[frameIndex];
            }
        }
        return null;
    }
}