package services;

import exceptions.AlreadyExists;
import exceptions.InvalidCredentials;
import java.util.Date;
import java.util.List;

import model.domain.DeletedUser;
import model.domain.User;
import model.enumeration.TeacherType;
import model.factories.UserFactory;
import model.repository.UserRepository;
import services.events.UserDeleteEvent;


public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(UserRepository.getInstance());
        // we need to register the deleted user object as a placholder for deleted users.
        if(!repository.exists(DeletedUser.ID)){
            registerDeletedUser();
        }
    }


    public User registerUser(Class<? extends User> userClass, String login, String password, String name, String surname, Date admissionDate, TeacherType teacherType) {
        if(repository.existsByLogin(login)){
            throw new AlreadyExists(" user with login " + login);
        }
        User user = UserFactory.createFromClass(userClass, login, password, name, surname, admissionDate, teacherType);
        return repository.save(user);
    }

    @Override
    public void delete(int id) {
        User userToDelete = this.get(id);
        this.eventSystem.publish(new UserDeleteEvent(userToDelete));
        repository.delete(id);
    }


    public User authenticate(String login, String password) {
        User user = repository.findByLogin(login);

        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidCredentials();
        }

        return user;
    }
    
    public List<User> getAllByClass(Class<? extends User> dotClass) {
        return repository.findAllByClass(dotClass);
    }

    private void registerDeletedUser(){
        repository.save(new DeletedUser());
    }

}
