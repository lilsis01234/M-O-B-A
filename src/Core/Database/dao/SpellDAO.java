package Core.Database.dao;

import Core.Database.exception.DatabaseException;
import Core.Database.model.Spell;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpellDAO {
    private final Connection connection;
    
    public SpellDAO() throws DatabaseException {
        try {
            this.connection = Core.Database.DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize SpellDAO", e);
        }
    }
    
    public void create(Spell spell) throws DatabaseException {
        String sql = "INSERT INTO spells (hero_id, name, description, type, base_damage, cooldown, mana_cost, level) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, spell.getHeroId());
            pstmt.setString(2, spell.getName());
            pstmt.setString(3, spell.getDescription());
            pstmt.setString(4, spell.getType());
            pstmt.setInt(5, spell.getDamage()); // maps to base_damage
            pstmt.setDouble(6, spell.getCooldown());
            pstmt.setInt(7, spell.getManaCost());
            pstmt.setInt(8, 1); // default level
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    spell.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create spell", e);
        }
    }
    
    public Spell findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM spells WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSpell(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find spell by id", e);
        }
        return null;
    }
    
    public List<Spell> findByHeroId(int heroId) throws DatabaseException {
        List<Spell> spells = new ArrayList<>();
        String sql = "SELECT * FROM spells WHERE hero_id = ? ORDER BY id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, heroId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    spells.add(mapResultSetToSpell(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find spells by hero id", e);
        }
        return spells;
    }
    
    public List<Spell> findAll() throws DatabaseException {
        List<Spell> spells = new ArrayList<>();
        String sql = "SELECT * FROM spells ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                spells.add(mapResultSetToSpell(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all spells", e);
        }
        return spells;
    }
    
    public void update(Spell spell) throws DatabaseException {
        String sql = "UPDATE spells SET hero_id = ?, name = ?, description = ?, " +
                    "type = ?, base_damage = ?, cooldown = ?, mana_cost = ?, level = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, spell.getHeroId());
            pstmt.setString(2, spell.getName());
            pstmt.setString(3, spell.getDescription());
            pstmt.setString(4, spell.getType());
            pstmt.setInt(5, spell.getDamage());
            pstmt.setDouble(6, spell.getCooldown());
            pstmt.setInt(7, spell.getManaCost());
            pstmt.setInt(8, 1); // default level, could be from spell.getLevel() if exists
            pstmt.setInt(9, spell.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update spell", e);
        }
    }
    
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM spells WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete spell", e);
        }
    }
    
    private Spell mapResultSetToSpell(ResultSet rs) throws SQLException {
        Spell spell = new Spell();
        spell.setId(rs.getInt("id"));
        spell.setHeroId(rs.getInt("hero_id"));
        spell.setName(rs.getString("name"));
        spell.setDescription(rs.getString("description"));
        spell.setDamage(rs.getInt("base_damage"));
        spell.setCooldown(rs.getDouble("cooldown"));
        spell.setManaCost(rs.getInt("mana_cost"));
        spell.setType(rs.getString("type"));
        return spell;
    }
}
