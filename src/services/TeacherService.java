package services;

import model.domain.Teacher;

public class TeacherService {

    private final UserService userService;

    public TeacherService() {
        this(new UserService());
    }

    public TeacherService(UserService userService) {
        this.userService = userService;
    }

    public void createTeacher(Teacher teacher) {
        userService.createUser(teacher);
    }

    public void updateTeacher(Teacher oldTeacher, Teacher updatedTeacher) {
        userService.updateUser(oldTeacher, updatedTeacher);
    }

    public void deleteTeacher(String login) {
        userService.deleteUser(login);
    }
}
