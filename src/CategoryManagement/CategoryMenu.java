package CategoryManagement;

import UI.ConsoleUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Terminal menu for managing product categories.
 */
public class CategoryMenu {

    private static final CategoryService service = new CategoryService();

    public static void run(Scanner sc) {
        int choice;
        do {
            System.out.println("\n========== CATEGORY MANAGEMENT ==========");
            System.out.println("1. Add Category");
            System.out.println("2. View All Categories");
            System.out.println("3. Update Category");
            System.out.println("4. Delete Category");
            System.out.println("5. Back");
            choice = ConsoleUtils.readInt(sc, "Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> addCategory(sc);
                    case 2 -> viewCategories();
                    case 3 -> updateCategory(sc);
                    case 4 -> deleteCategory(sc);
                    case 5 -> System.out.println("Returning to main menu...");
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database operation failed: " + e.getMessage());
            }
        } while (choice != 5);
    }

    private static void addCategory(Scanner sc) throws SQLException {
        String name = ConsoleUtils.readNonEmpty(sc, "Enter Category Name: ");
        int id = service.add(name);
        System.out.println("Category added successfully! ID: " + id);
    }

    private static void viewCategories() throws SQLException {
        List<Category> categories = service.all();
        System.out.println("\n========== CATEGORIES ==========");
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }
        for (Category c : categories) {
            System.out.printf("ID: %d | Name: %s%n", c.getCategoryId(), c.getCategoryName());
        }
    }

    private static void updateCategory(Scanner sc) throws SQLException {
        int id = ConsoleUtils.readPositiveInt(sc, "Enter Category ID: ");
        if (service.get(id).isEmpty()) {
            System.out.println("Category not found.");
            return;
        }
        String name = ConsoleUtils.readNonEmpty(sc, "Enter New Category Name: ");
        if (service.update(id, name)) {
            System.out.println("Category updated successfully!");
        } else {
            System.out.println("Category update failed.");
        }
    }

    private static void deleteCategory(Scanner sc) throws SQLException {
        int id = ConsoleUtils.readPositiveInt(sc, "Enter Category ID: ");
        if (service.get(id).isEmpty()) {
            System.out.println("Category not found.");
            return;
        }
        if (ConsoleUtils.confirm(sc, "Confirm deletion (Y/N): ")) {
            if (service.delete(id)) {
                System.out.println("Category deleted successfully!");
            } else {
                System.out.println("Category deletion failed.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}
