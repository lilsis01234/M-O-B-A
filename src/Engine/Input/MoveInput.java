package Engine.Input;
//Interface pour les entrées pour savoir quel touche de direction sur quoi on a cliqué/
public interface MoveInput {
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isLeftPressed();
    boolean isRightPressed();

    default boolean isAnyKeyPressed() {
        return isUpPressed() || isDownPressed() || isLeftPressed() || isRightPressed();
    }
}

