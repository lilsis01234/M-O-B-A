package Core.Moba.Combat;

public final class Stats {
    private int maxHp;
    private int hp;
    private int maxMana;
    private int mana;
    private int attack;
    private int defense;
    private double moveSpeed;

    public Stats(int maxHp, int maxMana, int attack, int defense, double moveSpeed) {
        if (maxHp <= 0) throw new IllegalArgumentException("maxHp must be > 0");
        if (maxMana < 0) throw new IllegalArgumentException("maxMana must be >= 0");
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.attack = Math.max(0, attack);
        this.defense = Math.max(0, defense);
        this.moveSpeed = Math.max(0, moveSpeed);
    }

    public int maxHp() { return maxHp; }
    public int hp() { return hp; }
    public int maxMana() { return maxMana; }
    public int mana() { return mana; }
    public int attack() { return attack; }
    public int defense() { return defense; }
    public double moveSpeed() { return moveSpeed; }

    public void heal(int amount) {
        if (amount <= 0) return;
        hp = Math.min(maxHp, hp + amount);
    }

    public void restoreMana(int amount) {
        if (amount <= 0) return;
        mana = Math.min(maxMana, mana + amount);
    }

    public void takeDamage(int rawDamage) {
        int dmg = Math.max(0, rawDamage);
        hp = Math.max(0, hp - dmg);
    }

    public boolean spendMana(int amount) {
        if (amount <= 0) return true;
        if (mana < amount) return false;
        mana -= amount;
        return true;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void applyModifier(StatsModifier mod) {
        if (mod == null) return;
        maxHp = Math.max(1, maxHp + mod.bonusMaxHp());
        maxMana = Math.max(0, maxMana + mod.bonusMaxMana());
        attack = Math.max(0, attack + mod.bonusAttack());
        defense = Math.max(0, defense + mod.bonusDefense());
        moveSpeed = Math.max(0, moveSpeed + mod.bonusMoveSpeed());

        hp = Math.min(hp, maxHp);
        mana = Math.min(mana, maxMana);
    }
}

