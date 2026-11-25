
package SupplierManagement;

import java.sql.SQLException;
import java.util.List;

public class SupplierService {

    private final SupplierDAO dao;

    public SupplierService() {
        this.dao = new SupplierDAO();
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {

        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }

        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        if (supplier.getPhone() == null || !supplier.getPhone().matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits");
        }

        if (supplier.getEmail() == null ||
            !supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        if (supplier.getAddress() == null || supplier.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        return dao.addSupplier(supplier);
    }

    public Supplier getSupplierById(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID");
        }

        return dao.getSupplierById(id);
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        return dao.getAllSuppliers();
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {

        if (supplier == null || supplier.getSupplierId() == null ||
            supplier.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Invalid supplier details");
        }

        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        if (supplier.getPhone() == null || !supplier.getPhone().matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits");
        }

        if (supplier.getEmail() == null ||
            !supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        if (supplier.getAddress() == null || supplier.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        return dao.updateSupplier(supplier);
    }

    public boolean deleteSupplier(int id) throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid supplier ID");
        }

        return dao.deleteSupplier(id);
    }

    public List<Supplier> searchSupplier(String keyword) throws SQLException {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        return dao.searchSupplier(keyword.trim());
    }
}
