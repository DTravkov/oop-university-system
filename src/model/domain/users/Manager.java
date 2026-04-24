package model.domain.users;

public class Manager extends Employee {

    private static final long serialVersionUID = 1L;

    public Manager(String login, String password, String name, String surname) {
        super(login, password, name, surname);
    }
}
