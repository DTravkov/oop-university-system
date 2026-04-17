package model;

import java.util.*;

public class Employee extends User  {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(salary);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return salary == other.salary;
	}
    
    
	

	


}