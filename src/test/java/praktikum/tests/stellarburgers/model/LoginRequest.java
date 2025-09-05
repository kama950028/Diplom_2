package praktikum.tests.stellarburgers.model;


public class LoginRequest {
    public String email;
    public String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginRequest of(String email, String password) {
        return new LoginRequest(email, password);
    }
}

