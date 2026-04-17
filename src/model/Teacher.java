package model;

import java.util.Objects;

public class Teacher extends Employee {
	
	private static final long serialVersionUID = 1L;
	private TeacherTypeEnum type;
	private boolean isLecturer;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(type);
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
		Teacher other = (Teacher) obj;
		return type == other.type;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	
	
	

	
    

}