package model.domain;

import model.domain.users.Employee;

public class TechSupportSpecialist extends Employee {

    private static final long serialVersionUID = 1L;

    public TechSupportSpecialist(String login, String password, String name, String surname) {
        super(login, password, name, surname);
    }
}
