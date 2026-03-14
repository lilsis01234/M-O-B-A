package Core.Moba.Ids;

import java.util.Objects;
import java.util.UUID;

public final class GameId {
    private final String value;

    private GameId(String value) {
        this.value = value;
    }

    public static GameId random() {
        return new GameId(UUID.randomUUID().toString());
    }

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

