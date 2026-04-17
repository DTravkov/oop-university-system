package services;

import model.Student;


public class StudentService{
	
	private final UserService userService;
	
    public StudentService() {
        this(new UserService());
    }
    public StudentService(UserService userService) {
        this.userService = userService;
    }

    public void createStudent(Student student) {
        userService.createUser(student);
    }

    public void updateStudent(Student oldStudent, Student updatedStudent) {
        userService.updateUser(oldStudent, updatedStudent);
    }

    public void deleteStudent(String login) {
        userService.deleteUser(login);
    }
    
    

}
    
    
