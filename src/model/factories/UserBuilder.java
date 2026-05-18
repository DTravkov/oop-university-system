package model.factories;

import java.util.Date;

import exceptions.OperationNotAllowed;
import model.domain.Dean;
import model.domain.GraduateStudent;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.enumeration.TeacherType;
import settings.AppSettings;
import utils.UIText;


public class UserBuilder {

    public Class<?> userClass;
    public String login;
    public String password;
    public String name;
    public String surname;
    public Date admissionDate;
    public TeacherType teacherType;

    public UserBuilder userClass(Class<?> userClass) {
        this.userClass = userClass;
        return this;
    }

    public UserBuilder login(String login) {
        this.login = login;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder surname(String surname) {
        this.surname = surname;
        return this;
    }

    public UserBuilder admissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
        return this;
    }

    public UserBuilder teacherType(TeacherType teacherType) {
        this.teacherType = teacherType;
        return this;
    }

    public User build() {

        if (userClass == null)
            throw new OperationNotAllowed(UIText.ERR_USER_BUILDER_NO_CLASS);
        if (!AppSettings.REGISTRABLE_USER_CLASSES.contains(userClass))
            throw new OperationNotAllowed(UIText.ERR_USER_BUILDER_CLASS_NOT_REGISTRABLE);
        if (userClass.equals(Teacher.class) && teacherType == null)
            throw new OperationNotAllowed(UIText.ERR_USER_BUILDER_NO_TEACHER_TYPE);
        if (Student.class.isAssignableFrom(userClass) && admissionDate == null)
            throw new OperationNotAllowed(UIText.ERR_USER_BUILDER_NO_ADMISSION_DATE);

        if (userClass == Student.class) return new Student(login, password, name, surname, admissionDate);
        if (userClass == GraduateStudent.class) return new GraduateStudent(login, password, name, surname, admissionDate);
        if (userClass == Teacher.class) return new Teacher(login, password, name, surname, teacherType);
        if (userClass == Dean.class) return new Dean(login, password, name, surname);
        if (userClass == Manager.class) return new Manager(login, password, name, surname);
        if (userClass == TechSupportSpecialist.class) return new TechSupportSpecialist(login, password, name, surname);

        throw new OperationNotAllowed(UIText.ERR_USER_BUILDER_UNSUPPORTED_CLASS);
    }
}