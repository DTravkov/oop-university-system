package model.repository;

import java.util.List;

import model.domain.*;
import model.enumeration.UserRole;

public class UserRepository extends Repository<User> {

    private static final UserRepository INSTANCE = new UserRepository();

    private UserRepository() {
        super();
    }

    public static UserRepository getInstance() {
        return INSTANCE;
    }

    public User findByLogin(String login){
        return this.findFirst(user -> user.getLogin().equals(login))
                                                     .orElse(null);
    }

    public boolean existsByLogin(String login) {
        return this.exists(user -> user.getLogin().equals(login));
    }

    public List<User> findAllByRole(UserRole role) {
        return this.findAll(user -> UserRole.fromUser(user).equals(role));
    }

}
