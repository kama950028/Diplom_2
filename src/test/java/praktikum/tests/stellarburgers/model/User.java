package praktikum.tests.stellarburgers.model;


public class User {
    public String email;
    public String password;
    public String name;

    public User(String email, String password, String name) {
        this.email = email; this.password = password; this.name = name;
    }

    public static User of(String email, String password, String name) {
        return new User(email, password, name);
    }
}

