package model.domain;

public class Manager extends Employee {

    private static final long serialVersionUID = 1L;

    public Manager(String login, String password, String name, String surname) {
        super(login, password, name, surname);
    }

    @Override
    public String asLine() {
        return super.asLine() + " | Manager";
    }

    @Override
    public String asTable() {
        return "Role: Manager\n" + super.asTable();
    }
}
