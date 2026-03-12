package Core.Entity;

import Core.Config;
import Core.Input.MoveInput;
import Core.Input.TargetInput;
import Core.Entity.MathUtils;

import java.util.List;

/**
 * Gère toute la logique de mouvement du joueur.
 * C'est le chef d'orchestre qui décide : tu vas où ? Comment ? Par où ?
 * 
 * @see Player Le boss qui utilise cette classe
 * @see CollisionDetector Le buddy qui dit si on peut passer
 * @see PathFollower Le GPS intégré
 */
public class PlayerMovement {

    // Inputs - comment on contrôle le bonhomme
    private final MoveInput moveInput;
    private final TargetInput targetInput;
    
    // Les helpers - on delegate tout à eux
    private final CollisionDetector collisionDetector;
    private final PathFollower pathFollower;

    // Où on va ? (en pixels, position haut-gauche)
    private double targetX = -1;
    private double targetY = -1;
    private boolean hasTarget = false;
    private Direction targetDirection = Direction.DOWN;

    // Pour détecter si on est bloqué - le compteur de galère
    private int stuckCounter = 0;
    private double lastPathX = -1;
    private double lastPathY = -1;

    public PlayerMovement(MoveInput moveInput, TargetInput targetInput, 
                          CollisionDetector collisionDetector, PathFollower pathFollower) {
        this.moveInput = moveInput;
        this.targetInput = targetInput;
        this.collisionDetector = collisionDetector;
        this.pathFollower = pathFollower;
    }

    /**
     * Update principal - appelé à chaque frame.
     * C'est le cœur du mouvement : on gère clic souris, clavier, et animations.
     */
    public void update(Entity entity) {
        boolean isMoving = false;
        
        // Nouveau clic ? On traite ça en priorité
        if (targetInput.hasTarget()) {
            processMouseClick(entity);
        }
        
        // Si on a une destination, on se déplace
        if (hasTarget) {
            isMoving = moveToTarget(entity);
            if (isMoving) {
                checkStuckAndRecalculate(entity); // Tu es coincé ? On recalcule
            }
        } 
        // Sinon, si on appuie sur le clavier
        else if (moveInput.isAnyKeyPressed()) {
            isMoving = handleKeyboardMovement(entity);
            pathFollower.clearPath(); // Plus de chemin quand on joue au clavier
        }
        
        // Si on bouge, on anime le sprite
        if (isMoving) {
            entity.updateSpriteAnimation(Config.getSpriteAnimationSpeed());
        }
    }

    /**
     * Traitement du clic souris - le début de l'aventure.
     * On either va direct si possible, soit on demande au GPS de trouver un chemin.
     */
    private void processMouseClick(Entity entity) {
        int tileSize = Config.getTileSize();
        double half = tileSize / 2.0;
        
        // Convertir le clic (centre) en position haut-gauche
        double clickedX = targetInput.getTargetX() - half;
        double clickedY = targetInput.getTargetY() - half;

        // Chemin direct dégagé ? Nickel, on y va
        if (collisionDetector.isPathClear(entity.getX(), entity.getY(), clickedX, clickedY)) {
            targetX = clickedX;
            targetY = clickedY;
            hasTarget = true;
            pathFollower.clearPath();
        } 
        // Sinon, on demande au GPS de trouver un chemin
        else {
            int startCol = (int) Math.floor((entity.getX() + half) / tileSize);
            int startRow = (int) Math.floor((entity.getY() + half) / tileSize);
            int targetCol = (int) Math.floor((clickedX + half) / tileSize);
            int targetRow = (int) Math.floor((clickedY + half) / tileSize);

            List<int[]> path = pathFollower.findPath(startCol, startRow, targetCol, targetRow);
            if (path != null && path.size() > 1) {
                // Trouvé ! On smooth le chemin pour faire plus propre
                pathFollower.setPath(path);
                pathFollower.smoothPath(collisionDetector);
                pathFollower.setPathIndex(1); // Skip le point de départ
                
                // Premier node du chemin comme cible
                int[] firstNode = path.get(1);
                targetX = firstNode[0] * tileSize;
                targetY = firstNode[1] * tileSize;
                
                hasTarget = true;
            } else {
                // Pas de chemin trouvé... on tente quand même direct (c'est courageux)
                targetX = clickedX;
                targetY = clickedY;
                hasTarget = true;
                pathFollower.clearPath();
            }
        }
        targetInput.clearTarget(); // Reset le clic
    }

    /**
     * Se déplacer vers la cible - la logique de mouvement pure.
     * Calcule la direction, la distance, et applique le mouvement.
     */
    private boolean moveToTarget(Entity entity) {
        double dx = targetX - entity.getX();
        double dy = targetY - entity.getY();
        double distance = MathUtils.distance(entity.getX(), entity.getY(), targetX, targetY);
        
        // Assez loin pour bouger ?
        if (distance > entity.getSpeed()) {
            // Direction normalisée
            double ratioX = dx / distance;
            double ratioY = dy / distance;
            
            // Mise à jour de la direction du sprite
            targetDirection = Direction.fromDelta((int) Math.round(dx), (int) Math.round(dy));
            entity.setDirection(targetDirection);
            
            // Nouvelle position
            double newX = entity.getX() + ratioX * entity.getSpeed();
            double newY = entity.getY() + ratioY * entity.getSpeed();
            
            return tryMove(entity, newX, newY); // On tente le mouvement
        } else {
            return handleTargetReached(entity); // On est arrivé !
        }
    }

    /**
     * Tente de déplacer l'entité. Si bloqué, soit on slide soit on abandonne.
     * Le slide, c'est quand tu peux pas aller diagonal mais tu peux aller droit.
     */
    private boolean tryMove(Entity entity, double newX, double newY) {
        boolean isFollowingPath = pathFollower.hasPath();
        
        // Pas de collision ? Parfait
        if (!collisionDetector.collidesAt(newX, newY)) {
            entity.setX(newX);
            entity.setY(newY);
            return true;
        } 
        // Collision mais on suit un chemin ? On tente le slide
        else if (isFollowingPath) {
            // Slide sur X
            if (!collisionDetector.collidesAt(newX, entity.getY())) {
                entity.setX(newX);
                return true;
            }
            // Slide sur Y
            if (!collisionDetector.collidesAt(entity.getX(), newY)) {
                entity.setY(newY);
                return true;
            }
            
            // Slide complet - la totale
            return trySlideMovement(entity, newX, newY);
        } 
        // Pas de chemin, collision = stop
        else {
            hasTarget = false;
            resetTarget();
            stuckCounter = 0;
            return false;
        }
    }

    /**
     * Slide movement - quand on est bloqué mais on veut quand même avancer.
     * Essaie X, puis Y, puis les combos... comme un robot débloqué.
     */
    private boolean trySlideMovement(Entity entity, double newX, double newY) {
        double speed = entity.getSpeed();
        
        if (!collisionDetector.collidesAt(newX - speed, newY)) {
            entity.setX(newX - speed);
            return true;
        }
        if (!collisionDetector.collidesAt(newX + speed, newY)) {
            entity.setX(newX + speed);
            return true;
        }
        if (!collisionDetector.collidesAt(newX, newY - speed)) {
            entity.setY(newY - speed);
            return true;
        }
        if (!collisionDetector.collidesAt(newX, newY + speed)) {
            entity.setY(newY + speed);
            return true;
        }
        
        // nada... on avance dans le chemin
        pathFollower.advancePath();
        if (pathFollower.hasPath()) {
            setNextPathTargetFromFollower();
            return true;
        }
        
        stuckCounter = 0;
        return false;
    }

    /**
     * Met à jour la cible avec le prochain node du chemin.
     * Le GPS dit "tourne ici".
     */
    private void setNextPathTargetFromFollower() {
        int[] tile = pathFollower.getCurrentPathTarget();
        if (tile != null) {
            int tileSize = Config.getTileSize();
            targetX = tile[0] * tileSize;
            targetY = tile[1] * tileSize;
        }
    }

    /**
     * Cible atteinte ! Vérifie si y'a un autre node dans le chemin.
     * Comme un train :下一站下一站
     */
    private boolean handleTargetReached(Entity entity) {
        if (pathFollower.hasPath()) {
            pathFollower.advancePath();
            if (pathFollower.hasPath()) {
                setNextPathTargetFromFollower();
                stuckCounter = 0;
                return true;
            }
        }

        // Snap à la position finale (propre)
        if (!collisionDetector.collidesAt(targetX, targetY)) {
            entity.setX(targetX);
            entity.setY(targetY);
        }
        
        hasTarget = false;
        resetTarget();
        stuckCounter = 0;
        return false;
    }

    /**
     * Check si on est coincé - le système anti-blocage.
     * Si on bouge plus depuis 30 frames, on recalcule le chemin.
     */
    private void checkStuckAndRecalculate(Entity entity) {
        if (!pathFollower.hasPath() || !hasTarget) {
            stuckCounter = 0;
            return;
        }
        
        double distToTarget = MathUtils.distance(entity.getX(), entity.getY(), targetX, targetY);
        
        if (distToTarget < 1) {
            stuckCounter = 0;
            return;
        }
        
        // On a avancé ? Non ? Compteur++
        if (Math.abs(lastPathX - entity.getX()) < 1 && Math.abs(lastPathY - entity.getY()) < 1) {
            stuckCounter++;
            if (stuckCounter > 30) {
                recalculatePath(entity); // Bon, on fait quoi ?
            }
        } else {
            stuckCounter = 0;
        }
        
        lastPathX = entity.getX();
        lastPathY = entity.getY();
    }

    /**
     * Recalcule le chemin - le "je suis perdu" du joueur.
     */
    private void recalculatePath(Entity entity) {
        int tileSize = Config.getTileSize();
        double half = tileSize / 2.0;
        int startCol = (int) Math.floor((entity.getX() + half) / tileSize);
        int startRow = (int) Math.floor((entity.getY() + half) / tileSize);
        int targetCol = (int) Math.floor((targetX + half) / tileSize);
        int targetRow = (int) Math.floor((targetY + half) / tileSize);
        
        List<int[]> newPath = pathFollower.findPath(startCol, startRow, targetCol, targetRow);
        if (newPath != null && newPath.size() > 1) {
            pathFollower.setPath(newPath);
            pathFollower.smoothPath(collisionDetector);
            pathFollower.setPathIndex(1);
            pathFollower.setNextPathTarget();
            stuckCounter = 0;
        }
    }

    /**
     * Gestion du mouvement clavier - WASD mode.
     * Plus direct que le clic, mais attention aux murs.
     */
    private boolean handleKeyboardMovement(Entity entity) {
        int xAxis = 0;
        int yAxis = 0;

        if (moveInput.isLeftPressed()) xAxis -= 1;
        if (moveInput.isRightPressed()) xAxis += 1;
        if (moveInput.isUpPressed()) yAxis -= 1;
        if (moveInput.isDownPressed()) yAxis += 1;

        if (xAxis == 0 && yAxis == 0) return false;

        updateDirectionFromInput(entity, xAxis, yAxis);
        
        // Normaliser la vitesse diagonale (pas plus rapide en diagonal !)
        double len = Math.sqrt(xAxis * xAxis + yAxis * yAxis);
        double stepX = (xAxis / len) * entity.getSpeed();
        double stepY = (yAxis / len) * entity.getSpeed();

        double currentX = entity.getX();
        double currentY = entity.getY();
        double nextX = currentX + stepX;
        double nextY = currentY + stepY;

        return tryFullOrSlidingMove(entity, currentX, currentY, nextX, nextY);
    }

    /**
     * Met à jour la direction du sprite selon les touches.
     * En diagonal, on privilégie l'axe dominant.
     */
    private void updateDirectionFromInput(Entity entity, int xAxis, int yAxis) {
        if (xAxis != 0 && yAxis != 0) {
            if (Math.abs(xAxis) >= Math.abs(yAxis)) {
                entity.setDirection(xAxis > 0 ? Direction.RIGHT : Direction.LEFT);
            } else {
                entity.setDirection(yAxis > 0 ? Direction.DOWN : Direction.UP);
            }
        } else if (xAxis != 0) {
            entity.setDirection(xAxis > 0 ? Direction.RIGHT : Direction.LEFT);
        } else {
            entity.setDirection(yAxis > 0 ? Direction.DOWN : Direction.UP);
        }
    }

    /**
     * Tente mouvement complet avec slide si bloqué.
     * Le ninja qui se faufile entre les obstacles.
     */
    private boolean tryFullOrSlidingMove(Entity entity, double currentX, double currentY, 
                                      double nextX, double nextY) {
        if (!collisionDetector.collidesAt(nextX, nextY)) {
            entity.setX(nextX);
            entity.setY(nextY);
            return true;
        }
        if (!collisionDetector.collidesAt(nextX, currentY)) {
            entity.setX(nextX);
            return true;
        }
        if (!collisionDetector.collidesAt(currentX, nextY)) {
            entity.setY(nextY);
            return true;
        }
        return false;
    }

    private void resetTarget() {
        targetX = -1;
        targetY = -1;
    }

    public void setInitialPosition(double x, double y) {
        lastPathX = x;
        lastPathY = y;
    }
}
