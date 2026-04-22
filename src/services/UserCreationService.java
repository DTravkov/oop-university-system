package services;

import model.domain.User;
import model.dto.CreateUserRequest;
import model.enumeration.UserRole;
import model.factory.UserFactory;
import utils.FieldValidator;

public class UserCreationService {

    private final UserService userService;

    public UserCreationService(UserService userService) {
        this.userService = userService;
    }

    public User create(CreateUserRequest request) {
        validateRequest(request);

        User user = UserFactory.create(
                request.getRole(),
                request.getLogin(),
                request.getPassword(),
                request.getName(),
                request.getSurname(),
                request.getAdmissionDate(),
                request.getTeacherType()
        );

        userService.create(user);
        return user;
    }

    private void validateRequest(CreateUserRequest request) {
        FieldValidator validator = new FieldValidator()
                .requireNonNull(request.getRole(), "Role")
                .requireNonBlank(request.getLogin(), "Login")
                .requireNonBlank(request.getPassword(), "Password")
                .requireNonBlank(request.getName(), "Name")
                .requireNonBlank(request.getSurname(), "Surname");

        if (request.getRole() == UserRole.STUDENT) {
            validator.requireNonNull(request.getAdmissionDate(), "Admission date");
        }
        if (request.getRole() == UserRole.TEACHER) {
            validator.requireNonNull(request.getTeacherType(), "Teacher type");
        }

        validator.validate();
    }
}
