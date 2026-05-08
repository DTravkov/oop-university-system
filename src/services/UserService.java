package services;

import exceptions.AlreadyExists;
import exceptions.InvalidCredentials;
import java.util.Date;
import java.util.List;

import model.domain.User;
import model.dto.UserDTO;
import model.enumeration.TeacherType;
import model.factories.UserFactory;
import model.repository.UserRepository;
import services.events.UserCreateEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Logger;

public class UserService extends BaseService<User, UserRepository> {

    public UserService() {
        super(UserRepository.getInstance());

    }

    static {
        initializeSystemUsers();
    }


    public User registerUser(Class<? extends User> userClass, String login, String password, String name, String surname, Date admissionDate, TeacherType teacherType) {
        if(repository.existsByLogin(login)){
            throw new AlreadyExists(" user with login " + login);
        }
        User user = UserFactory.createFromClass(userClass, login, password, name, surname, admissionDate, teacherType);
        User savedUser = super.create(user);
        this.eventSystem.publish(new UserCreateEvent(savedUser));
        return savedUser;
    }
    
    @Override
    public void delete(int id) {
        User userToDelete = this.get(id);
        this.eventSystem.publish(new UserDeleteEvent(userToDelete));
        super.delete(id);
        if(AppSettings.getActiveUser().getId() == userToDelete.getId()){
            AppSettings.clearActiveUser();
        }
    }

    public User authenticate(String login, String password) {
        User user = repository.findByLogin(login);

        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidCredentials();
        }

        AppSettings.setActiveUser(user);
        Logger.log("Logged in id=" + user.getId());

        return user;
    }
    
    @SuppressWarnings("unchecked")
    public List<User> getAllByClass(Class<? extends User> dotClass) {
        return (List<User>) repository.findAllByClass(dotClass);
    }

    @SuppressWarnings("unchecked")
    public List<User> getAllByClassOrSubclass(Class<? extends User> dotClass) {
        return (List<User>) repository.findAllByClassOrSubclass(dotClass);
    }


    public UserDTO getDTO(int userId){
        User user = get(userId);
        return new UserDTO(user);
    }
    
    public UserDTO getDTO(User user){
        return new UserDTO(user);
    }

    private static void initializeSystemUsers(){

        UserRepository userRepository = UserRepository.getInstance();

        User deletedUser = AppSettings.DELETED_USER;
        User systemUser = AppSettings.ANONYMOUS_USER;
        User defaultAdminUser = AppSettings.DEFAULT_ADMIN;

        if(!userRepository.exists(deletedUser.getId())){
            userRepository.save(deletedUser);
        }
        if(!userRepository.exists(systemUser.getId())){
            userRepository.save(systemUser);
        }
        if(!userRepository.existsByLogin("admin")){
            userRepository.save(defaultAdminUser);
        }
    }

}
