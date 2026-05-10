package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import exceptions.InvalidCredentials;

import java.util.List;
import java.util.Objects;

import model.domain.User;
import services.events.UserCreateEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Logger;


public class UserService extends BaseService<User> {

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
        super.delete(user);
        eventSystem.publish(new UserDeleteEvent(user));
    }

    public User ban(User user) {
        user.setBanned(true);
        repository.save(user);
        Logger.log("Ban " + baseName + "(" + user + ")");
        return user;
    }



    public User authenticate(String login, String password) {
        User user = getByLogin(login);

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentials();
        }

        AppSettings.setActiveUser(user);
        Logger.log("Logged in as " + baseName + "(" + user + ")");
        return user;
    }



    public <U extends User> U get(int userId, Class<U> className){
        return (U) getAllByClass(className).stream()
                                        .filter(u -> u.getId() == userId)
                                        .findFirst()
                                        .orElseThrow(()-> new DoesNotExist(className.getSimpleName() + " with id=" + userId));
    }
    
    @SuppressWarnings("unchecked")
    public <U extends User> List<U> getAllByClass(Class<U> className){
        return (List<U>) repository.getAll()
                        .stream()
                        .filter(u -> className.isAssignableFrom(u.getClass()))
                        .toList();
    }

    public User getByLogin(String login){
        for(User user : repository.getAll()){
            if(Objects.equals(user.getLogin(), login)){
                return user;
            }
        }
        throw new DoesNotExist(baseName + " with login=" + login);
    }

    public boolean existsByLogin(String login){
        for(User user : repository.getAll()){
            if(Objects.equals(user.getLogin(), login)){
                return true;
            }
        }
        return false;
    }


}
