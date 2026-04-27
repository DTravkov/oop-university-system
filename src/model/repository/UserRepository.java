package model.repository;

import java.util.List;

import model.domain.*;

public class UserRepository extends Repository<User> {

    private static final UserRepository INSTANCE = new UserRepository();

    private UserRepository() {
        super();
    }

    public static UserRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public List<User> findAll() {
        return super.findAll().stream().filter(user -> user.getId() != -1).toList();
    }

    public User findByLogin(String login){
        return this.findFirst(user -> user.getLogin().equals(login))
                                                     .orElse(null);
    }

    public boolean existsByLogin(String login) {
        return this.exists(user -> user.getLogin().equals(login));
    }

    public List<User> findAllByClass(Class<? extends User> dotClass) {
        return this.findAll(user -> user.getClass().equals(dotClass));
    }

    public List<User> findAllByClassOrSubclass(Class<? extends User> dotClass) {
        return this.findAll(user -> dotClass.isAssignableFrom(user.getClass()));
    }

}
