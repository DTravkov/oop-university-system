package model.domain;

import model.enumeration.TeacherTypeEnum;

import java.util.Objects;

public class Teacher extends Employee {
	
	private static final long serialVersionUID = 1L;
	private TeacherTypeEnum type;
	
	public Teacher(String login, String password, String name, String surname, TeacherTypeEnum type) {
		super(login, password, name, surname);
		this.type = type;
	}
	
	public boolean isLecturer() {
		return this.type == TeacherTypeEnum.LECTURE || this.type == TeacherTypeEnum.BOTH;
	}
	public boolean isPractice() {
		return this.type == TeacherTypeEnum.PRACTICE || this.type == TeacherTypeEnum.BOTH;
	}
	
	public TeacherTypeEnum getType() {
		return type;
	}
	public void setType(TeacherTypeEnum type) {
		this.type = type;
	}

}