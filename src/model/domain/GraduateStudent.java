package model.domain;

import java.util.Date;


public class GraduateStudent extends Student {
	
	private static final long serialVersionUID = 1L;

	private Teacher supervisor = null;

    public GraduateStudent(String login, String password, String name, String surname, Date admissionDate) {
		super(login, password, name, surname, admissionDate);
	}

	
	public Teacher getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Teacher supervisor) {
		this.supervisor = supervisor;
	}


	@Override
	public String asLine() {
		return super.asLine() + " | Graduate";
	}

	@Override
	public String asTable() {
		return "Role: Graduate Student\n" + super.asTable();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}