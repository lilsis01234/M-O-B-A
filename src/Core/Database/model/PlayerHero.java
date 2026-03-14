package Core.Database.model;

public class PlayerHero {
    private int id;
    private int playerId;
    private int heroId;
    private int level;
    private int experience;
    private int spell1Level;
    private int spell2Level;
    private int spell3Level;
    
    public PlayerHero() {}
    
    public PlayerHero(int playerId, int heroId, int level, int experience, 
                     int spell1Level, int spell2Level, int spell3Level) {
        this.playerId = playerId;
        this.heroId = heroId;
        this.level = level;
        this.experience = experience;
        this.spell1Level = spell1Level;
        this.spell2Level = spell2Level;
        this.spell3Level = spell3Level;
    }
    
    public PlayerHero(int id, int playerId, int heroId, int level, int experience, 
                     int spell1Level, int spell2Level, int spell3Level) {
        this.id = id;
        this.playerId = playerId;
        this.heroId = heroId;
        this.level = level;
        this.experience = experience;
        this.spell1Level = spell1Level;
        this.spell2Level = spell2Level;
        this.spell3Level = spell3Level;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public int getHeroId() {
        return heroId;
    }
    
    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void setExperience(int experience) {
        this.experience = experience;
    }
    
    public int getSpell1Level() {
        return spell1Level;
    }
    
    public void setSpell1Level(int spell1Level) {
        this.spell1Level = spell1Level;
    }
    
    public int getSpell2Level() {
        return spell2Level;
    }
    
    public void setSpell2Level(int spell2Level) {
        this.spell2Level = spell2Level;
    }
    
    public int getSpell3Level() {
        return spell3Level;
    }
    
    public void setSpell3Level(int spell3Level) {
        this.spell3Level = spell3Level;
    }
}
