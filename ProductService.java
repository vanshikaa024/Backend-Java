import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ProductService {
    private final ProductDAO products=new ProductDAO(); private final CategoryDAO categories=new CategoryDAO();
    public int add(String name,int categoryId,BigDecimal price,BigDecimal cost,int stock,int minStock,LocalDate expiry,String condition)throws SQLException{return products.add(validated(0,name,categoryId,price,cost,stock,minStock,expiry,condition));}
    public List<Product> search(String query)throws SQLException{return products.search(query);}
    public Optional<Product> get(int id)throws SQLException{CategoryService.requireId(id);return products.find(id);}
    public boolean update(int id,String name,int categoryId,BigDecimal price,BigDecimal cost,int stock,int minStock,LocalDate expiry,String condition)throws SQLException{CategoryService.requireId(id);return products.update(id,validated(id,name,categoryId,price,cost,stock,minStock,expiry,condition));}
    public boolean delete(int id)throws SQLException{CategoryService.requireId(id);return products.delete(id);}
    private Product validated(int id,String name,int category,BigDecimal price,BigDecimal cost,int stock,int minStock,LocalDate expiry,String condition)throws SQLException{
        if(name==null||name.isBlank()||name.trim().length()>100)throw new IllegalArgumentException("Product name is required (maximum 100 characters).");
        if(categories.findById(category).isEmpty())throw new IllegalArgumentException("Select an existing category.");
        if(price==null||price.signum()<0||price.scale()>2||price.precision()>10)throw new IllegalArgumentException("Selling price must be nonnegative with at most 2 decimals.");
        if(cost==null||cost.signum()<0||cost.scale()>2||cost.precision()>10)throw new IllegalArgumentException("Cost price must be nonnegative with at most 2 decimals.");
        if(stock<0||minStock<0)throw new IllegalArgumentException("Stock and minimum stock must be nonnegative.");
        if(condition==null||condition.isBlank()||condition.length()>30)throw new IllegalArgumentException("Product condition is required (maximum 30 characters).");
        return new Product(id,name.trim(),category,null,price,cost,stock,minStock,expiry,condition.trim());
    }
}

