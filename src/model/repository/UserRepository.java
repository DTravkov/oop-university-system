package model.repository;

import model.domain.User;

public class UserRepository extends BaseRepository<User> {
    @Override
    protected String getFilePath() {
        return "users.ser";
    }
    
    public User getByLogin(String login){
    	User user = data.stream().filter(u -> u.getLogin().equals(login)).findFirst().orElse(null);
        return user;
    }
}