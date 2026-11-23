
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import Database.DBConnection;
import SupplierManagement.Supplier;
import SupplierManagement.SupplierDAO;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static SupplierDAO dao = new SupplierDAO();

    public static void main(String[] args) {

        if (!checkDatabaseConnection()) {
            return;
        }

        int choice;

        do {
            System.out.println("\n========== SMARTSHELF ==========");
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Search Supplier");
            System.out.println("4. Update Supplier");
            System.out.println("5. Delete Supplier");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        addSupplier();
                        break;

                    case 2:
                        viewSuppliers();
                        break;

                    case 3:
                        searchSupplier();
                        break;

                    case 4:
                        updateSupplier();
                        break;

                    case 5:
                        deleteSupplier();
                        break;

                    case 6:
                        System.out.println("Exiting SmartShelf...");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = 0;
            } catch (SQLException e) {
                System.out.println("Database operation failed: " + e.getMessage());
                choice = 0;
            }

        } while (choice != 6);

        sc.close();
    }

    private static boolean checkDatabaseConnection() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("\nDatabase Connected!");
            System.out.println("Connected Database: " + con.getCatalog());

            return true;

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

            return false;
        }
    }

    private static String readName() {

        while (true) {
            System.out.print("Enter Supplier Name: ");
            String name = sc.nextLine().trim();

            if (name.matches("[A-Za-z][A-Za-z .'-]*")) {
                return name;
            }

            System.out.println("Invalid name! Use letters and spaces only.");
        }
    }

    private static String readPhone() {

        while (true) {
            System.out.print("Enter Phone (10 digits): ");
            String phone = sc.nextLine().trim();

            if (phone.matches("[0-9]{10}")) {
                return phone;
            }

            System.out.println("Invalid phone! Phone must contain exactly 10 digits.");
        }
    }

    private static String readEmail() {

        while (true) {
            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();

            if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }

            System.out.println("Invalid email! Enter a valid email address.");
        }
    }

    private static String readAddress() {

        while (true) {
            System.out.print("Enter Address: ");
            String address = sc.nextLine().trim();

            if (!address.isEmpty()) {
                return address;
            }

            System.out.println("Address cannot be empty.");
        }
    }

    private static int readId() {

        while (true) {
            System.out.print("Enter Supplier ID: ");
            String input = sc.nextLine().trim();

            try {
                int id = Integer.parseInt(input);

                if (id > 0) {
                    return id;
                }

                System.out.println("Supplier ID must be positive.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric ID.");
            }
        }
    }

    private static void addSupplier() throws SQLException {

        String name = readName();
        String phone = readPhone();
        String email = readEmail();
        String address = readAddress();

        Supplier supplier = new Supplier(name, phone, email, address);

        if (dao.addSupplier(supplier)) {
            System.out.println("\nSupplier added successfully!");
            System.out.println("Generated Supplier ID: " + supplier.getSupplierId());
        } else {
            System.out.println("Supplier already exists or could not be added.");
        }
    }

    private static void viewSuppliers() throws SQLException {

        List<Supplier> suppliers = dao.getAllSuppliers();

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

    private static void searchSupplier() throws SQLException {

        System.out.print("Enter Supplier Name or Phone: ");
        String keyword = sc.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }

        List<Supplier> suppliers = dao.searchSupplier(keyword);

        if (suppliers.isEmpty()) {
            System.out.println("No matching supplier found.");
            return;
        }

        for (Supplier s : suppliers) {
            displaySupplier(s);
        }
    }

    private static void updateSupplier() throws SQLException {

        int id = readId();

        Supplier existing = dao.getSupplierById(id);

        if (existing == null) {
            System.out.println("Supplier not found.");
            return;
        }

        System.out.println("Enter new details:");

        String name = readName();
        String phone = readPhone();
        String email = readEmail();
        String address = readAddress();

        Supplier supplier = new Supplier(id, name, phone, email, address);

        if (dao.updateSupplier(supplier)) {
            System.out.println("Supplier updated successfully!");
        } else {
            System.out.println("Supplier update failed.");
        }
    }

    private static void deleteSupplier() throws SQLException {

        int id = readId();

        Supplier existing = dao.getSupplierById(id);

        if (existing == null) {
            System.out.println("Supplier not found.");
            return;
        }

        System.out.println("\nSupplier to delete:");
        displaySupplier(existing);

        System.out.print("\nConfirm deletion (Y/N): ");
        String confirmation = sc.nextLine().trim();

        if (confirmation.equalsIgnoreCase("Y")) {

            if (dao.deleteSupplier(id)) {
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
        System.out.println("Name: " + s.getSupplierName());
        System.out.println("Phone: " + s.getPhone());
        System.out.println("Email: " + s.getEmail());
        System.out.println("Address: " + s.getAddress());
    }
}
