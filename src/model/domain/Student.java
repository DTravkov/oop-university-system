package model.domain;

import java.util.*;

import utils.FieldValidator;


public class Student extends User implements IEnrollable {
	
	private static final long serialVersionUID = 1L;
    protected Date admissionDate;

    public Student(String login, String password, String name, String surname, Date admissionDate) {
		FieldValidator.requireNonNull(admissionDate, "Date of admission");
		super(login, password, name, surname);
		this.admissionDate = admissionDate;
	}


	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		if (id != 0 || student.getId() != 0) {
			return id != 0 && id == student.getId();
		}
		if (!super.equals(o)) return false;
		return Objects.equals(admissionDate, student.admissionDate);
	}

	@Override
	public int hashCode() {
		if (id != 0) {
			return Integer.hashCode(id);
		}
		return Objects.hash(super.hashCode(), admissionDate);
	}

	@Override
	public String toString() {
		return super.toString();
	}
}