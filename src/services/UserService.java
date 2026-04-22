package services;

import exceptions.AlreadyExists;
import exceptions.InvalidCredentials;
import java.util.Collection;
import model.domain.User;
import model.enumeration.UserRole;
import model.repository.UserRepository;


public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(UserRepository.getInstance());
    }

    public void create(User user) {
        if(repository.existsByLogin(user.getLogin())){
            throw new AlreadyExists("user with login " + user.getLogin());
        }
        repository.save(user);
    }

    public void delete(int id) {
        if(!repository.exists(id)){
            throw new AlreadyExists("user with login " + id);
        }
        repository.delete(id);
    }


    public User authenticate(String login, String password) {
        User user = repository.findByLogin(login);

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentials(" for login '" + login + "'");
        }

        return user;
    }

    public Collection<User> getAllByRole(UserRole role) {
        return repository.findAllByRole(role);
    }

}
