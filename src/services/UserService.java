package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;
import model.User;
import model.UserRepository;
import utils.PasswordUtils;

public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(new UserRepository());
    }

    public void createUser(User user) {
        ensureNotExists(user.getLogin());
        user.setPassword(hash(user.getPassword()));
        repository.add(user);
    }

    public void updateUser(User oldUser, User updatedUser) {
        findOrThrow(oldUser.getLogin());
        updatedUser.setPassword(hash(updatedUser.getPassword()));
        repository.update(oldUser, updatedUser);
    }

    public void deleteUser(String login) {
        User user = findOrThrow(login);
        repository.delete(user);
    }

    public User authenticate(String login, String password) {
        User user = this.findOrThrow(login);
        String incomingPassword = PasswordUtils.hashPassword(password);
        if (!user.getPassword().equals(incomingPassword)) {
            throw new InvalidCredentials(" for login '" + login + "'");
        }
        return user;
    }

    public User findOrThrow(String login) {
        User user = repository.getByLogin(login);
        if (user == null) {
            throw new DoesNotExist("User with login '" + login + "'");
        }
        return user;
    }
    
    private void ensureNotExists(String login) {
        if (repository.getByLogin(login) != null) {
            throw new AlreadyExists("User with login '" + login + "'");
        }
    }

    private String hash(String password) {
        return PasswordUtils.hashPassword(password);
    }
}
