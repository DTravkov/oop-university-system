package model.domain;

import java.util.*;

public abstract class Employee extends User {
	private static final long serialVersionUID = 1L;
	private int salary = 0;
	
    public Employee(String login, String password, String name, String surname) {
		super(login, password, name, surname);
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}


}