package Core.Moba.Spells;

import Core.Moba.Units.Heros;
import Core.Moba.Units.Unite;

import java.util.Objects;

public record SortContext(Heros caster, Unite target) {
    public SortContext {
        Objects.requireNonNull(caster, "caster");
    }
}

