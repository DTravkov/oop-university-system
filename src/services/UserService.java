package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.User;
import model.UserRepository;

public class UserService {
    private static UserRepository repo = new UserRepository();

   
    public void createUser(User user) {
        this.repo.add(user);
    }
    
    public void updateUser(User old, User updated) {
        this.getByLogin(old.getLogin());
        this.repo.update(old, updated);
    }

    public void deleteUser(String login) {
        User user = this.getByLogin(login);
        this.repo.delete(user);
    }
    
    public User getByLogin(String login) {
        User user = this.repo.getByLogin(login);
        
        if (user == null) {
            throw new DoesNotExist("User not found.");
        }
        return user;
    }
    
    public boolean checkNotExist(String login) {
        User user = this.repo.getByLogin(login);
        if(user != null) throw new AlreadyExists();
        return true;
    }
    public UserRepository getRepository() {
    	return this.repo;
    }

}