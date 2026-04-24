package model.factories;

import java.util.Date;

import exceptions.FieldNullException;
import model.domain.Admin;
import model.domain.Dean;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.TeacherType;
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
            TeacherType teacherType
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
            TeacherType teacherType
    ) {
        switch (role) {
            case USER:
                return new User(login, password, name, surname);
            case STUDENT:
                if (admissionDate == null) {
                    throw new FieldNullException("Date of admission");
                }
                return new Student(login, password, name, surname, admissionDate);
            case TEACHER:
                if (teacherType == null) {
                    throw new FieldNullException("Teacher type");
                }
                return new Teacher(login, password, name, surname, teacherType);
            case ADMIN:
                return new Admin(login, password, name, surname);
            case MANAGER:
                return new Manager(login, password, name, surname);
            case TECH_SUPPORT_SPECIALIST:
                return new TechSupportSpecialist(login, password, name, surname);
            case DEAN:
                return new Dean(login, password, name, surname);
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
