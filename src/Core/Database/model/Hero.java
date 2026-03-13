package Core.Database.model;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private int id;
    private String name;
    private String history;
    private int categoryId;
    private int baseHp;
    private int maxHp;
    private int attack;
    private int defense;
    private double attackSpeed;
    private int maxMana;
    private double speed;
    private int characterRow; // Row in Character Model.png (body type/skin)
    private int hairRow; // Row in Hairs.png
    private String outfitFile; // Outfit PNG filename in Outfits folder
    private Integer suitRow; // Row in Suit.png (null if no suit)
    private List<Spell> spells;
    
    public Hero() {
        this.spells = new ArrayList<>();
    }
    
    public Hero(int id, String name, String history, int categoryId, int baseHp, int maxHp, 
                int attack, int defense, double attackSpeed, int maxMana) {
        this.id = id;
        this.name = name;
        this.history = history;
        this.categoryId = categoryId;
        this.baseHp = baseHp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.attackSpeed = attackSpeed;
        this.maxMana = maxMana;
        this.spells = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHistory() {
        return history;
    }
    
    public void setHistory(String history) {
        this.history = history;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getBaseHp() {
        return baseHp;
    }
    
    public void setBaseHp(int baseHp) {
        this.baseHp = baseHp;
    }
    
    public int getMaxHp() {
        return maxHp;
    }
    
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
    
    public int getAttack() {
        return attack;
    }
    
    public void setAttack(int attack) {
        this.attack = attack;
    }
    
    public int getDefense() {
        return defense;
    }
    
    public void setDefense(int defense) {
        this.defense = defense;
    }
    
    public double getAttackSpeed() {
        return attackSpeed;
    }
    
    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
    
    public int getMaxMana() {
        return maxMana;
    }
    
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public int getCharacterRow() {
        return characterRow;
    }
    
    public void setCharacterRow(int characterRow) {
        this.characterRow = characterRow;
    }
    
    public int getHairRow() {
        return hairRow;
    }
    
    public void setHairRow(int hairRow) {
        this.hairRow = hairRow;
    }
    
    public String getOutfitFile() {
        return outfitFile;
    }
    
    public void setOutfitFile(String outfitFile) {
        this.outfitFile = outfitFile;
    }
    
    public Integer getSuitRow() {
        return suitRow;
    }
    
    public void setSuitRow(Integer suitRow) {
        this.suitRow = suitRow;
    }
    
    public List<Spell> getSpells() {
        return spells;
    }
    
    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }
    
    public void addSpell(Spell spell) {
        this.spells.add(spell);
    }
}
