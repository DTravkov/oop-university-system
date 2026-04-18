package services;

import model.domain.Manager;

public class ManagerService {

    private final UserService userService;

    public ManagerService() {
        this(new UserService());
    }

    public ManagerService(UserService userService) {
        this.userService = userService;
    }

    public void createManager(Manager manager) {
        userService.createUser(manager);
    }

    public void updateManager(Manager oldManager, Manager updatedManager) {
        userService.updateUser(oldManager, updatedManager);
    }

    public void deleteManager(String login) {
        userService.deleteUser(login);
    }
}
