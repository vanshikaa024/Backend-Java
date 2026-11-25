import java.sql.*;
import java.util.Optional;

import Database.DBConnection;

public class UserDAO {
    public Optional<User> authenticate(String username) throws SQLException {
        String sql = "SELECT user_id, username, role, password_hash FROM users WHERE username=? AND status=TRUE";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r=p.executeQuery()) {
                return r.next() ? Optional.of(new User(r.getInt("user_id"), r.getString("username"), r.getString("role"))) : Optional.empty();
            }
        }
    }
    public String getPasswordHash(String username) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("SELECT password_hash FROM users WHERE username=? AND status=TRUE")) {
            p.setString(1,username); try(ResultSet r=p.executeQuery()){ return r.next()?r.getString(1):null; }
        }
    }
    public void create(String username, String encodedPassword, String role) throws SQLException {
        String sql="INSERT INTO users(username,password_hash,role) VALUES(?,?,?)";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,username); p.setString(2,encodedPassword); p.setString(3,role); p.executeUpdate();
        }
    }
}

