package model.domain.users;

public class Admin extends Employee {

	private static final long serialVersionUID = 1L;

	public Admin(String login, String password, String name, String surname) {
		super(login, password, name, surname);
	}
}
