package CategoryManagement;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Business service and validation for product categories.
 */
public class CategoryService {

    private final CategoryDAO dao = new CategoryDAO();

    public int add(String name) throws SQLException {
        return dao.add(validate(name));
    }

    public List<Category> all() throws SQLException {
        return dao.findAll();
    }

    public Optional<Category> get(int id) throws SQLException {
        requireId(id);
        return dao.findById(id);
    }

    public boolean update(int id, String name) throws SQLException {
        requireId(id);
        return dao.update(id, validate(name));
    }

    public boolean delete(int id) throws SQLException {
        requireId(id);
        return dao.delete(id);
    }

    public static String validate(String name) {
        if (name == null || name.isBlank() || name.trim().length() > 100) {
            throw new IllegalArgumentException("Category name is required (maximum 100 characters).");
        }
        return name.trim();
    }

    public static void requireId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive.");
        }
    }
}
