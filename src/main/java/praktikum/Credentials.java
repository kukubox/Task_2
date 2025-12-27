package praktikum;

public class Credentials {
    private final String email;
    private final String password;


    public Credentials(String login, String password) {
        this.email = login;
        this.password = password;
    }

    public static Credentials loginUser(User user) {
        return new Credentials(user.getEmail(), user.getPassword());
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}