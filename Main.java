import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService users=new UserService();
        try(Scanner input=new Scanner(System.in)) {
            System.out.print("Username: "); String username=input.nextLine();
            System.out.print("Password: "); String password=input.nextLine();
            Optional<User> user=users.login(username,password);
            if(user.isEmpty()) { System.out.println("Login failed."); return; }
            System.out.println("Welcome, "+user.get().getUsername()+" ("+user.get().getRole()+")");
            System.out.println("ProductService and CategoryService are ready for authenticated application code.");
        } catch(SQLException e) {
            System.err.println("Could not connect to SmartShelf. Check database settings and schema.");
            e.printStackTrace();
        }
    }
}
