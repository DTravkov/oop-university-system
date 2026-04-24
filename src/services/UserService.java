package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;
import java.util.Collection;
import java.util.Date;

import model.domain.DeletedUser;
import model.domain.User;
import model.enumeration.TeacherType;
import model.enumeration.UserRole;
import model.factories.UserFactory;
import model.repository.UserRepository;
import services.events.UserDeletedEvent;


public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(UserRepository.getInstance());
        registerDeletedUser();
        // we need to register the deleted user as a placholder for deleted users.
    }


    public User create(UserRole role, String login, String password, String name, String surname, Date admissionDate, TeacherType teacherType) {
        if(repository.existsByLogin(login)){
            throw new AlreadyExists(" user with login " + login);
        }
        User user = UserFactory.create(role, login, password, name, surname, admissionDate, teacherType);
        return repository.save(user);
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
