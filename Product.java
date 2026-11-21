public class Product {

    private int productId;
    private String productName;
    private int categoryId;
    private double price;
    private double costPrice;
    private int stock;

    public Product(String productName, int categoryId,
                   double price, double costPrice, int stock) {

        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
    }

    public String getProductName() {
        return productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public double getPrice() {
        return price;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public int getStock() {
        return stock;
    }
}
