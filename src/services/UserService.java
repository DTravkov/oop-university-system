package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;
import exceptions.OperationNotAllowed;
import exceptions.UserBannedOrDeleted;

import java.util.List;
import java.util.Objects;

import model.domain.Admin;
import model.domain.User;
import services.events.concrete.UserCreateEvent;
import services.events.concrete.UserDeleteEvent;
import settings.AppSettings;
import utils.Logger;
import utils.UIText;

/**
 * UserService is a concrete service. It implements logic for user accounts: registration rules, login, ban, lookup by login or role,
 * soft delete with events, and seeding default system users when the app starts.
 */
public class UserService extends GenericService<User> {

    public UserService() {
        super(User.class);
        
    }

    {
        // Register system basic users (anonymous, default admin etc).
        for (User user : AppSettings.DEFAULT_SYSTEM_USERS) {
            if(!existsByLogin(user.getLogin()))
                repository.save(user);
        }
    }

    // CREATE / UPDATE / DELETE

    @Override
    public User create(User user) {
        if(existsByLogin(user.getLogin())){
            throw new AlreadyExists(UIText.ERR_USER_LOGIN);
        }
        User savedUser = super.create(user);
        this.eventSystem.publish(new UserCreateEvent(savedUser));
        return savedUser;
    }


    @Override
    public void delete(User user) {
        if(AppSettings.DEFAULT_SYSTEM_LOGINS.contains(user.getLogin())){
            throw new OperationNotAllowed(UIText.ERR_DELETE_SYSTEM_USER);
        }
        user.markAsDeleted();
        repository.save(user);
        eventSystem.publish(new UserDeleteEvent(user));
    }

    /**
     * has additional admin paramter to check that admin is not trying to delete himself
     * @param user
     * @param admin
     * @return
     */
    public User ban(User user, Admin admin) {
        if(AppSettings.DEFAULT_SYSTEM_LOGINS.contains(user.getLogin())){
            throw new OperationNotAllowed(UIText.ERR_BAN_SYSTEM_USER);
        }
        if(user.equals(admin)){
            throw new OperationNotAllowed(UIText.ERR_BAN_SELF);
        }
        user.setBanned(true);
        repository.save(user);
        Logger.log("Ban " + baseName + "(" + user.getId() + ")");
        return user;
    }
    public User unban(User user) {
        user.setBanned(false);
        repository.save(user);
        Logger.log("Unban " + baseName + "(" + user.getId() + ")");
        return user;
    }

    public User authenticate(String login, String password) {
        User user = getUserByLogin(login);

        if(user.isBanned() || user.isDeleted()){
            throw new UserBannedOrDeleted();
        }
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentials();
        }

        AppSettings.setActiveUser(user);
        Logger.log("Logged in as " + baseName + "(" + user.getId() + ")");
        return user;
    }


    // QUERIES

    /**
     * returns the list of users of passed class (subclasses are also included).
     * e.g, if we pass Student.class, we will get GraduateStudent as well.
     * @param <U>
     * @param className
     * @return
     */
    @SuppressWarnings("unchecked")
    public <U extends User> List<U> getUsersByClass(Class<U> className){
        return (List<U>) getAll()
                        .stream()
                        .filter(u -> className.isAssignableFrom(u.getClass()))
                        .toList();
    }

    public User getUserByLogin(String login){
        for(User user : getAll()){
            if(Objects.equals(user.getLogin(), login)){
                return user;
            }
        }
        throw new DoesNotExist(UIText.ERR_USER_LOGIN_NOT_FOUND);
    }

    public boolean existsByLogin(String login){
        return find(u -> u.getLogin().equals(login)) != null;
    }


}
