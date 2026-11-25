import Analytics.AnalyticsMenu;
import CategoryManagement.CategoryMenu;
import Database.DBConnection;
import InventoryManagement.InventoryMenu;
import ProductManagement.ProductMenu;
import PurchaseManagement.PurchaseMenu;
import SalesManagement.SaleMenu;
import SupplierManagement.SupplierMenu;
import UI.ConsoleUtils;
import UserManagement.User;
import UserManagement.UserMenu;
import UserManagement.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Entry point for the SmartShelf terminal-based Java backend application.
 */
public class Main {

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Connected to SmartShelf database: " + con.getCatalog());
        } catch (SQLException e) {
            System.err.println("Could not connect to SmartShelf MySQL database: " + e.getMessage());
            System.err.println("Verify MySQL is running and SMARTSHELF_DB_URL / USER / PASSWORD settings are valid.");
            return;
        }

        UserService userService = new UserService();

        try (Scanner sc = new Scanner(System.in)) {
            if (!userService.hasAnyUsers()) {
                bootstrapFirstAdmin(sc, userService);
            }

            while (true) {
                Optional<User> currentUser = promptLogin(sc, userService);
                if (currentUser.isEmpty()) {
                    System.out.println("Exiting SmartShelf. Goodbye!");
                    return;
                }
                boolean logout = runMainMenu(sc, currentUser.get());
                if (!logout) {
                    System.out.println("Exiting SmartShelf. Goodbye!");
                    return;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void bootstrapFirstAdmin(Scanner sc, UserService userService) throws SQLException {
        System.out.println("\n========== FIRST-TIME SETUP ==========");
        System.out.println("No users exist in the database. Create the initial ADMIN account.");
        while (true) {
            try {
                String username = ConsoleUtils.readNonEmpty(sc, "New Admin Username: ");
                String password = ConsoleUtils.readNonEmpty(sc, "New Admin Password (min 8 chars): ");
                userService.createUser(username, password, "ADMIN");
                System.out.println("Admin account '" + username + "' created. Please log in.");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Setup Error: " + e.getMessage());
            }
        }
    }

    private static Optional<User> promptLogin(Scanner sc, UserService userService) throws SQLException {
        System.out.println("\n========== SMARTSHELF LOGIN ==========");
        while (true) {
            System.out.print("Username (or 'exit' to quit): ");
            String username = sc.nextLine().trim();
            if (username.equalsIgnoreCase("exit")) {
                return Optional.empty();
            }
            System.out.print("Password: ");
            String password = sc.nextLine();
            Optional<User> user = userService.login(username, password);
            if (user.isPresent()) {
                System.out.println("\nWelcome, " + user.get().getUsername() + " (" + user.get().getRole() + ")");
                return user;
            }
            System.out.println("Invalid credentials or inactive account. Try again.");
        }
    }

    private static boolean runMainMenu(Scanner sc, User user) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        int choice;
        do {
            System.out.println("\n========== SMARTSHELF BACKEND MENU ==========");
            System.out.println("User: " + user.getUsername() + " | Role: " + user.getRole());
            System.out.println("1. Category Management");
            System.out.println("2. Product Management");
            System.out.println("3. Supplier Management");
            System.out.println("4. Purchase Management");
            System.out.println("5. Sales Management");
            System.out.println("6. Inventory & Notifications");
            System.out.println("7. Analytics & Dashboard");
            if (isAdmin) {
                System.out.println("8. User Management (Admin)");
            }
            System.out.println("9. Logout");
            System.out.println("0. Exit");

            choice = ConsoleUtils.readInt(sc, "Enter your choice: ");
            switch (choice) {
                case 1 -> CategoryMenu.run(sc);
                case 2 -> ProductMenu.run(sc);
                case 3 -> SupplierMenu.run(sc);
                case 4 -> PurchaseMenu.run(sc);
                case 5 -> SaleMenu.run(sc);
                case 6 -> InventoryMenu.run(sc);
                case 7 -> AnalyticsMenu.run(sc);
                case 8 -> {
                    if (isAdmin) {
                        UserMenu.run(sc);
                    } else {
                        System.out.println("Access denied. ADMIN role required.");
                    }
                }
                case 9 -> {
                    System.out.println("Logging out...");
                    return true;
                }
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Invalid choice.");
            }
        } while (true);
    }
}
