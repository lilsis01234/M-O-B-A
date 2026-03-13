package Core.Database;

import Core.Database.model.*;
import java.io.*;
import java.util.*;

public class JsonDataProvider {
    
    private static final String HEROES_FILE = "src/Data/heroes.json";
    
    private List<Hero> heroes;
    private List<Category> categories;
    private Map<Integer, List<Spell>> heroSpells;
    
    private static JsonDataProvider instance;
    
    private JsonDataProvider() throws IOException {
        loadData();
    }
    
    public static synchronized JsonDataProvider getInstance() throws IOException {
        if (instance == null) {
            instance = new JsonDataProvider();
        }
        return instance;
    }
    
    private void loadData() throws IOException {
        // Initialize categories (hardcoded, matching old system)
        categories = new ArrayList<>();
        categories.add(new Category("Force"));
        categories.add(new Category("Agilité"));
        categories.add(new Category("Intelligence"));
        
        // Load heroes and spells from JSON
        heroes = new ArrayList<>();
        heroSpells = new HashMap<>();
        
        String json = readJSONFile();
        parseHeroes(json);
    }
    
    private String readJSONFile() throws IOException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(HEROES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        }
        return json.toString();
    }
    
    private void parseHeroes(String json) {
        // Simple JSON parser for the expected structure
        List<Map<String, String>> heroObjects = parseJsonArray(json);
        
        for (Map<String, String> heroMap : heroObjects) {
            Hero hero = new Hero();
            
            // Required fields
            hero.setName(heroMap.get("name"));
            hero.setHistory(heroMap.get("history"));
            hero.setCategoryId(getCategoryId(heroMap.get("category")));
            hero.setBaseHp(parseInt(heroMap.get("baseHp")));
            hero.setMaxHp(parseInt(heroMap.get("maxHp")));
            hero.setAttack(parseInt(heroMap.get("attack")));
            hero.setDefense(parseInt(heroMap.get("defense")));
            hero.setAttackSpeed(parseDouble(heroMap.get("attackSpeed")));
            hero.setMaxMana(parseInt(heroMap.get("maxMana")));
            hero.setSpeed(parseDouble(heroMap.get("speed")));
            hero.setCharacterRow(parseInt(heroMap.get("characterRow")));
            hero.setHairRow(parseInt(heroMap.get("hairRow")));
            hero.setOutfitFile(heroMap.get("outfitFile"));
            
            // Optional field
            String suitRowStr = heroMap.get("suitRow");
            if (suitRowStr != null && !suitRowStr.isEmpty() && !suitRowStr.equals("null")) {
                hero.setSuitRow(parseInt(suitRowStr));
            }
            
            heroes.add(hero);
            
            // Load spells
            List<Spell> spells = new ArrayList<>();
            List<Map<String, String>> spellObjects = parseJsonArray(heroMap.get("spells"));
            for (Map<String, String> spellMap : spellObjects) {
                Spell spell = new Spell();
                spell.setHeroId(hero.getId());
                spell.setName(spellMap.get("name"));
                spell.setDescription(spellMap.get("description"));
                spell.setType(spellMap.get("type"));
                spell.setDamage(parseInt(spellMap.get("damage")));
                spell.setCooldown(parseDouble(spellMap.get("cooldown")));
                spell.setManaCost(parseInt(spellMap.get("manaCost")));
                spells.add(spell);
            }
            heroSpells.put(hero.getId(), spells);
        }
    }
    
    private List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        // Remove outer array brackets
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
        // Split objects at "}," separator (handles nested objects/arrays by tracking braces)
        int braceCount = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // Found complete object
                    if (i + 1 < json.length() && json.charAt(i + 1) == ',') {
                        String objStr = json.substring(start, i + 1);
                        result.add(parseJsonObject(objStr));
                        start = i + 2; // Skip comma
                    } else if (i + 1 >= json.length()) {
                        String objStr = json.substring(start, i + 1);
                        result.add(parseJsonObject(objStr));
                    }
                }
            }
        }
        return result;
    }
    
    private Map<String, String> parseJsonObject(String json) {
        Map<String, String> result = new LinkedHashMap<>();
        // Remove outer braces
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        // Parse key-value pairs
        int pos = 0;
        while (pos < json.length()) {
            // Skip whitespace and commas
            while (pos < json.length() && (json.charAt(pos) == ' ' || json.charAt(pos) == '\n' || json.charAt(pos) == '\t' || json.charAt(pos) == ',')) {
                pos++;
            }
            if (pos >= json.length()) break;
            
            // Parse key (should be in quotes)
            if (json.charAt(pos) != '"') break;
            int keyStart = pos + 1;
            int keyEnd = json.indexOf('"', keyStart);
            if (keyEnd == -1) break;
            String key = json.substring(keyStart, keyEnd);
            pos = keyEnd + 1;
            
            // Skip to colon
            while (pos < json.length() && json.charAt(pos) != ':') pos++;
            if (pos >= json.length()) break;
            pos++; // Skip colon
            
            // Skip whitespace
            while (pos < json.length() && (json.charAt(pos) == ' ' || json.charAt(pos) == '\n' || json.charAt(pos) == '\t')) {
                pos++;
            }
            if (pos >= json.length()) break;
            
            // Parse value
            String value;
            if (json.charAt(pos) == '"') {
                // String value
                int valStart = pos + 1;
                int valEnd = json.indexOf('"', valStart);
                if (valEnd == -1) break;
                value = json.substring(valStart, valEnd);
                pos = valEnd + 1;
            } else if (json.charAt(pos) == '[') {
                // Array value - extract as string for nested parsing
                int arrayStart = pos;
                int braceCount = 0;
                while (pos < json.length()) {
                    if (json.charAt(pos) == '[') braceCount++;
                    else if (json.charAt(pos) == ']') braceCount--;
                    pos++;
                    if (braceCount == 0) break;
                }
                value = json.substring(arrayStart, pos);
            } else {
                // Number or boolean/null
                int valStart = pos;
                while (pos < json.length() && json.charAt(pos) != ',' && json.charAt(pos) != ' ' && json.charAt(pos) != '\n' && json.charAt(pos) != '\t') {
                    pos++;
                }
                value = json.substring(valStart, pos);
            }
            
            result.put(key, value);
        }
        return result;
    }
    
    private int parseInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private int getCategoryId(String categoryName) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(categoryName)) {
                return i + 1;
            }
        }
        return 1; // Default to Force
    }
    
    public List<Hero> getAllHeroes() {
        return Collections.unmodifiableList(heroes);
    }
    
    public Hero getHeroById(int id) {
        for (Hero h : heroes) {
            if (h.getId() == id) return h;
        }
        return null;
    }
    
    public List<Spell> getSpellsForHero(int heroId) {
        return heroSpells.getOrDefault(heroId, Collections.emptyList());
    }
    
    public List<Category> getAllCategories() {
        return Collections.unmodifiableList(categories);
    }
    
    public Category getCategoryById(int id) {
        if (id >= 1 && id <= categories.size()) {
            return categories.get(id - 1);
        }
        return categories.get(0);
    }
    
    public Category getCategoryByName(String name) {
        for (Category c : categories) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
