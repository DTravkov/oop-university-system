package model.domain;

import java.util.*;

import utils.FieldValidator;


public class Student extends User {
	
	private static final long serialVersionUID = 1L;

    protected Date admissionDate;

    public Student(String login, String password, String name, String surname, Date admissionDate) {
		super(login, password, name, surname);
		FieldValidator.requireNonNull(admissionDate);
		this.admissionDate = admissionDate;
	}


	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	@Override
	public String asLine() {
		return super.asLine() + "| " + getClass().getSimpleName();
	}

	@Override
	public String asTable() {
		return super.asTable();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}


