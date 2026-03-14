package Core.Moba.Units;

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

    public void demarrerRecall() {
        remainingSeconds = recallDurationSeconds;
    }

    public void annulerRecall() {
        remainingSeconds = 0;
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds <= 0) return;
        remainingSeconds = Math.max(0, remainingSeconds - deltaSeconds);
    }

    public boolean estTermine() {
        return remainingSeconds == 0;
    }
}

