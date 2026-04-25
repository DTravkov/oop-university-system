package model.domain;

import model.enumeration.TeacherType;
import utils.FieldValidator;

public class Teacher extends Employee {
	
	private static final long serialVersionUID = 1L;
	private TeacherType type;
	
	public Teacher(String login, String password, String name, String surname, TeacherType type) {
		FieldValidator.requireNonNull(type, "Teacher type");
		super(login, password, name, surname);
		this.type = type;
	}
	
	public boolean isLecturer() {
		return this.type == TeacherType.LECTURE || this.type == TeacherType.BOTH;
	}
	public boolean isPractice() {
		return this.type == TeacherType.PRACTICE || this.type == TeacherType.BOTH;
	}
	
	public TeacherType getType() {
		return type;
	}

	public void setType(TeacherType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return super.toString().replace("]", ", type="+getType()+"]");
	}

}