package service;

import model.User;

public interface AuthService {
    String login(String username, String password);
    boolean register(User user);
    String generateId();

    User getUser(String username, String password);
}