package Core.Database.util;

import Core.Database.DatabaseManager;
import Core.Database.dao.*;
import Core.Database.model.*;
import Core.Database.exception.DatabaseException;
import java.io.IOException;
import java.util.List;

public class DatabaseInitializer {
    
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
            
            List<HeroJSONLoader.HeroData> heroDataList = HeroJSONLoader.loadHeroesFromJSON();
            CategoryDAO catDAO = new CategoryDAO();
            
            // Get all categories
            Category force = catDAO.findByName("Force");
            Category agilite = catDAO.findByName("Agilité");
            Category intel = catDAO.findByName("Intelligence");
            
            if (force == null || agilite == null || intel == null) {
                throw new DatabaseException("Required categories not found in database");
            }
            
            for (HeroJSONLoader.HeroData data : heroDataList) {
                // Validate required fields
                if (data.name == null || data.name.trim().isEmpty()) {
                    System.err.println("Warning: Skipping hero with missing name");
                    continue;
                }
                if (data.category == null || data.category.trim().isEmpty()) {
                    System.err.println("Warning: Hero " + data.name + " has missing category, skipping");
                    continue;
                }
                
                // Determine which category ID to use
                int categoryId;
                switch (data.category) {
                    case "Force": categoryId = force.getId(); break;
                    case "Agilité": categoryId = agilite.getId(); break;
                    case "Intelligence": categoryId = intel.getId(); break;
                    default: throw new DatabaseException("Unknown category: " + data.category);
                }
                
                Hero hero = HeroJSONLoader.convertToDatabaseHero(data, 
                    new Core.Database.model.Category(categoryId, data.category));
                heroDAO.create(hero);
                System.out.println("  Created hero: " + hero.getName() + " (ID:" + hero.getId() + 
                                 ", Row:" + hero.getCharacterRow() + ", Hair:" + hero.getHairRow() + 
                                 ", Outfit:" + hero.getOutfitFile() + ")");
                
                // Create spells
                for (HeroJSONLoader.SpellData spellData : data.spells) {
                    Core.Database.model.Spell spell = HeroJSONLoader.convertToSpell(spellData, hero.getId());
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
}
