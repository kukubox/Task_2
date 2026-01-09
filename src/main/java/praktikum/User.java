package praktikum;

public class User {
    private final String email;
    private final String password;
    private final String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User accountForCreateUser() {
        return new User("ShalimovTest" + System.currentTimeMillis() + "@yandex.rus", "11111111", "Shalimov" );
    }

    public static  User editAccountUser() {
        return new User("ShalimovTest" + System.currentTimeMillis() + "@yandex.rus", "11111111" + System.currentTimeMillis(), "Shalimov" + System.currentTimeMillis());
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}