package Core.Database.dao;

import Core.Database.exception.DatabaseException;
import Core.Database.model.Hero;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HeroDAO {
    private final Connection connection;
    
    public HeroDAO() throws DatabaseException {
        try {
            this.connection = Core.Database.DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize HeroDAO", e);
        }
    }
    
    public void create(Hero hero) throws DatabaseException {
        String sql = "INSERT INTO heroes (name, history, category_id, base_hp, max_hp, attack, defense, attack_speed, max_mana, character_row, hair_row, outfit_file, suit_row) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, hero.getName());
            pstmt.setString(2, hero.getHistory());
            pstmt.setInt(3, hero.getCategoryId());
            pstmt.setInt(4, hero.getBaseHp());
            pstmt.setInt(5, hero.getMaxHp());
            pstmt.setInt(6, hero.getAttack());
            pstmt.setInt(7, hero.getDefense());
            pstmt.setDouble(8, hero.getAttackSpeed());
            pstmt.setInt(9, hero.getMaxMana());
            pstmt.setInt(10, hero.getCharacterRow());
            pstmt.setInt(11, hero.getHairRow());
            pstmt.setString(12, hero.getOutfitFile());
            if (hero.getSuitRow() != null) {
                pstmt.setInt(13, hero.getSuitRow());
            } else {
                pstmt.setNull(13, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hero.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create hero", e);
        }
    }
    
    public Hero findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM heroes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Hero hero = new Hero();
                    hero.setId(rs.getInt("id"));
                    hero.setName(rs.getString("name"));
                    hero.setHistory(rs.getString("history"));
                    hero.setCategoryId(rs.getInt("category_id"));
                    hero.setBaseHp(rs.getInt("base_hp"));
                    hero.setMaxHp(rs.getInt("max_hp"));
                    hero.setAttack(rs.getInt("attack"));
                    hero.setDefense(rs.getInt("defense"));
                    hero.setAttackSpeed(rs.getDouble("attack_speed"));
                    hero.setMaxMana(rs.getInt("max_mana"));
                    hero.setCharacterRow(rs.getInt("character_row"));
                    hero.setHairRow(rs.getInt("hair_row"));
                    hero.setOutfitFile(rs.getString("outfit_file"));
                    int suitRow = rs.getInt("suit_row");
                    if (!rs.wasNull()) {
                        hero.setSuitRow(suitRow);
                    }
                    return hero;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find hero by id", e);
        }
        return null;
    }
    
    public List<Hero> findByCategoryId(int categoryId) throws DatabaseException {
        List<Hero> heroes = new ArrayList<>();
        String sql = "SELECT * FROM heroes WHERE category_id = ? ORDER BY id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    heroes.add(mapResultSetToHero(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find heroes by category", e);
        }
        return heroes;
    }
    
    public List<Hero> findAll() throws DatabaseException {
        List<Hero> heroes = new ArrayList<>();
        String sql = "SELECT * FROM heroes ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                heroes.add(mapResultSetToHero(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all heroes", e);
        }
        return heroes;
    }
    
    public void update(Hero hero) throws DatabaseException {
        String sql = "UPDATE heroes SET name = ?, history = ?, category_id = ?, " +
                    "base_hp = ?, max_hp = ?, attack = ?, defense = ?, attack_speed = ?, max_mana = ?, " +
                    "character_row = ?, hair_row = ?, outfit_file = ?, suit_row = ? " +
                    "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, hero.getName());
            pstmt.setString(2, hero.getHistory());
            pstmt.setInt(3, hero.getCategoryId());
            pstmt.setInt(4, hero.getBaseHp());
            pstmt.setInt(5, hero.getMaxHp());
            pstmt.setInt(6, hero.getAttack());
            pstmt.setInt(7, hero.getDefense());
            pstmt.setDouble(8, hero.getAttackSpeed());
            pstmt.setInt(9, hero.getMaxMana());
            pstmt.setInt(10, hero.getCharacterRow());
            pstmt.setInt(11, hero.getHairRow());
            pstmt.setString(12, hero.getOutfitFile());
            if (hero.getSuitRow() != null) {
                pstmt.setInt(13, hero.getSuitRow());
            } else {
                pstmt.setNull(13, java.sql.Types.INTEGER);
            }
            pstmt.setInt(14, hero.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update hero", e);
        }
    }
    
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM heroes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete hero", e);
        }
    }
    
    private Hero mapResultSetToHero(ResultSet rs) throws SQLException {
        Hero hero = new Hero();
        hero.setId(rs.getInt("id"));
        hero.setName(rs.getString("name"));
        hero.setHistory(rs.getString("history"));
        hero.setCategoryId(rs.getInt("category_id"));
        hero.setBaseHp(rs.getInt("base_hp"));
        hero.setMaxHp(rs.getInt("max_hp"));
        hero.setAttack(rs.getInt("attack"));
        hero.setDefense(rs.getInt("defense"));
        hero.setAttackSpeed(rs.getDouble("attack_speed"));
        hero.setMaxMana(rs.getInt("max_mana"));
        hero.setCharacterRow(rs.getInt("character_row"));
        hero.setHairRow(rs.getInt("hair_row"));
        hero.setOutfitFile(rs.getString("outfit_file"));
        int suitRow = rs.getInt("suit_row");
        if (!rs.wasNull()) {
            hero.setSuitRow(suitRow);
        }
        return hero;
    }
}
