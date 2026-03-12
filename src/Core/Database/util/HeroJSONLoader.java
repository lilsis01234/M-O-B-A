package Core.Database.util;

import Core.Database.model.*;
import java.io.*;
import java.util.*;

public class HeroJSONLoader {
    
    private static final String HEROES_JSON_PATH = "src/Data/heroes.json";
    
    // Simple data classes for JSON parsing
    public static class HeroData {
        public String name;
        public String history;
        public String category;
        public int baseHp;
        public int maxHp;
        public int attack;
        public int defense;
        public double attackSpeed;
        public int maxMana;
        public int characterRow;
        public int hairRow;
        public String outfitFile;
        public Integer suitRow;
        public List<SpellData> spells;
    }
    
    public static class SpellData {
        public String name;
        public String description;
        public int damage;
        public double cooldown;
        public int manaCost;
        public String type;
    }
    
    /**
     * Load and parse heroes from JSON file without any external libraries
     */
    public static List<HeroData> loadHeroesFromJSON() throws IOException {
        List<HeroData> heroes = new ArrayList<>();
        
        // Read entire file
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(HEROES_JSON_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        }
        
        String content = json.toString();
        System.out.println("Read " + content.length() + " chars from JSON");
        
        // Parse array objects
        if (!content.startsWith("[") || !content.endsWith("]")) {
            throw new IOException("Invalid JSON: must be an array");
        }
        
        // Remove outer array brackets
        content = content.substring(1, content.length() - 1).trim();
        
        // Split into individual hero objects
        List<String> heroObjects = splitJsonObjects(content);
        
        for (String heroObj : heroObjects) {
            HeroData hero = parseHeroObject(heroObj);
            if (hero != null) {
                heroes.add(hero);
            }
        }
        
        System.out.println("Loaded " + heroes.size() + " heroes from JSON");
        return heroes;
    }
    
    /**
     * Split JSON objects at top level, handling nested arrays/objects
     */
    private static List<String> splitJsonObjects(String content) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    objects.add(content.substring(start, i + 1));
                    // Skip comma between objects
                    while (i + 1 < content.length() && (content.charAt(i + 1) == ',' || Character.isWhitespace(content.charAt(i + 1)))) {
                        i++;
                    }
                }
            }
        }
        
        return objects;
    }
    
    /**
     * Parse a single hero JSON object
     */
    private static HeroData parseHeroObject(String json) {
        HeroData hero = new HeroData();
        
        try {
            // Extract each field
            hero.name = extractStringField(json, "name");
            hero.history = extractStringField(json, "history");
            hero.category = extractStringField(json, "category");
            hero.baseHp = extractIntField(json, "baseHp");
            hero.maxHp = extractIntField(json, "maxHp");
            hero.attack = extractIntField(json, "attack");
            hero.defense = extractIntField(json, "defense");
            hero.attackSpeed = extractDoubleField(json, "attackSpeed");
            hero.maxMana = extractIntField(json, "maxMana");
            hero.characterRow = extractIntField(json, "characterRow");
            hero.hairRow = extractIntField(json, "hairRow");
            hero.outfitFile = extractStringField(json, "outfitFile");
            
            String suitRowStr = extractStringField(json, "suitRow");
            if (suitRowStr == null || suitRowStr.isEmpty() || suitRowStr.equals("null")) {
                hero.suitRow = null;
            } else {
                hero.suitRow = Integer.parseInt(suitRowStr);
            }
            
            // Parse spells array
            hero.spells = parseSpellsArray(json);
            
            return hero;
        } catch (Exception e) {
            System.err.println("Error parsing hero: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse the spells array
     */
    private static List<SpellData> parseSpellsArray(String heroJson) {
        List<SpellData> spells = new ArrayList<>();
        
        int spellsStart = heroJson.indexOf("\"spells\"");
        if (spellsStart == -1) return spells;
        
        // Find the array content
        int arrayStart = heroJson.indexOf('[', spellsStart);
        if (arrayStart == -1) return spells;
        
        // Find matching closing bracket
        int depth = 0;
        int arrayEnd = -1;
        for (int i = arrayStart; i < heroJson.length(); i++) {
            if (heroJson.charAt(i) == '[') depth++;
            else if (heroJson.charAt(i) == ']') {
                depth--;
                if (depth == 0) {
                    arrayEnd = i;
                    break;
                }
            }
        }
        
        if (arrayEnd == -1) return spells;
        
        String spellsArray = heroJson.substring(arrayStart + 1, arrayEnd);
        
        // Split spell objects
        List<String> spellObjects = splitJsonObjects(spellsArray);
        
        for (String spellObj : spellObjects) {
            SpellData spell = parseSpellObject(spellObj);
            if (spell != null) {
                spells.add(spell);
            }
        }
        
        return spells;
    }
    
    /**
     * Parse a single spell JSON object
     */
    private static SpellData parseSpellObject(String json) {
        SpellData spell = new SpellData();
        
        try {
            spell.name = extractStringField(json, "name");
            spell.description = extractStringField(json, "description");
            spell.damage = extractIntField(json, "damage");
            spell.cooldown = extractDoubleField(json, "cooldown");
            spell.manaCost = extractIntField(json, "manaCost");
            spell.type = extractStringField(json, "type");
            return spell;
        } catch (Exception e) {
            System.err.println("Error parsing spell: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract a string field value from JSON
     */
    private static String extractStringField(String json, String fieldName) {
        String fieldKey = "\"" + fieldName + "\"";
        int keyStart = json.indexOf(fieldKey);
        if (keyStart == -1) return null;
        
        // Find colon after field name
        int colonPos = json.indexOf(':', keyStart);
        if (colonPos == -1) return null;
        
        // Find opening quote after colon (skip whitespace)
        int pos = colonPos + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
        if (pos >= json.length() || json.charAt(pos) != '"') return null;
        
        int quoteStart = pos;
        int end = quoteStart + 1;
        while (end < json.length()) {
            if (json.charAt(end) == '"') {
                // Check if escaped
                if (end > quoteStart + 1 && json.charAt(end - 1) == '\\') {
                    end++;
                    continue;
                }
                break;
            }
            end++;
        }
        
        if (end >= json.length()) return null;
        
        String value = json.substring(quoteStart + 1, end);
        // Unescape basic escape sequences
        return value.replace("\\\"", "\"")
                    .replace("\\/", "/")
                    .replace("\\b", "\b")
                    .replace("\\f", "\f")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t");
    }
    
    /**
     * Extract an integer field value from JSON
     */
    private static int extractIntField(String json, String fieldName) {
        String fieldKey = "\"" + fieldName + "\"";
        int keyStart = json.indexOf(fieldKey);
        if (keyStart == -1) return 0;
        
        int colonPos = json.indexOf(':', keyStart);
        if (colonPos == -1) return 0;
        
        int pos = colonPos + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
        
        int start = pos;
        while (pos < json.length() && (Character.isDigit(json.charAt(pos)) || json.charAt(pos) == '-')) {
            pos++;
        }
        
        if (start == pos) return 0;
        
        return Integer.parseInt(json.substring(start, pos));
    }
    
    /**
     * Extract a double field value from JSON
     */
    private static double extractDoubleField(String json, String fieldName) {
        String fieldKey = "\"" + fieldName + "\"";
        int keyStart = json.indexOf(fieldKey);
        if (keyStart == -1) return 0.0;
        
        int colonPos = json.indexOf(':', keyStart);
        if (colonPos == -1) return 0.0;
        
        int pos = colonPos + 1;
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
        
        int start = pos;
        while (pos < json.length() && 
               (Character.isDigit(json.charAt(pos)) || 
                json.charAt(pos) == '-' || 
                json.charAt(pos) == '.')) {
            pos++;
        }
        
        if (start == pos) return 0.0;
        
        return Double.parseDouble(json.substring(start, pos));
    }
    
    /**
     * Convert HeroData to database Hero entity
     */
    public static Core.Database.model.Hero convertToDatabaseHero(HeroData data, Core.Database.model.Category category) {
        Core.Database.model.Hero hero = new Core.Database.model.Hero();
        hero.setName(data.name);
        hero.setHistory(data.history);
        hero.setCategoryId(category.getId());
        hero.setBaseHp(data.baseHp);
        hero.setMaxHp(data.maxHp);
        hero.setAttack(data.attack);
        hero.setDefense(data.defense);
        hero.setAttackSpeed(data.attackSpeed);
        hero.setMaxMana(data.maxMana);
        hero.setCharacterRow(data.characterRow);
        hero.setHairRow(data.hairRow);
        hero.setOutfitFile(data.outfitFile);
        hero.setSuitRow(data.suitRow);
        return hero;
    }
    
    /**
     * Convert SpellData to database Spell entity
     */
    public static Core.Database.model.Spell convertToSpell(SpellData data, int heroId) {
        Core.Database.model.Spell spell = new Core.Database.model.Spell();
        spell.setHeroId(heroId);
        spell.setName(data.name);
        spell.setDescription(data.description);
        spell.setDamage(data.damage);
        spell.setCooldown(data.cooldown);
        spell.setManaCost(data.manaCost);
        spell.setType(data.type);
        return spell;
    }
}
