package model;

import java.util.*;


public class Student extends User {
	
	private static final long serialVersionUID = 1L;
    private final Date admissionDate;
    private int currentCredits;
    private int failedDisciplines;
    private boolean isPhdStudent = false;

    public Student(String login, String password, String name, String surname) {
		super(login, password, name, surname);
		this.currentCredits = 0;
		this.admissionDate = new Date();
		this.failedDisciplines = 0;
	}

	public int getCurrentCredits() {
		return currentCredits;
	}

	public void setCurrentCredits(int currentCredits) {
		this.currentCredits = currentCredits;
	}

	public int getFailedDisciplines() {
		return failedDisciplines;
	}

	public void setFailedDisciplines(int failedDisciplines) {
		this.failedDisciplines = failedDisciplines;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}
	
	public boolean isPhdStudent() {
		return isPhdStudent;
	}

	public void setPhdStudent(boolean isPhdStudent) {
		this.isPhdStudent = isPhdStudent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(admissionDate, currentCredits, failedDisciplines);
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
		Student other = (Student) obj;
		return Objects.equals(admissionDate, other.admissionDate) && currentCredits == other.currentCredits
				&& failedDisciplines == other.failedDisciplines;
	}

	
	

	
    
	
    
    

}