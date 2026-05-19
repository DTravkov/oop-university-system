package model.domain;

public class TechSupportSpecialist extends Employee {

    private static final long serialVersionUID = 1L;

    public TechSupportSpecialist(String login, String password, String name, String surname) {
        super(login, password, name, surname);
    }

    @Override
    public String asLine() {
        return super.asLine();
    }

    @Override
    public String asTable() {
        return "Role: TechSupportSpecialist\n" + super.asTable();
    }
}
