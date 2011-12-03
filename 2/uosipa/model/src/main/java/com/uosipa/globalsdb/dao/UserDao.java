package com.uosipa.globalsdb.dao;

import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.User;
import org.apache.commons.codec.digest.DigestUtils;

public class UserDao {
    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    private UserDao() {
        // No operations.
    }

    public User findUser(String login) {
        String password = Database.read(login);

        if (password == null) {
            return null;
        }

        return new User(login, password);
    }

    public User authenticate(String login, String password) {
        User result = findUser(login);
        if (result != null && !result.getPassword().equals(DigestUtils.shaHex(password))) {
            return null;
        } else {
            return result;
        }
    }

    public boolean isUserExist(String login) {
        return Database.isNodeExist(login);
    }

    public void addUser(User user) {
        Database.write(user.getLogin(), DigestUtils.shaHex(user.getPassword()));
    }
}
