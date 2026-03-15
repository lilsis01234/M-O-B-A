package Engine;

import Core.Config;
import Core.Database.model.Hero;
import Core.Entity.CollisionDetector;
import Core.Entity.PathFollower;
import Core.Entity.Player;
import Core.Moba.Units.Tour;
import Core.Moba.World.*;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;
import Engine.Input.KeyHandler;
import Engine.Input.MouseHandler;
import Engine.Render.Camera;
import Engine.Tile.MapParser;
import Engine.Tile.Tile;
import Engine.Tile.TileLoader;

public class GameInitializer {

    public record GameContext(
        TileMap tileMap,
        
        Tile[] tiles,
        CollisionTable collisionTable,
        Player player,
        Arena arena
    ) {}

    public static GameContext initializeGame(Hero hero, KeyHandler keyHandler, 
                                             MouseHandler mouseHandler, Camera camera) {
        TileMap tileMap = parseTileMap();
        Tile[] tiles = loadTiles();
        CollisionTable collisionTable = buildCollisionTable(tiles);

        Arena arena = createArena(tileMap);
        Player player = createPlayer(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);

        return new GameContext(tileMap, tiles, collisionTable, player, arena);
    }

    private static TileMap parseTileMap() {
        MapParser mapParser = new MapParser();
        MapParser.MapData mapData = mapParser.parse(Config.getMapFilePath());
        return new TileMap(mapData.tileNumbers(), mapData.columns(), mapData.rows());
    }

    private static Tile[] loadTiles() {
        TileLoader tileLoader = new TileLoader();
        return tileLoader.load(Config.getMapFilePath(), Config.getMaxTiles());
    }

    private static CollisionTable buildCollisionTable(Tile[] tiles) {
        TileLoader tileLoader = new TileLoader();
        return new CollisionTable(tileLoader.buildCollisionTable(tiles));
    }

    private static Arena createArena(TileMap tileMap) {
        Arena arena = new Arena();

        Base blueBase = new Base(5000);
        Fontaine blueFontaine = new Fontaine(new Vec2(5, 95), 100, 50);
        Equipe blueTeam = new Equipe("Radiant", TeamColor.BLUE, blueBase, blueFontaine);

        Base redBase = new Base(5000);
        Fontaine redFontaine = new Fontaine(new Vec2(95, 5), 100, 50);
        Equipe redTeam = new Equipe("Dire", TeamColor.RED, redBase, redFontaine);

        arena.initializeFromMap(tileMap, blueTeam, redTeam);

        return arena;
    }

    private static Player createPlayer(KeyHandler keyHandler, MouseHandler mouseHandler,
                                       TileMap tileMap, CollisionTable collisionTable, 
                                       Arena arena, Hero hero) {
        Player player = new Player(keyHandler, mouseHandler, tileMap, collisionTable, arena, hero);
        arena.ajouterUnite(player);
        return player;
    }
}
