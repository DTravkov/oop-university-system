package model.factory;

import java.util.Date;

import model.domain.Admin;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.TeacherTypeEnum;
import model.enumeration.UserRole;

public class UserFactory {

    private UserFactory() {
    }

    public static User create(UserRole role, String login, String password, String name, String surname) {
        return create(role, login, password, name, surname, null, null);
    }

    public static User createStudent(
            String login,
            String password,
            String name,
            String surname,
            Date admissionDate
    ) {
        return create(UserRole.STUDENT, login, password, name, surname, admissionDate, null);
    }

    public static User createTeacher(
            String login,
            String password,
            String name,
            String surname,
            TeacherTypeEnum teacherType
    ) {
        return create(UserRole.TEACHER, login, password, name, surname, null, teacherType);
    }

    public static User create(
            UserRole role,
            String login,
            String password,
            String name,
            String surname,
            Date admissionDate,
            TeacherTypeEnum teacherType
    ) {
        switch (role) {
            case USER:
                return new User(login, password, name, surname);
            case STUDENT:
                if (admissionDate == null) {
                    throw new IllegalArgumentException("Admission date is required for STUDENT");
                }
                return new Student(login, password, name, surname, admissionDate);
            case TEACHER:
                if (teacherType == null) {
                    throw new IllegalArgumentException("Teacher type is required for TEACHER");
                }
                return new Teacher(login, password, name, surname, teacherType);
            case ADMIN:
                return new Admin(login, password, name, surname);
            case MANAGER:
                return new Manager(login, password, name, surname);
            case TECH_SUPPORT_SPECIALIST:
                return new TechSupportSpecialist(login, password, name, surname);
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
