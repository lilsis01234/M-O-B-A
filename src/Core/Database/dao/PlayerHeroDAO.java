package Core.Database.dao;

import Core.Database.exception.DatabaseException;
import Core.Database.model.PlayerHero;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerHeroDAO {
    private final Connection connection;
    
    public PlayerHeroDAO() throws DatabaseException {
        try {
            this.connection = Core.Database.DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize PlayerHeroDAO", e);
        }
    }
    
    public void create(PlayerHero playerHero) throws DatabaseException {
        String sql = "INSERT INTO player_heroes (player_id, hero_id, level, experience, " +
                    "spell1_level, spell2_level, spell3_level) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPlayerHeroParameters(pstmt, playerHero);
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    playerHero.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create player-hero record", e);
        }
    }
    
    public PlayerHero findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM player_heroes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayerHero(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find player-hero by id", e);
        }
        return null;
    }
    
    public PlayerHero findByPlayerAndHero(int playerId, int heroId) throws DatabaseException {
        String sql = "SELECT * FROM player_heroes WHERE player_id = ? AND hero_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, heroId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayerHero(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find player-hero by player and hero", e);
        }
        return null;
    }
    
    public List<PlayerHero> findByPlayerId(int playerId) throws DatabaseException {
        List<PlayerHero> playerHeroes = new ArrayList<>();
        String sql = "SELECT * FROM player_heroes WHERE player_id = ? ORDER BY id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, playerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playerHeroes.add(mapResultSetToPlayerHero(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find player-heroes by player id", e);
        }
        return playerHeroes;
    }
    
    public List<PlayerHero> findAll() throws DatabaseException {
        List<PlayerHero> playerHeroes = new ArrayList<>();
        String sql = "SELECT * FROM player_heroes ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                playerHeroes.add(mapResultSetToPlayerHero(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all player-heroes", e);
        }
        return playerHeroes;
    }
    
    public void update(PlayerHero playerHero) throws DatabaseException {
        String sql = "UPDATE player_heroes SET player_id = ?, hero_id = ?, level = ?, " +
                    "experience = ?, spell1_level = ?, spell2_level = ?, spell3_level = ? " +
                    "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setPlayerHeroParameters(pstmt, playerHero);
            pstmt.setInt(8, playerHero.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update player-hero", e);
        }
    }
    
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM player_heroes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete player-hero", e);
        }
    }
    
    private void setPlayerHeroParameters(PreparedStatement pstmt, PlayerHero playerHero) throws SQLException {
        pstmt.setInt(1, playerHero.getPlayerId());
        pstmt.setInt(2, playerHero.getHeroId());
        pstmt.setInt(3, playerHero.getLevel());
        pstmt.setInt(4, playerHero.getExperience());
        pstmt.setInt(5, playerHero.getSpell1Level());
        pstmt.setInt(6, playerHero.getSpell2Level());
        pstmt.setInt(7, playerHero.getSpell3Level());
    }
    
    private PlayerHero mapResultSetToPlayerHero(ResultSet rs) throws SQLException {
        PlayerHero playerHero = new PlayerHero();
        playerHero.setId(rs.getInt("id"));
        playerHero.setPlayerId(rs.getInt("player_id"));
        playerHero.setHeroId(rs.getInt("hero_id"));
        playerHero.setLevel(rs.getInt("level"));
        playerHero.setExperience(rs.getInt("experience"));
        playerHero.setSpell1Level(rs.getInt("spell1_level"));
        playerHero.setSpell2Level(rs.getInt("spell2_level"));
        playerHero.setSpell3Level(rs.getInt("spell3_level"));
        return playerHero;
    }
}
