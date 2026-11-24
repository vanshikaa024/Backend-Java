import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;
import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private final UserDAO dao = new UserDAO();
    public Optional<User> login(String username, String password) throws SQLException {
        if (username==null || username.isBlank() || password==null || password.isEmpty()) return Optional.empty();
        String name=username.trim(); String stored=dao.getPasswordHash(name);
        if(stored==null || !verify(password,stored)) return Optional.empty();
        return dao.authenticate(name);
    }
    private String hash(String value) {
        try {
            byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt);
            byte[] key=derive(value.toCharArray(),salt,210000);
            return "pbkdf2$210000$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(key);
        } catch(Exception e) { throw new IllegalStateException(e); }
    }
    private boolean verify(String value,String encoded) {
        try {
            String[] parts=encoded.split("\\$"); if(parts.length!=4 || !parts[0].equals("pbkdf2")) return false;
            byte[] expected=Base64.getDecoder().decode(parts[3]);
            return java.security.MessageDigest.isEqual(expected,derive(value.toCharArray(),Base64.getDecoder().decode(parts[2]),Integer.parseInt(parts[1])));
        } catch(Exception e) { return false; }
    }
    private byte[] derive(char[] password,byte[] salt,int rounds) throws Exception {
        PBEKeySpec spec=new PBEKeySpec(password,salt,rounds,256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        finally { spec.clearPassword(); }
    }
    public void createUser(String username, String password, String role) throws SQLException {
        if(username==null || username.isBlank() || password==null || password.length()<8) throw new IllegalArgumentException("Username is required and password must be at least 8 characters.");
        dao.create(username.trim(), hash(password), role==null?"STAFF":role);
    }
}
