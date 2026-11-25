import java.sql.*;
import java.util.*;

import Database.DBConnection;

import java.math.BigDecimal;

public class ProductDAO {
    private static final String SELECT="SELECT p.product_id,p.product_name,p.category_id,c.category_name,p.price,p.cost_price,p.stock,p.min_stock,p.expiry_date,p.product_condition FROM products p JOIN categories c ON c.category_id=p.category_id";
    private Product map(ResultSet r)throws SQLException{return new Product(r.getInt("product_id"),r.getString("product_name"),r.getInt("category_id"),r.getString("category_name"),r.getBigDecimal("price"),r.getBigDecimal("cost_price"),r.getInt("stock"),r.getInt("min_stock"),r.getDate("expiry_date")==null?null:r.getDate("expiry_date").toLocalDate(),r.getString("product_condition"));}
    public int add(Product p)throws SQLException{
        String sql="INSERT INTO products(product_name,category_id,price,cost_price,stock,min_stock,expiry_date,product_condition) VALUES(?,?,?,?,?,?,?,?)";
        try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){bind(s,p);s.executeUpdate();try(ResultSet r=s.getGeneratedKeys()){r.next();return r.getInt(1);}}
    }
    private void bind(PreparedStatement s,Product p)throws SQLException{s.setString(1,p.getProductName());s.setInt(2,p.getCategoryId());s.setBigDecimal(3,p.getPrice());s.setBigDecimal(4,p.getCostPrice());s.setInt(5,p.getStock());s.setInt(6,p.getMinStock());if(p.getExpiryDate()==null)s.setNull(7,Types.DATE);else s.setDate(7,java.sql.Date.valueOf(p.getExpiryDate()));s.setString(8,p.getProductCondition());}
    public List<Product> search(String query)throws SQLException{
        String sql=SELECT+" WHERE CAST(p.product_id AS CHAR)=? OR p.product_name LIKE ? OR c.category_name LIKE ? ORDER BY p.product_name";List<Product> out=new ArrayList<>();
        try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){String q=query==null?"":query.trim();s.setString(1,q);s.setString(2,"%"+q+"%");s.setString(3,"%"+q+"%");try(ResultSet r=s.executeQuery()){while(r.next())out.add(map(r));}}return out;
    }
    public Optional<Product> find(int id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement(SELECT+" WHERE p.product_id=?")){s.setInt(1,id);try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}}
    public boolean update(int id,Product p)throws SQLException{String sql="UPDATE products SET product_name=?,category_id=?,price=?,cost_price=?,stock=?,min_stock=?,expiry_date=?,product_condition=? WHERE product_id=?";try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){bind(s,p);s.setInt(9,id);return s.executeUpdate()==1;}}
    public boolean delete(int id)throws SQLException{try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement("DELETE FROM products WHERE product_id=?")){s.setInt(1,id);return s.executeUpdate()==1;}
}
}

