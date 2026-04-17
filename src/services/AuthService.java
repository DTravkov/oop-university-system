package services;

import exceptions.InvalidCredentials;
import model.User;
import model.UserRepository;
import utils.PasswordUtils;

public class AuthService extends BaseService<User, UserRepository> {
	
	protected UserService userService;
	
	public AuthService() {
		super(new UserRepository());
		this.userService = new UserService();
	}
	public AuthService(UserRepository repository, UserService userService) {
		super(repository);
	}
    
   
    public void registerUser(User user) {
    	this.userService.createUser(user);
    }
    
    public User authenticate(String login, String password) {
        User user = this.userService.findOrThrow(login);
        String incomingPassword = PasswordUtils.hashPassword(password);
        if (!user.getPassword().equals(incomingPassword)) {
            throw new InvalidCredentials("Invalid credentials.");
        }
        return user;
    }
}