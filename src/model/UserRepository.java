package model;

public class UserRepository extends Repository<User> {
    @Override
    protected String getFilePath() {
        return "users.ser";
    }
    
    public User getByLogin(String login){
    	User user = data.stream().filter(u -> u.getLogin().equals(login)).findFirst().orElse(null);
        return user;
    }
}