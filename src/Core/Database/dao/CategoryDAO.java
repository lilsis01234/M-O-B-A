package Core.Database.dao;

import Core.Database.exception.DatabaseException;
import Core.Database.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final Connection connection;
    
    public CategoryDAO() throws DatabaseException {
        try {
            this.connection = Core.Database.DatabaseManager.getInstance().getConnection();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize CategoryDAO", e);
        }
    }
    
    public void create(Category category) throws DatabaseException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, category.getName());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create category", e);
        }
    }
    
    public Category findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    return category;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find category by id", e);
        }
        return null;
    }
    
    public Category findByName(String name) throws DatabaseException {
        String sql = "SELECT * FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    return category;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find category by name", e);
        }
        return null;
    }
    
    public List<Category> findAll() throws DatabaseException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all categories", e);
        }
        return categories;
    }
    
    public void update(Category category) throws DatabaseException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setInt(2, category.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update category", e);
        }
    }
    
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete category", e);
        }
    }
}
