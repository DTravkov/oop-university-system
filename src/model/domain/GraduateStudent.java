package model.domain;

import java.util.*;

import settings.AppSettings;


public class GraduateStudent extends Student {
	
	private static final long serialVersionUID = 1L;

	private int supervisorId = AppSettings.DELETED_USER_ID;

    public GraduateStudent(String login, String password, String name, String surname, Date admissionDate) {
		super(login, password, name, surname, admissionDate);
	}

	public Integer getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}

	public void removeSupervisor() {
		this.supervisorId = AppSettings.DELETED_USER_ID;
	}




	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GraduateStudent student = (GraduateStudent) o;
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