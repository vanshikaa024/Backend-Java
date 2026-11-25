package CategoryManagement;

import Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data access object for categories.
 */
public class CategoryDAO {

    public int add(String name) throws SQLException {
        String sql = "INSERT INTO categories(category_name) VALUES(?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, name);
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                if (!r.next()) {
                    throw new SQLException("Failed to retrieve generated category ID.");
                }
                return r.getInt(1);
            }
        }
    }

    public List<Category> findAll() throws SQLException {
        List<Category> out = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM categories ORDER BY category_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                out.add(new Category(r.getInt(1), r.getString(2)));
            }
        }
        return out;
    }

    public Optional<Category> findById(int id) throws SQLException {
        String sql = "SELECT category_id, category_name FROM categories WHERE category_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? Optional.of(new Category(r.getInt(1), r.getString(2))) : Optional.empty();
            }
        }
    }

    public boolean update(int id, String name) throws SQLException {
        String sql = "UPDATE categories SET category_name = ? WHERE category_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, name);
            p.setInt(2, id);
            return p.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            return p.executeUpdate() == 1;
        }
    }
}
