package Core.Database.util;

import Core.Database.DatabaseManager;
import Core.Database.dao.*;
import Core.Database.model.*;
import Core.Database.exception.DatabaseException;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class DatabaseInitializer {
    
    private static final Gson GSON = new GsonBuilder().create();
    
    public static void initialize() throws DatabaseException {
        try {
            DatabaseManager.getInstance().initializeSchema();
            initializeCategories();
            initializeHeroesFromJSON();
            System.out.println("Database initialization completed successfully.");
        } catch (DatabaseException | IOException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            throw new DatabaseException("Initialization failed", e);
        }
    }
    
    private static void initializeCategories() throws DatabaseException {
        CategoryDAO dao = new CategoryDAO();
        if (dao.findAll().isEmpty()) {
            System.out.println("Seeding categories...");
            createCategory(dao, "Force");
            createCategory(dao, "Agilité");
            createCategory(dao, "Intelligence");
        } else {
            System.out.println("Categories already exist.");
        }
    }
    
    private static void createCategory(CategoryDAO dao, String name) throws DatabaseException {
        Category c = new Category(name);
        dao.create(c);
        System.out.println("  Category: " + name);
    }
    
    private static void initializeHeroesFromJSON() throws DatabaseException, IOException {
        HeroDAO heroDAO = new HeroDAO();
        SpellDAO spellDAO = new SpellDAO();
        
        if (heroDAO.findAll().isEmpty()) {
            System.out.println("Loading heroes from JSON...");
            
            // Read and parse JSON using Gson
            String json = readJSONFile();
            JsonArray heroArray = JsonParser.parseString(json).getAsJsonArray();
            
            CategoryDAO catDAO = new CategoryDAO();
            Category force = catDAO.findByName("Force");
            Category agilite = catDAO.findByName("Agilité");
            Category intel = catDAO.findByName("Intelligence");
            
            if (force == null || agilite == null || intel == null) {
                throw new DatabaseException("Required categories not found in database");
            }
            
            for (JsonElement element : heroArray) {
                JsonObject heroObj = element.getAsJsonObject();
                String name = heroObj.get("name").getAsString();
                String history = heroObj.get("history").getAsString();
                String category = heroObj.get("category").getAsString();
                
                if (name == null || name.isEmpty()) {
                    System.err.println("Warning: Skipping hero with missing name");
                    continue;
                }
                if (category == null || category.isEmpty()) {
                    System.err.println("Warning: Hero " + name + " has missing category, skipping");
                    continue;
                }
                
                int categoryId;
                switch (category) {
                    case "Force" -> categoryId = force.getId();
                    case "Agilité" -> categoryId = agilite.getId();
                    case "Intelligence" -> categoryId = intel.getId();
                    default -> throw new DatabaseException("Unknown category: " + category);
                }
                
                Hero hero = new Hero();
                hero.setName(name);
                hero.setHistory(history);
                hero.setCategoryId(categoryId);
                hero.setBaseHp(heroObj.get("baseHp").getAsInt());
                hero.setMaxHp(heroObj.get("maxHp").getAsInt());
                hero.setAttack(heroObj.get("attack").getAsInt());
                hero.setDefense(heroObj.get("defense").getAsInt());
                hero.setAttackSpeed(heroObj.get("attackSpeed").getAsDouble());
                hero.setMaxMana(heroObj.get("maxMana").getAsInt());
                hero.setCharacterRow(heroObj.get("characterRow").getAsInt());
                hero.setHairRow(heroObj.get("hairRow").getAsInt());
                hero.setOutfitFile(heroObj.get("outfitFile").getAsString());
                
                JsonElement suitRowElem = heroObj.get("suitRow");
                if (suitRowElem != null && !suitRowElem.isJsonNull()) {
                    hero.setSuitRow(suitRowElem.getAsInt());
                } else {
                    hero.setSuitRow(null);
                }
                
                heroDAO.create(hero);
                System.out.println("  Created hero: " + hero.getName() + " (ID:" + hero.getId() + 
                                 ", Row:" + hero.getCharacterRow() + ", Hair:" + hero.getHairRow() + 
                                 ", Outfit:" + hero.getOutfitFile() + ")");
                
                // Create spells
                JsonArray spellsArray = heroObj.getAsJsonArray("spells");
                for (JsonElement spellElem : spellsArray) {
                    JsonObject spellObj = spellElem.getAsJsonObject();
                    Spell spell = new Spell();
                    spell.setHeroId(hero.getId());
                    spell.setName(spellObj.get("name").getAsString());
                    spell.setDescription(spellObj.get("description").getAsString());
                    spell.setType(spellObj.get("type").getAsString());
                    spell.setDamage(spellObj.get("damage").getAsInt());
                    spell.setCooldown(spellObj.get("cooldown").getAsDouble());
                    spell.setManaCost(spellObj.get("manaCost").getAsInt());
                    spellDAO.create(spell);
                    System.out.println("    Spell: " + spell.getName() + " (dmg:" + spell.getDamage() + 
                                     ", cd:" + spell.getCooldown() + "s, mana:" + spell.getManaCost() + ")");
                }
            }
            
            System.out.println("Total heroes loaded: " + heroDAO.findAll().size());
        } else {
            System.out.println("Heroes already exist in database.");
        }
    }
    
    private static String readJSONFile() throws IOException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/Data/heroes.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line.trim());
            }
        }
        return json.toString();
    }
}
