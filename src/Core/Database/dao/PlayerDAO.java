package Core.Database.dao;

import Core.Database.exception.DatabaseException;
import Core.Database.model.Player;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class PlayerDAO {
    private final Connection connection;
    
    public PlayerDAO() throws DatabaseException {
        try {
            this.connection = Core.Database.DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize PlayerDAO", e);
        }
    }
    
    public void create(Player player) throws DatabaseException {
        String sql = "INSERT INTO players (username, created_at) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, player.getUsername());
            String createdAt = player.getCreatedAt() != null ? 
                player.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(2, createdAt);
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    player.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create player", e);
        }
    }
    
    public Player findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find player by id", e);
        }
        return null;
    }
    
    public Player findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM players WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find player by username", e);
        }
        return null;
    }
    
    public List<Player> findAll() throws DatabaseException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all players", e);
        }
        return players;
    }
    
    public void update(Player player) throws DatabaseException {
        String sql = "UPDATE players SET username = ?, created_at = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, player.getUsername());
            String createdAt = player.getCreatedAt() != null ? 
                player.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(2, createdAt);
            pstmt.setInt(3, player.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update player", e);
        }
    }
    
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM players WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete player", e);
        }
    }
    
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getInt("id"));
        player.setUsername(rs.getString("username"));
        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            player.setCreatedAt(LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return player;
    }
}
