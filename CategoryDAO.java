import java.sql.*;
import java.util.*;

import Database.DBConnection;

public class CategoryDAO {
    public int add(String name) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("INSERT INTO categories(category_name) VALUES(?)",Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1,name); p.executeUpdate(); try(ResultSet r=p.getGeneratedKeys()){r.next(); return r.getInt(1);}
        }
    }
    public List<Category> findAll() throws SQLException {
        List<Category> out=new ArrayList<>(); try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("SELECT category_id,category_name FROM categories ORDER BY category_name"); ResultSet r=p.executeQuery()) {
            while(r.next()) out.add(new Category(r.getInt(1),r.getString(2)));
        } return out;
    }
    public Optional<Category> findById(int id) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("SELECT category_id,category_name FROM categories WHERE category_id=?")) {
            p.setInt(1,id); try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(new Category(r.getInt(1),r.getString(2))):Optional.empty();}
        }
    }
    public boolean update(int id,String name) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("UPDATE categories SET category_name=? WHERE category_id=?")){p.setString(1,name);p.setInt(2,id);return p.executeUpdate()==1;}
    }
    public boolean delete(int id) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("DELETE FROM categories WHERE category_id=?")){p.setInt(1,id);return p.executeUpdate()==1;}
    }
}
