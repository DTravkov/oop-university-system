package model.domain;


public abstract class Employee extends User{
	private static final long serialVersionUID = 1L;
	
    public Employee(String login, String password, String name, String surname) {
		super(login, password, name, surname);
	}


	@Override
	public String asLine() {
		return super.asLine() + " | " + this.getClass().getSimpleName();
	}

	@Override
	public String asTable() {
		return super.asTable();
	}

}