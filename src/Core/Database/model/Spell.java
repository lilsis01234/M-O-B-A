package Core.Database.model;

public class Spell {
    private int id;
    private int heroId;
    private String name;
    private String description;
    private int damage;
    private double cooldown;
    private int manaCost;
    private String type; // "dmg", "CC", "SP"
    
    public Spell() {}
    
    public Spell(int id, int heroId, String name, String description, int damage, 
                 double cooldown, int manaCost, String type) {
        this.id = id;
        this.heroId = heroId;
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
        this.type = type;
    }
    
    public Spell(int heroId, String name, String description, int damage, 
                 double cooldown, int manaCost, String type) {
        this.heroId = heroId;
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.cooldown = cooldown;
        this.manaCost = manaCost;
        this.type = type;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getHeroId() {
        return heroId;
    }
    
    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public double getCooldown() {
        return cooldown;
    }
    
    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }
    
    public int getManaCost() {
        return manaCost;
    }
    
    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
