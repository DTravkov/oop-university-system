package model.enumeration;

import model.domain.Admin;
import model.domain.Dean;
import model.domain.DeletedUser;
import model.domain.Employee;
import model.domain.GraduateStudent;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;

public enum UserRole {
    USER(User.class),
    DELETED_USER(DeletedUser.class),
    STUDENT(Student.class),
    GRADUATE_STUDENT(GraduateStudent.class),
    EMPLOYEE(Employee.class),
    TEACHER(Teacher.class),
    DEAN(Dean.class),
    ADMIN(Admin.class),
    MANAGER(Manager.class),
    TECH_SUPPORT_SPECIALIST(TechSupportSpecialist.class);

    private final Class<? extends User> userClass;

    UserRole(Class<? extends User> userClass) {
        this.userClass = userClass;
    }

    
    public static UserRole fromUser(User user) {
        if (user.getClass().equals(User.class)) {
            return USER;
        }
        if (user.getClass().equals(Employee.class)) {
            return EMPLOYEE;
        }
        if (user.getClass().equals(DeletedUser.class)) {
            return DELETED_USER;
        }
        if (user.getClass().equals(Student.class)) {
            return STUDENT;
        }
        if (user.getClass().equals(GraduateStudent.class)) {
            return GRADUATE_STUDENT;
        }
        if (user.getClass().equals(Teacher.class)) {
            return TEACHER;
        }
        if (user.getClass().equals(Admin.class)) {
            return ADMIN;
        }
        if (user.getClass().equals(Manager.class)) {
            return MANAGER;
        }
        if (user.getClass().equals(Dean.class)) {
            return DEAN;
        }
        if (user.getClass().equals(TechSupportSpecialist.class)) {
            return TECH_SUPPORT_SPECIALIST;
        }
        throw new IllegalArgumentException("Unknown user type " + user.getClass().getSimpleName());
    }

}
