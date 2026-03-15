package Core.Moba.Match;

import Core.Moba.Ids.GameId;
import Core.Moba.World.Equipe;
import java.util.Objects;

/**
 * Classe pour la partie et les évènements de la partie
 * */
public final class Partie {
    private final GameId id;
    private final Equipe equipe1;
    private final Equipe equipe2;

    private double dureeSecondes;
    private boolean terminee;

    public Partie(GameId id, Equipe equipe1, Equipe equipe2) {
        this.id = Objects.requireNonNull(id, "id");
        this.equipe1 = Objects.requireNonNull(equipe1, "equipe1");
        this.equipe2 = Objects.requireNonNull(equipe2, "equipe2");
    }

    public GameId id() {
        return id;
    }

    public double dureeSecondes() {
        return dureeSecondes;
    }

    public boolean estTerminee() {
        return terminee;
    }

    public Equipe equipe1() {
        return equipe1;
    }

    public Equipe equipe2() {
        return equipe2;
    }

    public void update(double deltaSeconds) {
        if (terminee) return;
        if (deltaSeconds <= 0) return;

        dureeSecondes += deltaSeconds;

        if (equipe1.base().estDetruite() || equipe2.base().estDetruite()) {
            terminee = true;
        }
    }
}
