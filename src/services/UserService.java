package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;
import java.util.Collection;

import model.domain.DeletedUser;
import model.domain.User;
import model.enumeration.UserRole;
import model.repository.UserRepository;
import services.events.UserDeletedEvent;


public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(UserRepository.getInstance());
        registerDeletedUser();
    }

    public void create(User user) {
        if(repository.existsByLogin(user.getLogin())){
            throw new AlreadyExists(" user with login " + user.getLogin());
        }
        repository.save(user);
    }

    public void delete(int id) {
        if(!repository.exists(id)){
            throw new DoesNotExist(" user with id " + id);
        }
        this.eventSystem.publish(new UserDeletedEvent(this.get(id)));
        repository.delete(id);
    }


    public User authenticate(String login, String password) {
        User user = repository.findByLogin(login);

        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidCredentials();
        }

        return user;
    }

    public Collection<User> getAllByRole(UserRole role) {
        return repository.findAllByRole(role);
    }

    private void registerDeletedUser(){
        repository.save(new DeletedUser());
    }

}
