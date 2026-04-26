package model.factories;

import java.util.Date;

import exceptions.FieldNullException;
import model.domain.*;
import model.enumeration.TeacherType;
import model.enumeration.UserRole;

public class UserFactory {

    private UserFactory() {
    }

    public static User createFromClass(
            Class<? extends User> userClass,
            String login,
            String password,
            String name,
            String surname,
            Date admissionDate,
            TeacherType teacherType
    ) {
        if (userClass == User.class) {
            return new User(login, password, name, surname);
        }
        if (userClass == Student.class) {
            if (admissionDate == null) {
                throw new FieldNullException("Date of admission");
            }
            return new Student(login, password, name, surname, admissionDate);
        }
        if (userClass == GraduateStudent.class) {
            if (admissionDate == null) {
                throw new FieldNullException("Date of admission");
            }
            return new GraduateStudent(login, password, name, surname, admissionDate);
        }
        if (userClass == Teacher.class) {
            if (teacherType == null) {
                throw new FieldNullException("Teacher type");
            }
            return new Teacher(login, password, name, surname, teacherType);
        }
        if (userClass == Admin.class) {
            return new Admin(login, password, name, surname);
        }
        if (userClass == Manager.class) {
            return new Manager(login, password, name, surname);
        }
        if (userClass == TechSupportSpecialist.class) {
            return new TechSupportSpecialist(login, password, name, surname);
        }
        if (userClass == Dean.class) {
            return new Dean(login, password, name, surname);
        }
        if (userClass == Employee.class) {
            throw new IllegalArgumentException("Unsupported user class: Employee is abstract");
        }
        if (userClass == DeletedUser.class) {
            throw new IllegalArgumentException("Unsupported user class: DeletedUser is a system placeholder");
        }
        throw new IllegalArgumentException("Unsupported user class: " + userClass.getSimpleName());
    }

    public static User createFromRole(
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
            case GRADUATE_STUDENT:
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
