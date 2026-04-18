package services;

import model.domain.TechSupportSpecialist;

public class TechSupportSpecialistService {

    private final UserService userService;

    public TechSupportSpecialistService() {
        this(new UserService());
    }

    public TechSupportSpecialistService(UserService userService) {
        this.userService = userService;
    }

    public void createTechSupportSpecialist(TechSupportSpecialist techSupportSpecialist) {
        userService.createUser(techSupportSpecialist);
    }

    public void updateTechSupportSpecialist(
        TechSupportSpecialist oldTechSupportSpecialist,
        TechSupportSpecialist updatedTechSupportSpecialist
    ) {
        userService.updateUser(oldTechSupportSpecialist, updatedTechSupportSpecialist);
    }

    public void deleteTechSupportSpecialist(String login) {
        userService.deleteUser(login);
    }
}
