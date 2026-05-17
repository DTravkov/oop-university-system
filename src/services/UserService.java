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

/**
 * UserService is a concrete service. It implements logic for user accounts: registration rules, login, ban, lookup by login or role,
 * soft delete with events, and seeding default system users when the app starts.
 */
public class UserService extends GenericService<User> {

    public UserService() {
        super(User.class);
        
    }

    {
        // Register system basic users (anonymous, deleted, default adming etc).
        for (User user : AppSettings.DEFAULT_SYSTEM_USERS) {
            if(!existsByLogin(user.getLogin()))
                repository.save(user);
        }
    }

    // CREATE / UPDATE / DELETE (AND RELATED COMMANDS)

    @Override
    public User create(User user) {
        if(existsByLogin(user.getLogin())){
            throw new AlreadyExists("User with login=" + user.getLogin());
        }
        User savedUser = super.create(user);
        this.eventSystem.publish(new UserCreateEvent(savedUser));
        return savedUser;
    }

    @Override
    public void delete(User user) {
        user.markAsDeleted();
        repository.save(user);
        eventSystem.publish(new UserDeleteEvent(user));
    }

    public User ban(User user, Admin admin) {
        if(user.equals(admin)){
            throw new OperationNotAllowed("banning yourself");
        }
        user.setBanned(true);
        update(user);
        Logger.log("Ban " + baseName + "(" + user.getId() + ")");
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
        throw new DoesNotExist(baseName + " with login=" + login);
    }

    public boolean existsByLogin(String login){
        return find(u -> u.getLogin().equals(login)) != null;
    }


}
