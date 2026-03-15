package Core.Database;

import Core.Database.model.*;
import java.io.*;
import java.util.*;

/**
 * Fournisseur de données à partir de fichiers JSON.
 * @author RAHARIMANANA Tianantenaina 
 */
public class JsonDataProvider {
    
    private static final String HEROES_FILE = "src/Data/heroes.json";
    
    private List<Hero> heroes;
    private List<Category> categories;
    private Map<Integer, List<Spell>> heroSpells;
    
    /**
     * Initialise le fournisseur et charge les données.
     * @throws IOException si le fichier JSON est introuvable ou illisible.
     */
    public JsonDataProvider() throws IOException {
        loadData();
    }
    
    /**
     * Initialise les catégories par défaut et lance le processus de lecture du fichier JSON.
     * @throws IOException En cas d'erreur d'accès au fichier.
     */
    private void loadData() throws IOException {

        categories = new ArrayList<>();
        categories.add(new Category("Force"));
        categories.add(new Category("Agilité"));
        categories.add(new Category("Intelligence"));
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
    
    /**
     * créer les objets Hero et leurs sorts associés.
     * @param tableau d'objets héros.
     */
    private void parseHeroes(String json) {
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
    
    /**
     * Découpe une chaîne de tableau JSON en une liste d'objets.
     * Gère l'imbrication des accolades pour ne pas couper au mauvais endroit.
     * @param json Chaîne commençant par '[' et finissant par ']'.
     * @return Une liste de maps, où chaque map contient les paires clé-valeur d'un objet.
     */
    private List<Map<String, String>> parseJsonArray(String json) {
        List<Map<String, String>> result = new ArrayList<>();
        // enlever les crochets
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
        
        // separe l'objet quand on rencontre un {
        int braceCount = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // objet complet trouvé
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
    
    /**
     * Analyse un objet JSON individuel.
     * @param json  objet JSON "{...}".
     * @return Une map contenant les paires clé-valeur 
     */
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
    
    /**
     * Convertit une chaîne en entier de 
     * @param s La chaîne à convertir.
     * @return L'entier converti ou 0 si la chaîne est invalide.
     */
    private int parseInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Convertit une chaîne en double 
     * @param s La chaîne à convertir.
     * @return Le double converti ou 0.0 si la chaîne est invalide.
     */
    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Trouve l'ID d'une catégorie à partir de son nom.
     * @param categoryName Nom de la catégorie
     * @return L'index de la catégorie ou 1 par défaut.
     */
    private int getCategoryId(String categoryName) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(categoryName)) {
                return i + 1;
            }
        }
        return 1;
    }
    
    /** @return Une liste non modifiable de tous les héros chargés. */
    public List<Hero> getAllHeroes() {
        return Collections.unmodifiableList(heroes);
    }
    
    /** * @param id L'identifiant du héros.
     * @return Le héros correspondant ou null si non trouvé.
     */
    public Hero getHeroById(int id) {
        for (Hero h : heroes) {
            if (h.getId() == id) return h;
        }
        return null;
    }
    
    /** * @param heroId L'identifiant du héros.
     * @return La liste des sorts associés à ce héros.
     */
    public List<Spell> getSpellsForHero(int heroId) {
        return heroSpells.getOrDefault(heroId, Collections.emptyList());
    }
    
    /** @return La liste non modifiable de toutes les catégories disponibles. */
    public List<Category> getAllCategories() {
        return Collections.unmodifiableList(categories);
    }
    
    /** * @param id L'identifiant de la catégorie.
     * @return La catégorie correspondante ou la première par défaut.
     */
    public Category getCategoryById(int id) {
        if (id >= 1 && id <= categories.size()) {
            return categories.get(id - 1);
        }
        return categories.get(0);
    }
    
    /** * @param name Le nom de la catégorie recherchée.
     * @return La catégorie correspondante ou null.
     */
    public Category getCategoryByName(String name) {
        for (Category c : categories) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
}
