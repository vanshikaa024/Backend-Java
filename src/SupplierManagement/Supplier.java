package SupplierManagement;


public class Supplier {

    private Integer supplierId;
    private String supplierName;
    private String phone;
    private String email;
    private String address;

    public Supplier(Integer supplierId, String supplierName,
                    String phone, String email, String address) {

        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Supplier(String supplierName, String phone,
                    String email, String address) {

        this(null, supplierName, phone, email, address);
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
