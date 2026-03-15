package Core.Moba.Ids;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifiant unique pour les objets du jeu.
 */
public final class GameId {
    private final String value;

    private GameId(String value) {
        this.value = value;
    }

    /**
     * Génère un identifiant aléatoire.
     * @return Une nouvelle instance de GameId
     */
    public static GameId random() {
        return new GameId(UUID.randomUUID().toString());
    }

    /**
     * Crée un GameId à partir d'une chaîne
     * @param value La chaîne à utiliser comme identifiant.
     * @return Une instance de GameId
     * @throws IllegalArgumentException si la valeur est vide.
     */
    public static GameId of(String value) {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) throw new IllegalArgumentException("GameId cannot be blank");
        return new GameId(value);
    }

   public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Comparaison 
     **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameId gameId)) return false;
        return value.equals(gameId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
