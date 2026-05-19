package model.domain;


public class Admin extends Employee {

	private static final long serialVersionUID = 1L;

	public Admin(String login, String password, String name, String surname) {
		super(login, password, name, surname);
	}

	@Override
	public String asLine() {
		return super.asLine();
	}

	@Override
	public String asTable() {
		return "Role: Admin\n" + super.asTable();
	}
}
