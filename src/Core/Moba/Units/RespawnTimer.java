package Core.Moba.Units;
/**
 * Gère le temps de réapparition (respawn)
 **/

public final class RespawnTimer {
    private double remainingSeconds;

    public RespawnTimer() {
        this.remainingSeconds = 0;
    }

    public void start(double seconds) {
        remainingSeconds = Math.max(0, seconds);
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds <= 0) return;
        remainingSeconds = Math.max(0, remainingSeconds - deltaSeconds);
    }

    public boolean isRunning() {
        return remainingSeconds > 0;
    }

    public double remainingSeconds() {
        return remainingSeconds;
    }
}

