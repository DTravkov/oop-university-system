package model.domain;

import java.util.*;


public class GraduateStudent extends Student {
	
	private static final long serialVersionUID = 1L;

    public GraduateStudent(String login, String password, String name, String surname, Date admissionDate) {
		super(login, password, name, surname, admissionDate);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		Student student = (Student) o;
		return Objects.equals(admissionDate, student.admissionDate);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}