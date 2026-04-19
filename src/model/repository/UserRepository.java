package model.repository;

import java.util.List;
import java.util.stream.Collectors;
import model.domain.User;

public class UserRepository extends Repository<User> {

    public UserRepository() {
        super("users.ser");
    }

    public User getByLogin(String login){
        return this.data.values().stream()
                .filter(entity -> entity.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

}
