package Engine.Input;

public interface MoveInput {
    boolean isUpPressed();
    boolean isDownPressed();
    boolean isLeftPressed();
    boolean isRightPressed();

    default boolean isAnyKeyPressed() {
        return isUpPressed() || isDownPressed() || isLeftPressed() || isRightPressed();
    }
}
