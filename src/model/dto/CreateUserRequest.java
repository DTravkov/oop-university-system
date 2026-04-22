package model.dto;

import java.util.Date;

import model.enumeration.TeacherTypeEnum;
import model.enumeration.UserRole;

public class CreateUserRequest {

    private final UserRole role;
    private final String login;
    private final String password;
    private final String name;
    private final String surname;
    private final Date admissionDate;
    private final TeacherTypeEnum teacherType;

    public CreateUserRequest(
            UserRole role,
            String login,
            String password,
            String name,
            String surname,
            Date admissionDate,
            TeacherTypeEnum teacherType
    ) {
        this.role = role;
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.admissionDate = admissionDate;
        this.teacherType = teacherType;
    }

    public UserRole getRole() {
        return role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public TeacherTypeEnum getTeacherType() {
        return teacherType;
    }
}
