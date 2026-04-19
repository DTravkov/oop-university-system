package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;
import model.domain.User;
import model.repository.UserRepository;
import utils.PasswordUtils;

public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(new UserRepository());
    }

    public void createUser(User user) {
        throwIfLoginExists(user.getLogin());
        user.setPassword(hash(user.getPassword()));
        repository.save(user);
    }

    public void updateUser(User oldUser, User updatedUser) {
        get(oldUser.getId());
        updatedUser.setPassword(hash(updatedUser.getPassword()));
        repository.save(updatedUser);
    }

    public void deleteUser(int id) {
        get(id);
        repository.delete(id);
    }

    public User getByLogin(String login) {
        User user = repository.getByLogin(login);
        if (user == null) {
            throw new DoesNotExist("User with login '" + login + "'");
        }
        return user;
    }

    public void deleteUser(String login) {
        User user = getByLogin(login);
        repository.delete(user);
    }

    public User authenticate(String login, String password) {
        User user = getByLogin(login);

        String incomingPassword = PasswordUtils.hashPassword(password);
        if (!user.getPassword().equals(incomingPassword)) {
            throw new InvalidCredentials(" for login '" + login + "'");
        }
        return user;
    }

    public boolean existsByLogin(String login) {
        return repository.getByLogin(login) != null;
    }

    public void throwIfLoginExists(String login) {
        if (existsByLogin(login)) {
            throw new AlreadyExists("User with login '" + login + "' already exists");
        }
    }

    private String hash(String password) {
        return PasswordUtils.hashPassword(password);
    }
}
