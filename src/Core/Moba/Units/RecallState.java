package Core.Moba.Units;

/**
 * Gère le rappel à la base (Recall).
 **/
public final class RecallState {
    private final double recallDurationSeconds;
    private double remainingSeconds;
    public RecallState(double recallDurationSeconds) {
        this.recallDurationSeconds = Math.max(0, recallDurationSeconds);
        this.remainingSeconds = 0;
    }

    public boolean isRecalling() {
        return remainingSeconds > 0;
    }

    /** Démarre le rappel. */
    public void demarrerRecall() {
        remainingSeconds = recallDurationSeconds;
    }

    /** Interrompt le rappel. */
    public void annulerRecall() {
        remainingSeconds = 0;
    }

    /**
     * Met à jour le chronomètre de rappel.
     * @param deltaSeconds Temps écoulé depuis la dernière mise à jour.
     */
    public void update(double deltaSeconds) {
        if (deltaSeconds <= 0) return;
        if (remainingSeconds > 0) {
            remainingSeconds = Math.max(0, remainingSeconds - deltaSeconds);
        }
    }

    /**
     *@return true si le temps de rappel est écoulé 
     **/
    public boolean estTermine() {
        return remainingSeconds == 0;
    }
}
