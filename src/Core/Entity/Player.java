package Core.Entity;

import Core.Config;
import Core.Input.MoveInput;
import Core.Input.TargetInput;
import Core.Moba.World.Arena;
import Core.Tile.CollisionTable;
import Core.Tile.TileMap;

/**
 * Représente le joueur contrôlé par l'utilisateur.
 */
public class Player extends Entity {
    
    private final MoveInput moveInput;
    private final TargetInput targetInput;
    private final CollisionDetector collisionDetector;
    private final PathFollower pathFollower;
    private final PlayerMovement movement;
    
    public Player(MoveInput moveInput, TargetInput targetInput, 
                  TileMap tileMap, CollisionTable collisionTable, Arena arena) {
        this.moveInput = moveInput;
        this.targetInput = targetInput;
        
        this.collisionDetector = new CollisionDetector(tileMap, collisionTable, arena);
        this.pathFollower = new PathFollower(tileMap, collisionTable, arena);
        this.movement = new PlayerMovement(moveInput, targetInput, collisionDetector, pathFollower);
        
        // Position initiale
        setX(Config.getPlayerDefaultX());
        setY(Config.getPlayerDefaultY());
        setSpeed(Config.getPlayerDefaultSpeed());
        setDirection(Direction.DOWN);
        
        movement.setInitialPosition(getX(), getY());
    }
    
    public void update() {
        movement.update(this);
    }
}
