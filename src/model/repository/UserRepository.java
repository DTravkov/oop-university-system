package model.repository;


import java.util.Collection;
import java.util.Optional;

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
        return this.data.values().stream()
                .filter(entity -> entity.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }

    public boolean existsByLogin(String login) {
        return findByLogin(login) != null;
    }

    public Collection<User> findAllByRole(UserRole role) {
        return this.data.values().stream()
                .filter(role::matches)
                .toList();
    }



}
