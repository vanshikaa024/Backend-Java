package SupplierManagement;

import Database.DBConnection;
import UI.ConsoleUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Terminal menu for supplier CRUD operations.
 */
public class SupplierMenu {

    private static final SupplierService service = new SupplierService();

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("\nDatabase Connected: " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            return;
        }
        try (Scanner sc = new Scanner(System.in)) {
            run(sc);
        }
    }

    public static void run(Scanner sc) {
        int choice;
        do {
            System.out.println("\n========== SUPPLIER MANAGEMENT ==========");
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Search Supplier");
            System.out.println("4. Update Supplier");
            System.out.println("5. Delete Supplier");
            System.out.println("6. Back");
            choice = ConsoleUtils.readInt(sc, "Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> addSupplier(sc);
                    case 2 -> viewSuppliers();
                    case 3 -> searchSupplier(sc);
                    case 4 -> updateSupplier(sc);
                    case 5 -> deleteSupplier(sc);
                    case 6 -> System.out.println("Returning to main menu...");
                    default -> System.out.println("Invalid choice.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Validation Error: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Database operation failed: " + e.getMessage());
            }
        } while (choice != 6);
    }

    private static String readName(Scanner sc) {
        while (true) {
            System.out.print("Enter Supplier Name: ");
            String name = sc.nextLine().trim();
            if (name.matches("[A-Za-z][A-Za-z .'-]*")) {
                return name;
            }
            System.out.println("Invalid name! Use letters and spaces only.");
        }
    }

    private static String readPhone(Scanner sc) {
        while (true) {
            System.out.print("Enter Phone (10 digits): ");
            String phone = sc.nextLine().trim();
            if (phone.matches("[0-9]{10}")) {
                return phone;
            }
            System.out.println("Invalid phone! Phone must contain exactly 10 digits.");
        }
    }

    private static String readEmail(Scanner sc) {
        while (true) {
            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();
            if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }
            System.out.println("Invalid email! Enter a valid email address.");
        }
    }

    private static void addSupplier(Scanner sc) throws SQLException {
        String name = readName(sc);
        String phone = readPhone(sc);
        String email = readEmail(sc);
        String address = ConsoleUtils.readNonEmpty(sc, "Enter Address: ");

        Supplier supplier = new Supplier(name, phone, email, address);
        if (service.addSupplier(supplier)) {
            System.out.println("\nSupplier added successfully! Generated Supplier ID: " + supplier.getSupplierId());
        } else {
            System.out.println("Supplier could not be added.");
        }
    }

    private static void viewSuppliers() throws SQLException {
        List<Supplier> suppliers = service.getAllSuppliers();
        System.out.println("\n========== SUPPLIER RECORDS ==========");
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found.");
            return;
        }
        for (Supplier s : suppliers) {
            displaySupplier(s);
        }
        System.out.println("\nTotal Suppliers: " + suppliers.size());
    }

    private static void searchSupplier(Scanner sc) throws SQLException {
        String keyword = ConsoleUtils.readNonEmpty(sc, "Enter Supplier Name or Phone: ");
        List<Supplier> suppliers = service.searchSupplier(keyword);
        if (suppliers.isEmpty()) {
            System.out.println("No matching supplier found.");
            return;
        }
        for (Supplier s : suppliers) {
            displaySupplier(s);
        }
    }

    private static void updateSupplier(Scanner sc) throws SQLException {
        int id = ConsoleUtils.readPositiveInt(sc, "Enter Supplier ID: ");
        Supplier existing = service.getSupplierById(id);
        if (existing == null) {
            System.out.println("Supplier not found.");
            return;
        }

        System.out.println("Enter new details:");
        String name = readName(sc);
        String phone = readPhone(sc);
        String email = readEmail(sc);
        String address = ConsoleUtils.readNonEmpty(sc, "Enter Address: ");

        Supplier supplier = new Supplier(id, name, phone, email, address);
        if (service.updateSupplier(supplier)) {
            System.out.println("Supplier updated successfully!");
        } else {
            System.out.println("Supplier update failed.");
        }
    }

    private static void deleteSupplier(Scanner sc) throws SQLException {
        int id = ConsoleUtils.readPositiveInt(sc, "Enter Supplier ID: ");
        Supplier existing = service.getSupplierById(id);
        if (existing == null) {
            System.out.println("Supplier not found.");
            return;
        }
        System.out.println("\nSupplier to delete:");
        displaySupplier(existing);
        if (ConsoleUtils.confirm(sc, "\nConfirm deletion (Y/N): ")) {
            if (service.deleteSupplier(id)) {
                System.out.println("Supplier deleted successfully!");
            } else {
                System.out.println("Supplier deletion failed.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void displaySupplier(Supplier s) {
        System.out.println("--------------------------------");
        System.out.println("Supplier ID: " + s.getSupplierId());
        System.out.println("Name:        " + s.getSupplierName());
        System.out.println("Phone:       " + s.getPhone());
        System.out.println("Email:       " + s.getEmail());
        System.out.println("Address:     " + s.getAddress());
    }
}
