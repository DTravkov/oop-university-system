package model.enumeration;

import model.domain.Admin;
import model.domain.Manager;
import model.domain.Student;
import model.domain.Teacher;
import model.domain.TechSupportSpecialist;
import model.domain.User;

public enum UserRole {
    USER(User.class),
    STUDENT(Student.class),
    TEACHER(Teacher.class),
    ADMIN(Admin.class),
    MANAGER(Manager.class),
    TECH_SUPPORT_SPECIALIST(TechSupportSpecialist.class);

    private final Class<? extends User> userClass;

    UserRole(Class<? extends User> userClass) {
        this.userClass = userClass;
    }

    public boolean matches(User user) {
        if (this == USER) {
            return user.getClass().equals(User.class);
        }
        return userClass.isInstance(user);
    }

    public static UserRole fromUser(User user) {
        if (user.getClass().equals(User.class)) {
            return USER;
        }
        if (user instanceof Student) {
            return STUDENT;
        }
        if (user instanceof Teacher) {
            return TEACHER;
        }
        if (user instanceof Admin) {
            return ADMIN;
        }
        if (user instanceof Manager) {
            return MANAGER;
        }
        if (user instanceof TechSupportSpecialist) {
            return TECH_SUPPORT_SPECIALIST;
        }
        throw new IllegalArgumentException("Unsupported user type: " + user.getClass().getSimpleName());
    }

    public static boolean isValidRole(String raw) {
        if (raw == null) return false;
        try {
            UserRole.valueOf(raw.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
