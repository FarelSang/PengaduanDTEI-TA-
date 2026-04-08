package model;

public class UserModel extends AbstractUser {
    private String username;

    public UserModel(String username) {
        this.username = username;
    }

    @Override
    public String getInfo() {
        return "User: " + username;
    }
}