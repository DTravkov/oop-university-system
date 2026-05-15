package model.factories;

import java.util.Date;

import exceptions.FieldNullException;
import exceptions.OperationNotAllowed;
import model.domain.*;
import model.enumeration.TeacherType;
import settings.AppSettings;

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
        if(!AppSettings.REGISTRABLE_USER_CLASSES.contains(userClass)){
            throw new OperationNotAllowed("This class can not be registered");
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

        throw new IllegalArgumentException("Unsupported user class: " + userClass.getSimpleName());
    }

}
