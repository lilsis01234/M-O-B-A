package Core.Database;

import Core.Database.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:game_data.db";
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to database", e);
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {
            // Categories table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL UNIQUE
                )
            """);
            
            // Heroes table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS heroes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    history TEXT,
                    category_id INTEGER NOT NULL,
                    base_hp INTEGER NOT NULL,
                    max_hp INTEGER NOT NULL,
                    attack INTEGER NOT NULL,
                    defense INTEGER NOT NULL,
                    attack_speed REAL NOT NULL,
                    max_mana INTEGER NOT NULL,
                    character_row INTEGER NOT NULL,
                    hair_row INTEGER NOT NULL,
                    outfit_file TEXT NOT NULL,
                    suit_row INTEGER,
                    FOREIGN KEY (category_id) REFERENCES categories(id)
                )
            """);
            
            // Spells table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS spells (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    hero_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    type TEXT NOT NULL, -- 'dmg', 'CC', 'SP'
                    base_damage INTEGER,
                    cooldown INTEGER NOT NULL,
                    mana_cost INTEGER NOT NULL,
                    level INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY (hero_id) REFERENCES heroes(id) ON DELETE CASCADE
                )
            """);
            
            // Players table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_games INTEGER DEFAULT 0,
                    wins INTEGER DEFAULT 0
                )
            """);
            
            // Player Heroes table (tracks selected hero and progress)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player_heroes (
                    player_id INTEGER NOT NULL,
                    hero_id INTEGER NOT NULL,
                    level INTEGER NOT NULL DEFAULT 1,
                    experience INTEGER NOT NULL DEFAULT 0,
                    current_hp INTEGER NOT NULL,
                    current_mp INTEGER NOT NULL,
                    spell1_level INTEGER NOT NULL DEFAULT 1,
                    spell2_level INTEGER NOT NULL DEFAULT 1,
                    spell3_level INTEGER NOT NULL DEFAULT 1,
                    PRIMARY KEY (player_id, hero_id),
                    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
                    FOREIGN KEY (hero_id) REFERENCES heroes(id) ON DELETE CASCADE
                )
            """);
            
            // Create indexes for performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_heroes_category ON heroes(category_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_spells_hero ON spells(hero_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_player_heroes_player ON player_heroes(player_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_player_heroes_hero ON player_heroes(hero_id)");
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize schema", e);
        }
    }
}
