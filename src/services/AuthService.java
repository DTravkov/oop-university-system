package services;

import exceptions.InvalidCredentials;
import model.User;
import utils.PasswordUtils;

public class AuthService {
    private UserService userService = new UserService();
    
   
    
    public void registerUser(User user) {
    	userService.checkNotExist(user.getLogin());
    	user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
    	this.userService.createUser(user);
    }
    
    public User authenticate(String login, String password) {
    	User user = this.userService.getByLogin(login);
        String incomingPassword = PasswordUtils.hashPassword(password);
        if (!user.getPassword().equals(incomingPassword)) {
            throw new InvalidCredentials("Invalid credentials.");
        }

        return user;
    }
}