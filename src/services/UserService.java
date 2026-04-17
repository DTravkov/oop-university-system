package services;

import exceptions.AlreadyExists;
import exceptions.DoesNotExist;
import model.User;
import model.UserRepository;
import utils.PasswordUtils;

public class UserService extends BaseService<User, UserRepository> {

	public UserService() {
		super(new UserRepository());
	}
	public UserService(UserRepository repository, UserService userService) {
		super(repository);
	}
	
    public void createUser(User user) {
    	this.throwIfExists(user.getLogin());
    	user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        this.repository.add(user);
    }
    
    public void updateUser(User old, User updated) {
        this.findOrThrow(old.getLogin());
        updated.setPassword(PasswordUtils.hashPassword(updated.getPassword()));
        this.repository.update(old, updated);
    }

    public void deleteUser(String login) {
        User user = this.findOrThrow(login);
        this.repository.delete(user);
    }
    
    public User findOrThrow(String login) {
        User user = this.repository.getByLogin(login);
        if (user == null) throw new DoesNotExist("User not found.");
        
        return user;
    }
    
    private boolean throwIfExists(String login) {
        User user = this.repository.getByLogin(login);
        if(user != null) throw new AlreadyExists();
        return true;
    }


}