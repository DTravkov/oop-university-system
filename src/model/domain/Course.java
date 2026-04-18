package model.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import model.enumeration.CourseType;
import utils.FieldValidator;

public class Course extends SerializableModel{
	
    private static final long serialVersionUID = 1L;

	private String name;
    private String description;
    private int credits;
    private CourseType type;

	public Course(String name, String description, int credits, CourseType type) {
		this.name = name;
		this.description = description;
		this.credits = credits;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public CourseType getType() {
		return type;
	}

	public void setType(CourseType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Course course = (Course) o;
		if(this.id != 0 && course.getId() != 0) return this.id == course.getId();
		return Objects.equals(name, course.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return "Course{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}