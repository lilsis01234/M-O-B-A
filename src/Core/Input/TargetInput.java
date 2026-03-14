package Core.Input;

/**
 * Interface définissant les entrées liées à un ciblage ou à une destination précise.
**/

public interface TargetInput {
    boolean hasTarget();
    int getTargetX();
    int getTargetY();
    void clearTarget();
}

