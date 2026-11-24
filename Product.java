import java.math.BigDecimal;
import java.time.LocalDate;

public class Product {
    private final int productId;
    private final String productName;
    private final int categoryId;
    private final String categoryName;
    private final BigDecimal price, costPrice;
    private final int stock, minStock;
    private final LocalDate expiryDate;
    private final String productCondition;

    public Product(int productId, String productName, int categoryId, String categoryName,
                   BigDecimal price, BigDecimal costPrice, int stock, int minStock,
                   LocalDate expiryDate, String productCondition) {
        this.productId=productId; this.productName=productName; this.categoryId=categoryId;
        this.categoryName=categoryName; this.price=price; this.costPrice=costPrice;
        this.stock=stock; this.minStock=minStock; this.expiryDate=expiryDate;
        this.productCondition=productCondition;
    }
    public int getProductId(){return productId;} public String getProductName(){return productName;}
    public int getCategoryId(){return categoryId;} public String getCategoryName(){return categoryName;}
    public BigDecimal getPrice(){return price;} public BigDecimal getCostPrice(){return costPrice;}
    public int getStock(){return stock;} public int getMinStock(){return minStock;}
    public LocalDate getExpiryDate(){return expiryDate;} public String getProductCondition(){return productCondition;}
}
