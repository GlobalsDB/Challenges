package com.uosipa.globalsdb.dao;

import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.User;

public class UserDao {
    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    private UserDao() {
        throw new AssertionError();
    }

    public User findUser(String login) {
        String password = Database.read(login);

        if (password == null) {
            return null;
        }

        return new User(login, password);
    }

    public boolean isUserExist(String login) {
        return Database.isNodeExist(login);
    }

    public void addUser(User user) {
        Database.write(user.getLogin(), user.getPassword());
    }
}
